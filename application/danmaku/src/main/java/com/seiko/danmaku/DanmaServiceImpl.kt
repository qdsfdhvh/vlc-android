package com.seiko.danmaku

import com.seiko.danmaku.domain.GetDanmaResultWithFileUseCase
import com.seiko.danmaku.domain.GetDanmaResultWithFtpUseCase
import com.seiko.danmaku.domain.GetDanmaResultWithNetUseCase
import com.seiko.danmaku.domain.GetDanmaResultWithSmbUseCase
import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.data.repo.SmbMrlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.libvlc.interfaces.IMedia
import java.io.File
import javax.inject.Inject

class DanmaServiceImpl @Inject constructor(
    private val smbMrlRepo: SmbMrlRepository,
    private val getDanmaResultWithFile: GetDanmaResultWithFileUseCase,
    private val getDanmaResultWithSmb: GetDanmaResultWithSmbUseCase,
    private val getDanmaResultWithNet: GetDanmaResultWithNetUseCase,
    private val getDanmaResultWithFtp: GetDanmaResultWithFtpUseCase
) : DanmaService {

    override suspend fun saveSmbServer(mrl: String, account: String, password: String) {
        smbMrlRepo.saveSmbMrl(mrl, account, password)
    }

    override suspend fun getDanmaResult(media: IMedia): DanmaResultBean? {
        return getDanmaResult(media, true)
    }

    private suspend fun getDanmaResult(media: IMedia, isMatched: Boolean): DanmaResultBean? {
        return withContext(Dispatchers.IO) {
            val result = when(val scheme = media.uri.scheme) {
                "file" -> getDanmaResultWithFile.invoke(File(media.uri.path!!), isMatched)
                "smb" -> getDanmaResultWithSmb.invoke(media.uri, isMatched)
                "http", "https" -> getDanmaResultWithNet.invoke(media.uri.toString(), isMatched)
                "ftp", "sftp" -> getDanmaResultWithFtp.invoke(media.uri, isMatched, scheme)
                else -> {
//                    Timber.d("danma service do not support url -> ${media.uri}")
                    return@withContext null
                }
            }
            when(result) {
                is Result.Success -> result.data
                is Result.Error -> {
//                    Timber.e(result.exception)
                    null
                }
            }
        }
    }

}