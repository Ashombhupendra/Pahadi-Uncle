package com.pahadi.uncle.network.data


import com.google.gson.annotations.SerializedName

data class AgentDto(
    @SerializedName("address")
    val address: String,
    @SerializedName("agent_code")
    val agentCode: String,
    @SerializedName("agent_file")
    val agentFile: String,
    @SerializedName("agent_mobile")
    val agentMobile: String,
    @SerializedName("agent_name")
    val agentName: String,
    @SerializedName("created")
    val created: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("password")
    val password: String
)