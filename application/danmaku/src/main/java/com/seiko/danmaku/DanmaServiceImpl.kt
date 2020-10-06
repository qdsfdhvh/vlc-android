package com.seiko.danmaku

import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.data.repo.SmbMrlRepository
import com.seiko.danmaku.factory.*
import com.seiko.danmaku.util.log
import org.videolan.libvlc.interfaces.IMedia
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Provider

class DanmaServiceImpl @Inject constructor(
    private val smbMrlRepo: SmbMrlRepository,
    private val resultMap: Map<String, @JvmSuppressWildcards Provider<IDanmaResult>>
) : DanmaService {

    override suspend fun saveSmbServer(mrl: String, account: String, password: String) {
        log("Save account=${account}, password=${password}")
        smbMrlRepo.saveSmbMrl(mrl, account, password)
    }

    override suspend fun getDanmaResult(media: IMedia, isMatched: Boolean): Result<DanmaResultBean> {
        val scheme = media.uri.scheme
        if (resultMap.containsKey(scheme)) {
            return resultMap[scheme]!!.get().decode(media.uri, isMatched)
        }
        return Result.Error(
            IllegalArgumentException("danma service do not support url -> ${media.uri}")
        )
    }

}