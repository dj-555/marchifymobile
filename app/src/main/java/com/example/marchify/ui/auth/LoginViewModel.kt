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

/**
 * ViewModel for Login Screen
 * Handles login logic, validation, and state management
 */
class LoginViewModel(
    private val authRepository: AuthRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    // ==================== UI STATE ====================
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // ==================== FUNCTIONS ====================

    /**
     * Update email field
     */
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    /**
     * Update password field
     */
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    /**
     * Toggle password visibility
     */
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    /**
     * Validate form fields
     */
    private fun validateForm(): Boolean {
        val emailError = ValidationUtils.getEmailError(_uiState.value.email)
        val passwordError = ValidationUtils.getPasswordError(_uiState.value.password)

        _uiState.value = _uiState.value.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    /**
     * Perform login
     */
    fun login() {
        // Validate form
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.login(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { loginResponse ->
                            // Save token
                            prefsManager.saveAuthToken(loginResponse.token)

                            // Save user data
                            prefsManager.saveUserData(
                                userId = loginResponse.user.id,
                                role = loginResponse.user.role.name,
                                name = "${loginResponse.user.prenom} ${loginResponse.user.nom}",
                                email = loginResponse.user.email,
                                telephone = loginResponse.user.telephone,
                                adresse = loginResponse.user.adresse,
                                vendeurId = loginResponse.user.vendeurId,
                                livreurId = loginResponse.user.livreurId
                            )

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                userRole = loginResponse.user.role.name
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

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * UI State for Login Screen
 */
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
