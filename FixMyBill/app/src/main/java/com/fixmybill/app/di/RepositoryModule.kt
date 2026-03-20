package com.fixmybill.app.di

import com.fixmybill.app.data.repository.BillRepositoryImpl
import com.fixmybill.app.data.repository.ComplaintRepositoryImpl
import com.fixmybill.app.data.repository.TariffRepositoryImpl
import com.fixmybill.app.domain.repository.BillRepository
import com.fixmybill.app.domain.repository.ComplaintRepository
import com.fixmybill.app.domain.repository.TariffRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBillRepository(
        billRepositoryImpl: BillRepositoryImpl
    ): BillRepository

    @Binds
    @Singleton
    abstract fun bindTariffRepository(
        tariffRepositoryImpl: TariffRepositoryImpl
    ): TariffRepository

    @Binds
    @Singleton
    abstract fun bindComplaintRepository(
        complaintRepositoryImpl: ComplaintRepositoryImpl
    ): ComplaintRepository
}
