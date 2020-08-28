package com.seiko.danmaku.data.repo

import com.seiko.danmaku.data.db.dao.VideoDanmakuDao
import com.seiko.danmaku.data.db.model.VideoDanmaku
import com.seiko.danmaku.data.model.DanmaCommentBean
import com.seiko.danmaku.data.model.Result
import javax.inject.Inject

class VideoDanmaRepository @Inject constructor(
    private val danmaDao: VideoDanmakuDao
) {

    suspend fun saveDanmaDownloadBean(bean: VideoDanmaku): Boolean {
        bean.downloadDate = System.currentTimeMillis()
        return danmaDao.insert(bean) > 0
    }

    suspend fun getDanmaDownloadBean(videoMd5: String): Result<List<DanmaCommentBean>> {
        if (videoMd5.isEmpty()) {
            return Result.Error(Exception("videoMd5 is empty"))
        }
        return try {
            val bean = danmaDao.getDanma(videoMd5)
            if (bean == null || bean.danma.isEmpty()) {
                Result.Error(Exception("Not found danma in db"))
            } else {
                Result.Success(bean.danma)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}