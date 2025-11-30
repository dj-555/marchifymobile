package com.example.marchify.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.data.repository.AuthRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import com.example.marchify.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    private fun validateForm(): Boolean {
        val emailError = ValidationUtils.getEmailError(_uiState.value.email)
        val passwordError = ValidationUtils.getPasswordError(_uiState.value.password)

        _uiState.value = _uiState.value.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    fun login() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.login(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val loginResponse = result.data
                        if (loginResponse != null) {
                            // All user data including vendeur/livreur IDs are already saved in repository
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                userRole = loginResponse.user.role.name
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Réponse de connexion invalide"
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Erreur de connexion"
                        )
                    }

                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val userRole: String? = null
)
