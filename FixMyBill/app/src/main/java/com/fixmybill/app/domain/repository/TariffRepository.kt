package com.fixmybill.app.domain.repository

import com.fixmybill.app.domain.model.TariffSlab

interface TariffRepository {
    suspend fun insertTariffs(tariffs: List<TariffSlab>)
    suspend fun getTariffsByBoard(utilityBoard: String, state: String): List<TariffSlab>
    suspend fun getTariffsByBoardAndCategory(
        utilityBoard: String,
        state: String,
        category: String
    ): List<TariffSlab>
}
