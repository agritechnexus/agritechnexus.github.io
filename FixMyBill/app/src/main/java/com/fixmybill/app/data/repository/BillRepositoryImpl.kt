package com.fixmybill.app.data.repository

import com.fixmybill.app.data.local.database.dao.BillDao
import com.fixmybill.app.data.local.database.entity.BillEntity
import com.fixmybill.app.data.local.database.entity.BillType as EntityBillType
import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillType
import com.fixmybill.app.domain.repository.BillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillRepositoryImpl @Inject constructor(
    private val billDao: BillDao
) : BillRepository {

    override suspend fun insertBill(bill: Bill): Long {
        return billDao.insert(bill.toEntity())
    }

    override suspend fun updateBill(bill: Bill) {
        billDao.update(bill.toEntity())
    }

    override suspend fun deleteBill(bill: Bill) {
        billDao.delete(bill.toEntity())
    }

    override fun getAllBills(): Flow<List<Bill>> {
        return billDao.getAllBills().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getBillById(id: Long): Bill? {
        return billDao.getBillById(id)?.toDomain()
    }

    override suspend fun getRecentBills(limit: Int): List<Bill> {
        return billDao.getRecentBills(limit).first().map { it.toDomain() }
    }

    override suspend fun getTotalSavings(): Double {
        return billDao.getTotalSavings().first()
    }

    private fun Bill.toEntity(): BillEntity {
        return BillEntity(
            id = id,
            consumerNumber = consumerNumber,
            billingPeriodStart = billingPeriod.first.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            billingPeriodEnd = billingPeriod.second.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            previousReading = meterReadings.first.toDouble(),
            currentReading = meterReadings.second.toDouble(),
            unitsConsumed = unitsConsumed.toDouble(),
            totalAmount = totalAmount,
            billType = when (billType) {
                BillType.ELECTRICITY -> EntityBillType.ELECTRICITY
                BillType.WATER -> EntityBillType.WATER
                BillType.GAS -> EntityBillType.GAS
            },
            utilityProvider = utilityProvider,
            state = state,
            imagePath = imagePath,
            isOvercharged = isOvercharged,
            overchargeAmount = overchargeAmount,
            confidence = confidence,
            createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    private fun BillEntity.toDomain(): Bill {
        return Bill(
            id = id,
            consumerNumber = consumerNumber,
            billingPeriod = Pair(
                Instant.ofEpochMilli(billingPeriodStart).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(billingPeriodEnd).atZone(ZoneId.systemDefault()).toLocalDate()
            ),
            meterReadings = Pair(previousReading.toLong(), currentReading.toLong()),
            unitsConsumed = unitsConsumed.toInt(),
            totalAmount = totalAmount,
            billType = when (billType) {
                EntityBillType.ELECTRICITY -> BillType.ELECTRICITY
                EntityBillType.WATER -> BillType.WATER
                EntityBillType.GAS -> BillType.GAS
            },
            utilityProvider = utilityProvider,
            state = state,
            imagePath = imagePath,
            isOvercharged = isOvercharged,
            overchargeAmount = overchargeAmount,
            confidence = confidence,
            createdAt = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDateTime()
        )
    }
}
