package ru.laneboy.sportmove.presentation.game_diagram

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.data.SportRepository
import ru.laneboy.sportmove.data.network.responses.GameDiagramRequest
import ru.laneboy.sportmove.data.toGameData
import ru.laneboy.sportmove.data.toGameDataRequest
import ru.laneboy.sportmove.data.toRequest
import ru.laneboy.sportmove.domain.GameData
import ru.laneboy.sportmove.domain.GameDiagram
import ru.laneboy.sportmove.domain.RequestItem
import ru.laneboy.sportmove.domain.Resource
import kotlin.math.log2
import kotlin.math.pow

class GameDiagramViewModel : ViewModel() {


    private val _gameDiagram = MutableLiveData<Resource<GameDiagram>>()
    val gameDiagram: LiveData<Resource<GameDiagram>>
        get() = _gameDiagram


    private val _gameState = MutableLiveData<Resource<Boolean>>()
    val gameState: LiveData<Resource<Boolean>>
        get() = _gameState

    private var competitionId = -1

    fun setup(competitionId: Int) {
        this.competitionId = competitionId
    }

    fun loadGameState() {
        viewModelScope.launch(Dispatchers.IO) {
            SportRepository.getGameList(competitionId)
                .ifSuccessAsync { list ->
                    if (list!!.isEmpty()) {
                        _gameState.postValue(Resource.success(false))
                    } else {
                        _gameState.postValue(Resource.success(true))
                        val result = processGameDiagramRequest(ArrayList(list))
                        if (result.second.isEmpty()) {
                            _gameDiagram.postValue(Resource.success(result.first))
                        } else {
                            var updateError: Throwable? = null
                            for (gameForUpdate in result.second) {
                                SportRepository.updateGame(gameForUpdate)
                                    .ifError {
                                        updateError = it
                                    }
                            }
                            if (updateError == null) {
                                loadGameState()
                            } else {
                                _gameDiagram.postValue(Resource.error(updateError!!))
                            }
                        }
                    }
                }.ifError {
                    _gameState.postValue(Resource.error(it))
                }
        }
    }

    fun generateGames() {
        viewModelScope.launch(Dispatchers.IO) {
            val requestsList = SportRepository.getRequestsByCompetitionId(competitionId)
            if (requestsList.status == Resource.Status.SUCCESS && requestsList.data != null) {
                val acceptedRequests = requestsList.data
                    .filter { it.requestStatus == RequestItem.RequestStatus.ACCEPTED }.shuffled()
                    .toMutableList()
                val treeDeep = log2(acceptedRequests.size / 2f)
                val acceptIsValid = treeDeep % 1 == 0f
                if (acceptIsValid) {
                    idIterator = 0
                    val gameDiagram = generateTree(acceptedRequests, treeDeep.toInt(), 0)
                    val gameList = convertGameDiagramToListRequest(gameDiagram)
                    SportRepository.createGame(gameList).ifSuccess {
                        _gameDiagram.postValue(Resource.success(gameDiagram))
                    }.ifError {
                        Log.d("MainLog", "Second error: ${it}")
                        _gameDiagram.postValue(Resource.error(it))
                    }
                } else {
                    var neededTeams = 2.0.pow(treeDeep.toInt() + 2).toInt()
                    if (neededTeams == 0 || treeDeep < 1) {
                        neededTeams = 2
                    }
                    _gameDiagram.postValue(
                        Resource.error(
                            Throwable("Количество участников должно быть: ${neededTeams}")
                        )
                    )
                }
            } else {
                Log.d("MainLog", "First error: ${requestsList.error}")
                _gameDiagram.postValue(
                    Resource.error(
                        requestsList.error ?: Throwable("Unknown error")
                    )
                )
            }
        }
    }

