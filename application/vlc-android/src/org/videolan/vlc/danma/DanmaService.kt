package org.videolan.vlc.danma

import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
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

    suspend fun getDanmaResult(media: IMedia): DanmaResultBean?

}