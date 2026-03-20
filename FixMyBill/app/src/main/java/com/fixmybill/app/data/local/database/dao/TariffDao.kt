package com.fixmybill.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fixmybill.app.data.local.database.entity.TariffEntity

@Dao
interface TariffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tariff: TariffEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tariffs: List<TariffEntity>)

    @Query("SELECT * FROM tariffs WHERE utility_board = :board ORDER BY slab_start ASC")
    suspend fun getTariffsByBoard(board: String): List<TariffEntity>

    @Query("SELECT * FROM tariffs WHERE utility_board = :board AND category = :category ORDER BY slab_start ASC")
    suspend fun getTariffsByBoardAndCategory(board: String, category: String): List<TariffEntity>

    @Query("DELETE FROM tariffs WHERE utility_board = :board")
    suspend fun deleteByBoard(board: String)
}
