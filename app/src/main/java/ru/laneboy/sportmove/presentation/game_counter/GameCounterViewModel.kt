package ru.laneboy.sportmove.presentation.game_counter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.data.SportRepository
import ru.laneboy.sportmove.data.network.responses.GameDiagramRequest
import ru.laneboy.sportmove.data.toRequest
import ru.laneboy.sportmove.domain.GameDiagram
import ru.laneboy.sportmove.domain.Resource

class GameCounterViewModel(private val gameDiagram: GameDiagram) : ViewModel() {

    private val _game = MutableLiveData(Resource.success(gameDiagram.toRequest()))
    val game: LiveData<Resource<GameDiagramRequest>>
        get() = _game

    private val _gameResult = MutableLiveData<Resource<Unit>>()
    val gameResult: LiveData<Resource<Unit>>
        get() = _gameResult

    private var counterJob: Job? = null

    init {
        loadGame()
    }

    fun loadGame() {
        viewModelScope.launch(Dispatchers.IO) {
            _game.postValue(SportRepository.loadGame(gameDiagram.competitionId, gameDiagram.id))
        }
    }

    fun changeScore(isFirstTeam: Boolean, isPlus: Boolean) {
        val oldGame = _game.value?.data
        if (oldGame != null) {
            val newGameData =
                if (isFirstTeam) {
                    val newScore =
                        if (isPlus) oldGame.gameData!!.firstTeamScore + 1 else if (oldGame.gameData!!.firstTeamScore > 0) oldGame.gameData.firstTeamScore - 1 else 0
                    oldGame.gameData.copy(firstTeamScore = newScore)
                } else {
                    val newScore =
                        if (isPlus) oldGame.gameData!!.secondTeamScore + 1 else if (oldGame.gameData!!.secondTeamScore > 0) oldGame.gameData!!.secondTeamScore - 1 else 0
                    oldGame.gameData.copy(secondTeamScore = newScore)
                }
            val editGame = oldGame.copy(gameData = newGameData)
            _game.value = Resource.success(editGame)
            counterJob?.cancel()
            counterJob = viewModelScope.launch(Dispatchers.IO) {
                delay(500)
                SportRepository.updateGame(editGame).ifError {
                    _game.postValue(Resource.error(it))
                }
            }
        } else {
            _game.postValue(Resource.error(Throwable("Ошибка сети. Обновите экран")))
        }
    }

    fun endGame() {
        viewModelScope.launch(Dispatchers.IO) {
            val game = game.value?.data
            if (game != null) {
                if (game.gameData!!.firstTeamScore != game.gameData!!.secondTeamScore) {
                    _gameResult.postValue(Resource.loading())
                    SportRepository.updateGame(game.copy(gameData = game.gameData!!.copy(gameIsEnd = true)))
                        .ifSuccess {
                            _gameResult.postValue(Resource.success(Unit))
                        }.ifError {
                            _gameResult.postValue(Resource.error(it))
                        }
                } else {
                    _gameResult.postValue(Resource.error(Throwable("Равный счет не допустим")))
                }
            } else {
                _gameResult.postValue(Resource.error(Throwable("Ошибка сети. Обновите экран")))
            }
        }
    }
}