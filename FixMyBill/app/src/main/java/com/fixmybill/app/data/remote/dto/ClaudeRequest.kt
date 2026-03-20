package com.fixmybill.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClaudeRequest(
    @SerializedName("model")
    val model: String = "claude-sonnet-4-20250514",

    @SerializedName("max_tokens")
    val maxTokens: Int = 4096,

    @SerializedName("messages")
    val messages: List<ClaudeMessage>
)

data class ClaudeMessage(
    @SerializedName("role")
    val role: String,

    @SerializedName("content")
    val content: String
)
