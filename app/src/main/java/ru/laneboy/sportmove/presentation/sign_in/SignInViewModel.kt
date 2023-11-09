package ru.laneboy.sportmove.presentation.sign_in

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

class SignInViewModel : ViewModel() {

    private val _auth = MutableLiveData<Resource<AuthData>>()
    val auth: LiveData<Resource<AuthData>>
        get() = _auth

    fun signIn(inputEmail: String?, inputPassword: String?) {
        val email = inputEmail?.trim() ?: ""
        val password = inputPassword?.trim() ?: ""
        if (email.isEmailValid() && password.isPasswordValid()) {
            _auth.value = Resource.loading()
            viewModelScope.launch(Dispatchers.IO) {
                _auth.postValue(SportRepository.signIn(email, password))
            }
        } else {
            _auth.value = Resource.error(Throwable("Заполните все поля"))
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    private fun String.isPasswordValid(): Boolean {
        return (this.length >= 2 && this.isNotEmpty())
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