package com.seiko.danmaku.domain

import com.seiko.danmaku.DanmaResultBean
import com.seiko.danmaku.data.api.DownloadApi
import com.seiko.danmaku.data.repo.SmbMd5Repository
import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.util.getVideoMd5
import javax.inject.Inject

class GetDanmaResultWithNetUseCase @Inject constructor(
    private val getResult: GetDanmaResultUseCase,
    private val smbMd5Repo: SmbMd5Repository,
    private val downloadApi: DownloadApi
) {

    /**
     * @param url 视频连接
     * @param isMatched 是否精确匹配
     */
    suspend operator fun invoke(url: String, isMatched: Boolean): Result<DanmaResultBean> {

        // 先从数据去查找是否与此url匹配的MD5，没有则下载数据去获取。
        var videoMd5 = smbMd5Repo.getVideoMd5(url)
        if (!videoMd5.isNullOrEmpty()) {
//            Timber.tag(DANMA_RESULT_TAG).d("get videoMd5 with net from db")
            return getResult.invoke(videoMd5, isMatched)
        }

//        val start = System.currentTimeMillis()
//        Timber.tag(DANMA_RESULT_TAG).d("get videoMd5 with net downloading...")

        // 下载前16mb的数据
        val response = downloadApi.get(url, mapOf())
        val body = response.body()
            ?: return Result.Error(RuntimeException("Response body is NULL"))

        // 获取视频Md5，需要下载16mb资源，有点慢5~18s。
        videoMd5 = body.byteStream().getVideoMd5()
        smbMd5Repo.saveVideoMd5(url, videoMd5)

//        Timber.tag(DANMA_RESULT_TAG).d("get videoMd5 with net download finish, 耗时：%d",
//            System.currentTimeMillis() - start)

        return getResult.invoke(videoMd5, isMatched)
    }

}