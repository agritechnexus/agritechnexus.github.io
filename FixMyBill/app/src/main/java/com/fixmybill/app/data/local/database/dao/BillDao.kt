package com.fixmybill.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fixmybill.app.data.local.database.entity.BillEntity
import com.fixmybill.app.data.local.database.entity.BillType
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: BillEntity): Long

    @Update
    suspend fun update(bill: BillEntity)

    @Delete
    suspend fun delete(bill: BillEntity)

    @Query("SELECT * FROM bills ORDER BY created_at DESC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE id = :billId")
    suspend fun getBillById(billId: Long): BillEntity?

    @Query("SELECT * FROM bills WHERE bill_type = :billType ORDER BY created_at DESC")
    fun getBillsByType(billType: BillType): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills ORDER BY created_at DESC LIMIT :limit")
    fun getRecentBills(limit: Int): Flow<List<BillEntity>>

    @Query("SELECT COALESCE(SUM(overcharge_amount), 0.0) FROM bills WHERE is_overcharged = 1")
    fun getTotalSavings(): Flow<Double>
}
