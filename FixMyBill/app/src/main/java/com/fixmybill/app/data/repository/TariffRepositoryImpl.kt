package com.fixmybill.app.data.repository

import com.fixmybill.app.data.local.database.dao.TariffDao
import com.fixmybill.app.data.local.database.entity.TariffEntity
import com.fixmybill.app.domain.model.TariffSlab
import com.fixmybill.app.domain.repository.TariffRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TariffRepositoryImpl @Inject constructor(
    private val tariffDao: TariffDao
) : TariffRepository {

    override suspend fun insertTariffs(tariffs: List<TariffSlab>) {
        tariffDao.insertAll(tariffs.map { it.toEntity() })
    }

    override suspend fun getTariffsByBoard(utilityBoard: String, state: String): List<TariffSlab> {
        return tariffDao.getTariffsByBoard(utilityBoard).map { it.toDomain() }
    }

    override suspend fun getTariffsByBoardAndCategory(
        utilityBoard: String,
        state: String,
        category: String
    ): List<TariffSlab> {
        return tariffDao.getTariffsByBoardAndCategory(utilityBoard, category).map { it.toDomain() }
    }

    private fun TariffSlab.toEntity(): TariffEntity {
        return TariffEntity(
            id = id,
            utilityBoard = utilityBoard,
            state = state,
            category = category,
            slabStart = slabStart.toDouble(),
            slabEnd = slabEnd.toDouble(),
            ratePerUnit = ratePerUnit,
            fixedCharge = fixedCharge,
            effectiveFrom = System.currentTimeMillis(),
            effectiveTo = null
        )
    }

    private fun TariffEntity.toDomain(): TariffSlab {
        return TariffSlab(
            id = id,
            utilityBoard = utilityBoard,
            state = state,
            category = category,
            slabStart = slabStart.toInt(),
            slabEnd = slabEnd.toInt(),
            ratePerUnit = ratePerUnit,
            fixedCharge = fixedCharge
        )
    }
}
