package com.seiko.danmaku.factory

import android.net.Uri
import com.seiko.danmaku.DanmaResultBean
import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.domain.GetDanmaResultUseCase
import com.seiko.danmaku.util.getVideoMd5
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class DanmaResultWithFile @Inject constructor(
    private val getResult: GetDanmaResultUseCase
) : IDanmaResult {

    override suspend fun decode(videoUri: Uri, isMatched: Boolean): Result<DanmaResultBean> {

        // 文件路径
        val videoFile = File(videoUri.path!!)

        // 视频是否存在
        if (!videoFile.exists()) {
            return Result.Error(FileNotFoundException("Not found file: $videoFile"))
        }

        // 获取视频Md5
        val videoMd5 = videoFile.inputStream().getVideoMd5()

        return getResult.invoke(videoMd5, isMatched)
    }
}