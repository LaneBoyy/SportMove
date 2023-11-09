package ru.laneboy.sportmove.data.network.requests

import com.google.gson.annotations.SerializedName

data class SignUpDataRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("userRole")
    val userRole: Int
)