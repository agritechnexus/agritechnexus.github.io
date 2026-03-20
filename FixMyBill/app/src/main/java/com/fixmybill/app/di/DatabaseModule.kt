package com.fixmybill.app.di

import android.content.Context
import androidx.room.Room
import com.fixmybill.app.data.local.database.AppDatabase
import com.fixmybill.app.data.local.database.dao.BillDao
import com.fixmybill.app.data.local.database.dao.ComplaintDao
import com.fixmybill.app.data.local.database.dao.TariffDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "fixmybill_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideBillDao(database: AppDatabase): BillDao = database.billDao()

    @Provides
    @Singleton
    fun provideTariffDao(database: AppDatabase): TariffDao = database.tariffDao()

    @Provides
    @Singleton
    fun provideComplaintDao(database: AppDatabase): ComplaintDao = database.complaintDao()
}
