package com.seiko.danmaku.factory

import android.net.Uri
import com.seiko.danmaku.DanmaResultBean
import com.seiko.danmaku.data.repo.SmbMd5Repository
import com.seiko.danmaku.data.repo.SmbMrlRepository
import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.domain.GetDanmaResultUseCase
import com.seiko.danmaku.util.SmbUtils
import com.seiko.danmaku.util.getVideoMd5
import com.seiko.danmaku.util.log
import java.io.FileNotFoundException
import javax.inject.Inject

class DanmaResultWithSmb @Inject constructor(
    private val getResult: GetDanmaResultUseCase,
    private val smbMd5Repo: SmbMd5Repository,
    private val smbMrlRepo: SmbMrlRepository
) : IDanmaResult {

    override suspend fun decode(videoUri: Uri, isMatched: Boolean): Result<DanmaResultBean> {
        val urlValue = videoUri.toString()

        // 先从数据去查找是否与此url匹配的MD5，没有则连接SMB去获取。
        var videoMd5 = smbMd5Repo.getVideoMd5(urlValue)
        if (!videoMd5.isNullOrEmpty()) {
            log("get videoMd5 with smb from db")
            return getResult.invoke(videoMd5, isMatched)
        }

        // 获取SMB的账号密码
        val smbMrl = smbMrlRepo.getSmbMrl(urlValue, "smb")
            ?: return Result.Error(Exception("Not account and password with $urlValue"))

        val account = smbMrl.account
        val password = smbMrl.password

        // 获取smb路径
        val videoFile = runCatching {
            SmbUtils.getInstance().getFileWithUri(videoUri, account, password)
        }.getOrElse { error ->
            return Result.Error(error as Exception)
        }

        // 视频是否存在
        try {
            if (!videoFile.exists()) {
                return Result.Error(FileNotFoundException("Not found smbFile: $videoFile"))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }

        val start = System.currentTimeMillis()
        log("get videoMd5 with smb...")

        // 获取视频Md5，需要下载16mb资源，很慢16~35s。
        videoMd5 = videoFile.inputStream.getVideoMd5()
        smbMd5Repo.saveVideoMd5(urlValue, videoMd5)

        log("get videoMd5 with smb, 耗时：${System.currentTimeMillis() - start}")

        // 加载弹幕
        return getResult.invoke(videoMd5, isMatched)
    }

}