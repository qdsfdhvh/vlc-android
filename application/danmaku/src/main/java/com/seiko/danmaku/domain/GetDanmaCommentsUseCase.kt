package com.seiko.danmaku.domain

import com.seiko.danmaku.data.db.model.VideoDanmaku
import com.seiko.danmaku.data.model.DanmaCommentBean
import com.seiko.danmaku.data.repo.DanDanApiRepository
import com.seiko.danmaku.data.repo.VideoDanmaRepository
import com.seiko.danmaku.data.model.Result
import com.seiko.danmaku.util.log
import javax.inject.Inject

/**
 * 获取弹幕集合
 */
class GetDanmaCommentsUseCase @Inject constructor(
    private val danmaDbRepo: VideoDanmaRepository,
    private val danmaApiRepo: DanDanApiRepository,
    private val getVideoEpisodeId: GetVideoEpisodeIdUseCase
) {

    /**
     * @param videoMd5 视频前16mb的MD5
     * @param isMatched 是否精确匹配
     */
    suspend fun hash(videoMd5: String, isMatched: Boolean): Result<List<DanmaCommentBean>> {
        log("get danma comments...")
        // 尝试从本地数据库获取弹幕
        var start = System.currentTimeMillis()
        when(val result = danmaDbRepo.getDanmaDownloadBean(videoMd5)) {
            is Result.Success -> {
                log("get danma from db, 耗时：${System.currentTimeMillis() - start}")
                return Result.Success(result.data)
            }
        }

        // 此视频相应的episodeId
        val episodeId = when(val result = getVideoEpisodeId.hash(videoMd5, isMatched)) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        // 下载弹幕
        start = System.currentTimeMillis()
        return when(val result = danmaApiRepo.downloadDanma(episodeId)) {
            is Result.Success -> {
                log("get danma from net, 耗时：${System.currentTimeMillis() - start}")
                // 保存到数据库
                danmaDbRepo.saveDanmaDownloadBean(VideoDanmaku(
                    videoMd5 = videoMd5,
                    danma = result.data
                ))
                result
            }
            is Result.Error -> result
        }
    }

}