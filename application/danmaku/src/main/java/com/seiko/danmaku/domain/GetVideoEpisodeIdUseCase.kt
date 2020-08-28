package com.seiko.danmaku.domain


import com.seiko.danmaku.data.api.model.MatchRequest
import com.seiko.danmaku.data.repo.DanDanApiRepository
import com.seiko.danmaku.data.repo.VideoMatchRepository
import com.seiko.danmaku.data.model.Result
import javax.inject.Inject

/**
 * 获取视频对应的集数id
 */
class GetVideoEpisodeIdUseCase @Inject constructor(
    private val danmaApiRepo: DanDanApiRepository,
    private val workMatchRepo: VideoMatchRepository
) {

    /**
     * @param videoMd5 视频前16mb的MD5
     * @param isMatched 是否精确匹配
     */
    suspend fun hash(videoMd5: String, isMatched: Boolean): Result<Int> {
        // 尝试从数据库中获取
        val episodeList = workMatchRepo.getEpisodeIdList(videoMd5, isMatched)
        if (episodeList.isNotEmpty()) {
            return Result.Success(episodeList[0])
        }

        // 通过弹弹api查询与之绑定的动漫信息
        val request = MatchRequest.hash(videoMd5)
        return when(val result = danmaApiRepo.getVideoMatchList(request)) {
            is Result.Success -> {
                val matched = result.data.first
                val matchList = result.data.second

                // 将结果存入数据，不管是否精确关联
                workMatchRepo.saveMatchResult(videoMd5, matchList, matched)

                if (isMatched == matched) {
                    Result.Success(matchList[0].episodeId)
                } else {
                    Result.Error(Exception("DanDanApi is not match this videoMd5:$videoMd5"))
                }
            }
            is Result.Error -> result
        }
    }

}