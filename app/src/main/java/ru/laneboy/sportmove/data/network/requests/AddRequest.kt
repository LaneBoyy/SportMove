package ru.laneboy.sportmove.data.network.requests


import com.google.gson.annotations.SerializedName

data class AddRequest(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("competitionId")
    val competitionId: Int,
    @SerializedName("teamName")
    val teamName: String,
    @SerializedName("teamCaptain")
    val teamCaptain: String
)