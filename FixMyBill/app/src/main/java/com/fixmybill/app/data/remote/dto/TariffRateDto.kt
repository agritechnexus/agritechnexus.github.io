package com.fixmybill.app.data.remote.dto

import com.fixmybill.app.data.local.database.entity.TariffEntity
import com.google.gson.annotations.SerializedName

data class TariffRateDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("utility_board")
    val utilityBoard: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("slab_start")
    val slabStart: Double,

    @SerializedName("slab_end")
    val slabEnd: Double,

    @SerializedName("rate_per_unit")
    val ratePerUnit: Double,

    @SerializedName("fixed_charge")
    val fixedCharge: Double,

    @SerializedName("effective_from")
    val effectiveFrom: Long,

    @SerializedName("effective_to")
    val effectiveTo: Long?
) {
    fun toEntity(): TariffEntity {
        return TariffEntity(
            utilityBoard = utilityBoard,
            state = state,
            category = category,
            slabStart = slabStart,
            slabEnd = slabEnd,
            ratePerUnit = ratePerUnit,
            fixedCharge = fixedCharge,
            effectiveFrom = effectiveFrom,
            effectiveTo = effectiveTo
        )
    }
}
