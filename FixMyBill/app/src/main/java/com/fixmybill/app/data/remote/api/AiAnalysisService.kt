package com.fixmybill.app.data.remote.api

import com.fixmybill.app.data.remote.dto.BillAnalysisResponse
import com.fixmybill.app.data.remote.dto.ClaudeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AiAnalysisService {

    @Headers(
        "Content-Type: application/json",
        "anthropic-version: 2023-06-01"
    )
    @POST("v1/messages")
    suspend fun analyzeBill(
        @Header("x-api-key") apiKey: String,
        @Body request: ClaudeRequest
    ): Response<BillAnalysisResponse>
}
