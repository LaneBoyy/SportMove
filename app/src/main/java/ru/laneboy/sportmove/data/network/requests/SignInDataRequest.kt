package ru.laneboy.sportmove.data.network.requests

import com.google.gson.annotations.SerializedName

data class SignInDataRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)