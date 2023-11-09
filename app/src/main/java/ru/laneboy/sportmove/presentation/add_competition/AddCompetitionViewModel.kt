package ru.laneboy.sportmove.presentation.add_competition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.data.SportRepository
import ru.laneboy.sportmove.domain.Resource

class AddCompetitionViewModel : ViewModel() {

    private val _competition = MutableLiveData<Resource<Any>>()
    val competition: LiveData<Resource<Any>>
        get() = _competition

    fun addCompetition(
        inputMatchName: String?,
        inputDescription: String?,
        inputSportType: String?,
        inputDate: String?
    ) {
        val matchName = inputMatchName?.trim() ?: ""
        val description = inputDescription?.trim() ?: ""
        val sportType = inputSportType?.trim() ?: ""
        val date = inputDate?.trim() ?: ""
        if (matchName.isEmpty() || description.isEmpty() || sportType.isEmpty() || date.isEmpty()) {
            _competition.value = Resource.error(Throwable("Заполните все поля"))
        } else {
            _competition.value = Resource.loading()
            viewModelScope.launch(Dispatchers.IO) {
                _competition.postValue(
                    SportRepository.addCompetition(
                        date,
                        description,
                        matchName,
                        sportType
                    )
                )

            }
        }
    }

}