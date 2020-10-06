package com.seiko.danmaku.factory

import android.net.Uri
import com.seiko.danmaku.DanmaResultBean
import com.seiko.danmaku.data.model.Result

interface IDanmaResult {
    /**
     * @param videoUri 资源连接
     * @param isMatched 是否精确匹配
     */
    suspend fun decode(videoUri: Uri, isMatched: Boolean): Result<DanmaResultBean>
}