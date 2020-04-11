package org.videolan.vlc.danma

import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.danma.DanmakuEngineOptions
import org.videolan.libvlc.interfaces.IMedia

/**
 * 弹幕下载服务，module-player中实现。
 */
interface DanmaService : IProvider {

    companion object {
        const val PATH = "/danma/service"

        fun get(): DanmaService? {
            return ARouter.getInstance().build(PATH)
                .navigation() as? DanmaService
        }
    }

    /**
     * 弹幕配置
     */
    fun loadDanmaOptions(): DanmakuEngineOptions

    /**
     * 下载弹幕
     */
    suspend fun getDanmaResult(media: IMedia): DanmaResultBean?

    /**
     * 记录smb账号
     */
    suspend fun saveSmbServer(mrl: String, account: String, password: String)
}