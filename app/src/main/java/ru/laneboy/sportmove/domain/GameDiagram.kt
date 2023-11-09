package ru.laneboy.sportmove.domain

import java.io.Serializable

data class GameDiagram(
    val id:Int,
    val deep:Int,
    val competitionId:Int,
    var gameData: GameData? = null,
    var previousTopGame: GameDiagram?,
    var previousBottomGame: GameDiagram?
):Serializable