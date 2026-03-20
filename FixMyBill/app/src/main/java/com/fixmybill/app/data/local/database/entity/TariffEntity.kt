package com.fixmybill.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tariffs")
data class TariffEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "utility_board")
    val utilityBoard: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "slab_start")
    val slabStart: Double,

    @ColumnInfo(name = "slab_end")
    val slabEnd: Double,

    @ColumnInfo(name = "rate_per_unit")
    val ratePerUnit: Double,

    @ColumnInfo(name = "fixed_charge")
    val fixedCharge: Double,

    @ColumnInfo(name = "effective_from")
    val effectiveFrom: Long,

    @ColumnInfo(name = "effective_to")
    val effectiveTo: Long? = null
)
