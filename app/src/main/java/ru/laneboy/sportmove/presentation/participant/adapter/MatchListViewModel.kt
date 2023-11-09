package ru.laneboy.sportmove.presentation.participant.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.data.SportRepository
import ru.laneboy.sportmove.data.network.responses.CompetitionItemResponse
import ru.laneboy.sportmove.domain.Resource

class MatchListViewModel : ViewModel() {

    private val _matchList = MutableLiveData<Resource<List<CompetitionItemResponse>>>()
    val matchList: LiveData<Resource<List<CompetitionItemResponse>>>
        get() = _matchList

    val isUser:Boolean
        get()= SportRepository.isUser

    fun loadCompetitionList() {
        _matchList.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            _matchList.postValue(SportRepository.getCompetitionList())
        }
    }
}