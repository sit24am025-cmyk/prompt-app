package com.idchan.prompt.ui.auth

import androidx.lifecycle.ViewModel
import com.idchan.prompt.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val usernameInput: String = "",
    val passwordInput: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            isLoggedIn = userPreferences.isLoggedIn,
            usernameInput = userPreferences.username
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(usernameInput = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(passwordInput = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRememberMe() {
        _uiState.update { it.copy(rememberMe = !it.rememberMe) }
    }

    fun login(onSuccess: () -> Unit) {
        val user = _uiState.value.usernameInput.trim()
        val pass = _uiState.value.passwordInput.trim()

        if (user.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your username.") }
            return
        }

        if (pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your password.") }
            return
        }

        // Authentication success
        if (_uiState.value.rememberMe) {
            userPreferences.isLoggedIn = true
            userPreferences.username = user
        }

        _uiState.update { it.copy(isLoggedIn = true, errorMessage = null) }
        onSuccess()
    }

    fun demoLogin(onSuccess: () -> Unit) {
        _uiState.update { it.copy(usernameInput = "creator", passwordInput = "idchan2026") }
        login(onSuccess)
    }

    fun logout() {
        userPreferences.isLoggedIn = false
        _uiState.update { LoginUiState(isLoggedIn = false) }
    }
}
