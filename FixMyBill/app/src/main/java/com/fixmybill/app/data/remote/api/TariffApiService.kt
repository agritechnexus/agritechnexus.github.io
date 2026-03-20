package com.fixmybill.app.data.remote.api

import com.fixmybill.app.data.remote.dto.TariffRateDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TariffApiService {

    @GET("api/v1/tariffs/{state}")
    suspend fun getTariffsByState(
        @Path("state") state: String
    ): Response<List<TariffRateDto>>

    @GET("api/v1/tariffs/{state}/{board}")
    suspend fun getTariffsByBoard(
        @Path("state") state: String,
        @Path("board") board: String
    ): Response<List<TariffRateDto>>

    @GET("api/v1/tariffs/{state}/{board}/{category}")
    suspend fun getTariffsByBoardAndCategory(
        @Path("state") state: String,
        @Path("board") board: String,
        @Path("category") category: String
    ): Response<List<TariffRateDto>>

    @GET("api/v1/tariffs/latest")
    suspend fun getLatestTariffs(
        @Query("bill_type") billType: String
    ): Response<List<TariffRateDto>>
}
