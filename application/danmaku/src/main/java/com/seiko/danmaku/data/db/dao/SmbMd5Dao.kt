package com.seiko.danmaku.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.danmaku.data.db.model.SmbMd5

@Dao
interface SmbMd5Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: SmbMd5): Long

    @Query("SELECT videoMd5 FROM SmbMd5_table WHERE uri=:uri")
    suspend fun getVideoMd5(uri: String): String?

}