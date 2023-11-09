package ru.laneboy.sportmove.presentation.sign_up

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.data.SportRepository
import ru.laneboy.sportmove.domain.AuthData
import ru.laneboy.sportmove.domain.Resource

class SignUpViewModel : ViewModel() {

    private val _openOrganizerScreen = MutableLiveData<Resource<AuthData>>()
    val openOrganizerScreen: LiveData<Resource<AuthData>>
        get() = _openOrganizerScreen

    fun signUp(
        inputEmail: String?,
        inputNickname: String?,
        inputPassword: String?,
        inputRole: Int
    ) {
        val email = inputEmail?.trim() ?: ""
        val nickname = inputNickname?.trim() ?: ""
        val password = inputPassword?.trim() ?: ""
        if (email.isEmailValid() && password.isPasswordValid() && nickname.isNotEmpty()) {
            _openOrganizerScreen.value = Resource.loading()
            viewModelScope.launch(Dispatchers.IO) {
                val result = SportRepository.signUp(email, nickname, password, inputRole)
                _openOrganizerScreen.postValue(result)
            }
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    private fun String.isPasswordValid(): Boolean {
        return (this.length >= 2)
    }

    companion object {

        private const val PARTICIPANT_ROLE_CODE = "Participant"
        private const val ORGANIZER_ROLE_CODE = "Organizer"
        private const val ERROR_STRING = "Неверная почта или пароль"
        private const val ERROR_INCORRECT_STRING = "Некорректно введена почта или пароль"
        private const val ERROR_NOT_INTERNET_STRING =
            "Отсутствует подключение к интернету. Проверьте соединение и попробуйте снова"
    }
}