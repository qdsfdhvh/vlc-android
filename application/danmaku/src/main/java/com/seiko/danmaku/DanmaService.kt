package com.seiko.danmaku

import com.seiko.danmaku.data.model.Result
import org.videolan.libvlc.interfaces.IMedia

interface DanmaService {

    /**
     * 下载弹幕
     * @param media 资源
     * @param isMatched 是否精确匹配
     */
    suspend fun getDanmaResult(media: IMedia, isMatched: Boolean = true): Result<DanmaResultBean>

    /**
     * 记录smb账号
     */
    suspend fun saveSmbServer(mrl: String, account: String, password: String)

}