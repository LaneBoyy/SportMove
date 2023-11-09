package ru.laneboy.sportmove.domain

data class GameData(
    var firstTeam: String,
    var secondTeam: String,
    var firstTeamScore: Int,
    var secondTeamScore: Int,
    var gameIsEnd: Boolean
)