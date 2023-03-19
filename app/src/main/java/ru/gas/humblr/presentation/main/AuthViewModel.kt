package ru.gas.humblr.presentation.main

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import ru.gas.humblr.data.remote.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val application: Application,
    private val authRepository: AuthRepository,
) : AndroidViewModel(application) {
    val onBoardingState = MutableStateFlow(false)
    private val authService: AuthorizationService = AuthorizationService(getApplication())
    private val _isAuthSuccess = MutableStateFlow(false)
    val isAuthSuccess = _isAuthSuccess.asStateFlow()

    fun startAuthIntent(): Intent {

        return authRepository.sendAuthRequest(authService)
    }

    fun requestToken(code: String) {
        authRepository.performRequestToken(code, authService) {
            _isAuthSuccess.value = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            val intent = Intent(application.applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.applicationContext.startActivity(intent)
        }
    }


    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}