    private var idIterator = 0
    private fun generateTree(
        list: MutableList<RequestItem>,
        totalDeep: Int,
        currentDeep: Int
    ): GameDiagram {
        idIterator += 1
        val gameDiagram = GameDiagram(
            idIterator,
            competitionId = competitionId,
            deep = currentDeep,
            gameData = if (totalDeep == currentDeep) {
                val request1 = list.removeAt(0)
                val request2 = list.removeAt(0)
                GameData(
                    request1.teamName,
                    request2.teamName,
                    0,
                    0,
                    false
                )
            } else null,
            previousTopGame = null,
            previousBottomGame = null
        )
        val previousTopDiagram = if (currentDeep < totalDeep) {
            generateTree(list, totalDeep, currentDeep + 1)
        } else null
        val previousBottomDiagram = if (currentDeep < totalDeep) {
            generateTree(list, totalDeep, currentDeep + 1)
        } else null
        gameDiagram.previousTopGame = previousTopDiagram
        gameDiagram.previousBottomGame = previousBottomDiagram
        return gameDiagram

    }

    private val needGameUpdate = arrayListOf<GameDiagramRequest>()

    private fun processGameDiagramRequest(list: ArrayList<GameDiagramRequest>): Pair<GameDiagram, List<GameDiagramRequest>> {
        val firstGameDiagramRequest = list.first { it.deep == 0 }
        val gameDiagram = convertRequestToGameDiagram(firstGameDiagramRequest, list)
        val needUpdateList = needGameUpdate.toList()
        needGameUpdate.clear()
        return Pair(gameDiagram, needUpdateList)
    }

    private fun convertRequestToGameDiagram(
        gameDiagramRequest: GameDiagramRequest,
        list: ArrayList<GameDiagramRequest>
    ): GameDiagram {
        val topGameDiagram = if (gameDiagramRequest.previousTopGameId != null)
            convertRequestToGameDiagram(
                list.first { it.id == gameDiagramRequest.previousTopGameId },
                list
            ) else null
        val bottomGameDiagram = if (gameDiagramRequest.previousBottomGameId != null)
            convertRequestToGameDiagram(
                list.first { it.id == gameDiagramRequest.previousBottomGameId },
                list
            ) else null

        var gameDiagram =
            GameDiagram(
                id = gameDiagramRequest.id,
                deep = gameDiagramRequest.deep,
                competitionId = competitionId,
                gameData = null,
                previousTopGame = topGameDiagram,
                previousBottomGame = bottomGameDiagram
            )
        if (gameDiagramRequest.gameData == null &&
            topGameDiagram?.gameData?.gameIsEnd == true &&
            bottomGameDiagram?.gameData?.gameIsEnd == true
        ) {
            val topGameData = topGameDiagram.gameData!!
            val bottomGameData = bottomGameDiagram.gameData!!
            val firstTeam = if (topGameData.firstTeamScore > topGameData.secondTeamScore)
                topGameData.firstTeam else topGameData.secondTeam
            val secondTeam = if (bottomGameData.firstTeamScore > bottomGameData.secondTeamScore)
                bottomGameData.firstTeam else bottomGameData.secondTeam
            gameDiagram.gameData = GameData(firstTeam, secondTeam, 0, 0, false)
            needGameUpdate.add(gameDiagram.toRequest())
        } else {
            gameDiagram.gameData = gameDiagramRequest.gameData?.toGameData()

        }
        return gameDiagram
    }

    private fun convertGameDiagramToListRequest(gameDiagram: GameDiagram): List<GameDiagramRequest> {
        val list = arrayListOf<GameDiagramRequest>()
        convertGameDiagramToRequest(gameDiagram, list)
        return list
    }

    private fun convertGameDiagramToRequest(
        gameDiagram: GameDiagram,
        list: ArrayList<GameDiagramRequest>
    ) {
        val gameDiagramRequest = GameDiagramRequest(
            id = gameDiagram.id,
            deep = gameDiagram.deep,
            competitionId = competitionId,
            gameData = gameDiagram.gameData?.toGameDataRequest(),
            previousTopGameId = gameDiagram.previousTopGame?.id,
            previousBottomGameId = gameDiagram.previousBottomGame?.id
        )
        list.add(gameDiagramRequest)
        if (gameDiagram.previousTopGame != null) {
            convertGameDiagramToRequest(gameDiagram.previousTopGame!!, list)
        }
        if (gameDiagram.previousBottomGame != null) {
            convertGameDiagramToRequest(gameDiagram.previousBottomGame!!, list)
        }
    }

}