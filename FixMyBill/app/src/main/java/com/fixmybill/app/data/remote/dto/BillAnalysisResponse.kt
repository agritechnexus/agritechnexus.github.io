package com.fixmybill.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BillAnalysisResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("content")
    val content: List<ContentBlock>,

    @SerializedName("model")
    val model: String,

    @SerializedName("stop_reason")
    val stopReason: String?,

    @SerializedName("usage")
    val usage: Usage
)

data class ContentBlock(
    @SerializedName("type")
    val type: String,

    @SerializedName("text")
    val text: String
)

data class Usage(
    @SerializedName("input_tokens")
    val inputTokens: Int,

    @SerializedName("output_tokens")
    val outputTokens: Int
)
