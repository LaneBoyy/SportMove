package ru.laneboy.sportmove.data.network.responses


import com.google.gson.annotations.SerializedName

data class CompetitionItemResponse(
    @SerializedName("competitionDate")
    val competitionDate: String,
    @SerializedName("competitionDescription")
    val competitionDescription: String,
    @SerializedName("competitionId")
    val competitionId: Int,
    @SerializedName("competitionName")
    val competitionName: String,
    @SerializedName("countOfPlayers")
    val countOfPlayers: Int,
    @SerializedName("sportType")
    val sportType: String
)