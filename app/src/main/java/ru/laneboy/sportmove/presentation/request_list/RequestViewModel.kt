package ru.laneboy.sportmove.presentation.request_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.data.SportRepository
import ru.laneboy.sportmove.domain.RequestItem
import ru.laneboy.sportmove.domain.Resource

class RequestViewModel : ViewModel() {

    private val _requestList = MutableLiveData<Resource<List<RequestItem>>>()
    val requestList: LiveData<Resource<List<RequestItem>>>
        get() = _requestList

    var competitionId: Int? = null

    fun loadRequestList() {
        _requestList.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            _requestList.postValue(SportRepository.getRequestsList())
        }
    }

    fun loadCompetitionRequests(id: Int) {
        competitionId = id
        _requestList.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            _requestList.postValue(SportRepository.getRequestsByCompetitionId(id))
        }
    }

    fun setRequestDecide(requestId: Int, permission: Boolean) {
        _requestList.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            val status = if (permission) 0 else 1
            SportRepository.changeRequestStatus(requestId, status)
            _requestList.postValue(SportRepository.getRequestsByCompetitionId(competitionId!!))


        }
    }

}