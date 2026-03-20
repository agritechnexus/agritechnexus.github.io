package com.fixmybill.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fixmybill.app.data.local.database.entity.ComplaintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(complaint: ComplaintEntity): Long

    @Update
    suspend fun update(complaint: ComplaintEntity)

    @Query("SELECT * FROM complaints WHERE bill_id = :billId ORDER BY created_at DESC")
    fun getComplaintsByBillId(billId: Long): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints ORDER BY created_at DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>
}
