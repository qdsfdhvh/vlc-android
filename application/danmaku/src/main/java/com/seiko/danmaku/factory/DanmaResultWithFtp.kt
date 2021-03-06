package com.seiko.danmaku.factory

import android.net.Uri
import com.seiko.danmaku.DanmaResultBean
import com.seiko.danmaku.data.repo.SmbMd5Repository
import com.seiko.danmaku.data.repo.SmbMrlRepository
import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.domain.GetDanmaResultUseCase
import com.seiko.danmaku.util.FtpUtils
import com.seiko.danmaku.util.SftpUtils
import com.seiko.danmaku.util.log
import javax.inject.Inject

class DanmaResultWithFtp @Inject constructor(
    private val getResult: GetDanmaResultUseCase,
    private val smbMd5Repo: SmbMd5Repository,
    private val smbMrlRepo: SmbMrlRepository
) : IDanmaResult {

    override suspend fun decode(videoUri: Uri, isMatched: Boolean): Result<DanmaResultBean> {
        val scheme = videoUri.scheme!!
        val urlValue = videoUri.toString()

        // 先从数据去查找是否与此url匹配的MD5，没有则连接SMB去获取。
        var videoMd5 = smbMd5Repo.getVideoMd5(urlValue)
        if (!videoMd5.isNullOrEmpty()) {
            log("get videoMd5 with ftp from db")
            return getResult.invoke(videoMd5, isMatched)
        }

        // 获取ftp的账号密码
        val smbMrl = smbMrlRepo.getSmbMrl(urlValue, scheme)
            ?: return Result.Error(Exception("Not account and password with $urlValue"))

        val account = smbMrl.account
        val password = smbMrl.password

        val start = System.currentTimeMillis()
        log("get videoMd5 with ftp downloading...")

        // 获取ftp资源的md5
        videoMd5 = kotlin.runCatching {
            if (scheme == "sftp") {
                // 很慢25~40s，可能用法不对
                SftpUtils.getVideoMd5WithUri(videoUri, account, password)
            } else {
                // 不错2~8s
                FtpUtils.getVideoMd5WithUri(videoUri, account, password)
            }
        }.getOrElse { error ->
            return Result.Error(error as Exception)
        }
        if (videoMd5.isNullOrEmpty()) {
            return Result.Error(RuntimeException("can not read ftp file md5 with:$videoUri"))
        }
        smbMd5Repo.saveVideoMd5(urlValue, videoMd5)

        log("get videoMd5 with ftp, 耗时：${System.currentTimeMillis() - start}")

        return getResult.invoke(videoMd5, isMatched)
    }
}