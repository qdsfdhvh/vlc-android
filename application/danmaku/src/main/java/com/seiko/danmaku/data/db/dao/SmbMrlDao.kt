package com.seiko.danmaku.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.danmaku.data.db.model.SmbMrl

@Dao
interface SmbMrlDao {

    @Query("SELECT * FROM SmbMrl_table")
    suspend fun all(): List<SmbMrl>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: SmbMrl): Long

}