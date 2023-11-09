package ru.laneboy.sportmove.data.network.requests


import com.google.gson.annotations.SerializedName

data class AddCompetitionDataRequest(
    @SerializedName("competitionDate")
    val competitionDate: String,
    @SerializedName("competitionDescription")
    val competitionDescription: String,
    @SerializedName("competitionName")
    val competitionName: String,
    @SerializedName("sportType")
    val sportType: String
)