package ru.laneboy.sportmove.presentation.add_request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.data.SportRepository
import ru.laneboy.sportmove.domain.RequestItem
import ru.laneboy.sportmove.domain.Resource

class AddRequestViewModel : ViewModel() {

    private val _request = MutableLiveData<Resource<RequestItem>>()
    val request: LiveData<Resource<RequestItem>>
        get() = _request

    fun addRequest(competitionId: Int, inputTeamName: String?, inputTeamCaptain: String?) {
        _request.value = Resource.loading()
        val teamName = inputTeamName?.trim() ?: ""
        val teamCaptain = inputTeamCaptain?.trim() ?: ""
        if (teamName.isNotEmpty() && teamCaptain.isNotEmpty())
            viewModelScope.launch {
                _request.postValue(
                    SportRepository.addRequest(
                        competitionId,
                        teamName,
                        teamCaptain
                    )
                )
            }
        else
            _request.value = Resource.error(Throwable("Не все поля заполнены"))


    }
}