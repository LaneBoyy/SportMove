package ru.laneboy.sportmove.data.network.requests


import com.google.gson.annotations.SerializedName

data class ChangeStatusRequest(
    @SerializedName("requestId")
    val requestId: Int,
    @SerializedName("status")
    val status: Int
)