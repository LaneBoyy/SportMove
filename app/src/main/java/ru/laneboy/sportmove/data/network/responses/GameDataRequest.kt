package ru.laneboy.sportmove.data.network.responses

import com.google.gson.annotations.SerializedName

data class GameDataRequest(
    @SerializedName("firstTeamName")
    var firstTeam: String,
    @SerializedName("secondTeamName")
    var secondTeam: String,
    @SerializedName("firstTeamScore")
    var firstTeamScore: Int,
    @SerializedName("secondTeamScore")
    var secondTeamScore: Int,
    @SerializedName("gameIsEnd")
    val gameIsEnd:Boolean
)