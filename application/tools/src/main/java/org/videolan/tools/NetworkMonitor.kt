package org.videolan.tools

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.net.NetworkInterface
import java.net.SocketException

class NetworkMonitor(private val context: Context) : LifecycleObserver {
    private var registered = false
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val connection = ConflatedBroadcastChannel(Connection(connected = false, mobile = true, vpn = false))
    val connectionFlow : Flow<Connection>
        get() = connection.asFlow()
    val connected : Boolean
        get() = connection.value.connected
    val isLan : Boolean
        get() = connection.value.run { connected && !mobile }
    val lanAllowed : Boolean
        get() = connection.value.run { connected && (!mobile || vpn) }
    val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ConnectivityManager.CONNECTIVITY_ACTION -> {
                    val networkInfo = cm.activeNetworkInfo
                    val isConnected = networkInfo != null && networkInfo.isConnected
                    val isMobile = isConnected && networkInfo!!.type == ConnectivityManager.TYPE_MOBILE
                    val isVPN = isConnected && updateVPNStatus()
                    val conn = Connection(isConnected, isMobile, isVPN)
                    if (connection.value != conn) connection.offer(conn)
                }

            }
        }
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this@NetworkMonitor)
    }

    private var callback: ConnectivityManager.NetworkCallback? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        if (registered) return
        registered = true

        // 从弹弹跳转过来无法接收CONNECTIVITY_ACTION，尚未找到原因，暂时使用新网络api。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint("MissingPermission")
            callback = object: ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    val isConnected = true
                    val isMobile =
                        cm.getNetworkCapabilities(network)
                            ?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            ?: false
                    val isVPN =  updateVPNStatus()
                    val conn = Connection(isConnected, isMobile, isVPN)
                    if (connection.value != conn) connection.offer(conn)
                }
            }
            cm.requestNetwork(NetworkRequest.Builder().build(), callback!!)
        } else {
            val networkFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(receiver, networkFilter)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        if (!registered) return
        registered = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (callback != null) {
                cm.unregisterNetworkCallback(callback!!)
                callback = null
            }
        } else {
            try {
                context.unregisterReceiver(receiver)
            } catch (ignored: Exception) {
            }
        }
    }

    protected fun finalize() {
        stop()
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun updateVPNStatus(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (network in cm.allNetworks) {
                val nc = cm.getNetworkCapabilities(network) ?: return false
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) return true
            }
            return false
        } else {
            try {
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement()
                    val name = networkInterface.displayName
                    if (name.startsWith("ppp") || name.startsWith("tun") || name.startsWith("tap"))
                        return true
                }
            } catch (ignored: SocketException) {}
            return false
        }
    }

    companion object : SingletonHolder<NetworkMonitor, Context>({ NetworkMonitor(it.applicationContext) })
}

class Connection(val connected: Boolean, val mobile: Boolean, val vpn: Boolean)
