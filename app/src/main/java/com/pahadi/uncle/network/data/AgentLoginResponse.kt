package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class AgentLoginResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("Agent_detail")
    val agentDto: AgentDto
)