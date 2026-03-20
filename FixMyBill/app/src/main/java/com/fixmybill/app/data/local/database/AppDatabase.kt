package com.fixmybill.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.fixmybill.app.data.local.database.dao.BillDao
import com.fixmybill.app.data.local.database.dao.ComplaintDao
import com.fixmybill.app.data.local.database.dao.TariffDao
import com.fixmybill.app.data.local.database.entity.BillEntity
import com.fixmybill.app.data.local.database.entity.BillType
import com.fixmybill.app.data.local.database.entity.ComplaintEntity
import com.fixmybill.app.data.local.database.entity.ComplaintStatus
import com.fixmybill.app.data.local.database.entity.TariffEntity
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromBillType(value: BillType): String {
        return value.name
    }

    @TypeConverter
    fun toBillType(value: String): BillType {
        return BillType.valueOf(value)
    }

    @TypeConverter
    fun fromComplaintStatus(value: ComplaintStatus): String {
        return value.name
    }

    @TypeConverter
    fun toComplaintStatus(value: String): ComplaintStatus {
        return ComplaintStatus.valueOf(value)
    }
}

@Database(
    entities = [
        BillEntity::class,
        TariffEntity::class,
        ComplaintEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao
    abstract fun tariffDao(): TariffDao
    abstract fun complaintDao(): ComplaintDao

    companion object {
        const val DATABASE_NAME = "fixmybill_database"
    }
}
