package com.seiko.danmaku.factory

import android.net.Uri
import com.seiko.danmaku.DanmaResultBean
import com.seiko.danmaku.data.api.DownloadApi
import com.seiko.danmaku.data.repo.SmbMd5Repository
import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.domain.GetDanmaResultUseCase
import com.seiko.danmaku.util.getVideoMd5
import com.seiko.danmaku.util.log
import javax.inject.Inject

class DanmaResultWithNet @Inject constructor(
    private val getResult: GetDanmaResultUseCase,
    private val smbMd5Repo: SmbMd5Repository,
    private val downloadApi: DownloadApi
) : IDanmaResult {

    override suspend fun decode(videoUri: Uri, isMatched: Boolean): Result<DanmaResultBean> {
        val url = videoUri.toString()

        // 先从数据去查找是否与此url匹配的MD5，没有则下载数据去获取。
        var videoMd5 = smbMd5Repo.getVideoMd5(url)
        if (!videoMd5.isNullOrEmpty()) {
            log("get videoMd5 with net from db")
            return getResult.invoke(videoMd5, isMatched)
        }

        val start = System.currentTimeMillis()
        log("get videoMd5 with net downloading...")

        // 下载前16mb的数据
        val response = downloadApi.get(url, mapOf())
        val body = response.body()
            ?: return Result.Error(RuntimeException("Response body is NULL"))

        // 获取视频Md5，需要下载16mb资源，有点慢5~18s。
        videoMd5 = body.byteStream().getVideoMd5()
        smbMd5Repo.saveVideoMd5(url, videoMd5)

        log("get videoMd5 with net download finish, 耗时：${System.currentTimeMillis() - start}")

        return getResult.invoke(videoMd5, isMatched)
    }

}