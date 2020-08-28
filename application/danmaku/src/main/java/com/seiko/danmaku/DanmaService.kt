package com.seiko.danmaku

import org.videolan.libvlc.interfaces.IMedia

interface DanmaService {

    /**
     * 下载弹幕
     */
    suspend fun getDanmaResult(media: IMedia): DanmaResultBean?

    /**
     * 记录smb账号
     */
    suspend fun saveSmbServer(mrl: String, account: String, password: String)

}