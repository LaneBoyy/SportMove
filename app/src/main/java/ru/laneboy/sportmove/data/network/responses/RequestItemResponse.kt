package ru.laneboy.sportmove.data.network.responses


import com.google.gson.annotations.SerializedName

data class RequestItemResponse(
    @SerializedName("requestId")
    val requestId: Int,
    @SerializedName("requestStatus")
    val requestStatus: String,
    @SerializedName("selectedCompetition")
    val selectedCompetition: String,
    @SerializedName("teamCaptain")
    val teamCaptain: String,
    @SerializedName("teamName")
    val teamName: String
)