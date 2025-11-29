package com.example.marchify.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.UserRole
import com.example.marchify.data.repository.AuthRepository
import com.example.marchify.utils.Resource
import com.example.marchify.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Register Screen
 * Handles registration logic, validation, and state management
 */
class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // ==================== UI STATE ====================
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ==================== FIELD UPDATES ====================

    fun onNomChange(nom: String) {
        _uiState.value = _uiState.value.copy(nom = nom, nomError = null)
    }

    fun onPrenomChange(prenom: String) {
        _uiState.value = _uiState.value.copy(prenom = prenom, prenomError = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onTelephoneChange(telephone: String) {
        _uiState.value = _uiState.value.copy(telephone = telephone, telephoneError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    fun onRoleChange(role: UserRole) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun onRueChange(rue: String) {
        _uiState.value = _uiState.value.copy(rue = rue, rueError = null)
    }

    fun onVilleChange(ville: String) {
        _uiState.value = _uiState.value.copy(ville = ville, villeError = null)
    }

    fun onCodePostalChange(codePostal: String) {
        _uiState.value = _uiState.value.copy(codePostal = codePostal, codePostalError = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    // ==================== VALIDATION ====================

    private fun validateForm(): Boolean {
        val nomError = ValidationUtils.getNameError(_uiState.value.nom)
        val prenomError = ValidationUtils.getNameError(_uiState.value.prenom)
        val emailError = ValidationUtils.getEmailError(_uiState.value.email)
        val telephoneError = ValidationUtils.getPhoneError(_uiState.value.telephone)
        val passwordError = ValidationUtils.getPasswordError(_uiState.value.password)

        val confirmPasswordError = when {
            _uiState.value.confirmPassword.isBlank() -> "Confirmez votre mot de passe"
            _uiState.value.confirmPassword != _uiState.value.password ->
                "Les mots de passe ne correspondent pas"
            else -> null
        }

        val rueError = if (_uiState.value.rue.isBlank()) "La rue est requise" else null
        val villeError = if (_uiState.value.ville.isBlank()) "La ville est requise" else null
        val codePostalError = if (_uiState.value.codePostal.isBlank())
            "Le code postal est requis" else null

        _uiState.value = _uiState.value.copy(
            nomError = nomError,
            prenomError = prenomError,
            emailError = emailError,
            telephoneError = telephoneError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            rueError = rueError,
            villeError = villeError,
            codePostalError = codePostalError
        )

        return listOf(
            nomError, prenomError, emailError, telephoneError,
            passwordError, confirmPasswordError, rueError, villeError, codePostalError
        ).all { it == null }
    }

    // ==================== REGISTRATION ====================

    fun register() {
        // Validate form
        if (!validateForm()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Veuillez corriger les erreurs"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Build address string from parts
            val adresse = "${_uiState.value.rue.trim()}, ${_uiState.value.ville.trim()}, ${_uiState.value.codePostal.trim()}, Tunisie"

            authRepository.register(
                nom = _uiState.value.nom.trim(),
                prenom = _uiState.value.prenom.trim(),
                email = _uiState.value.email.trim(),
                password = _uiState.value.password,
                role = _uiState.value.selectedRole,
                telephone = _uiState.value.telephone.trim(),
                adresse = adresse
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRegisterSuccessful = true
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Erreur d'inscription"
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

/**
 * UI State for Register Screen
 */
data class RegisterUiState(
    // Personal info
    val nom: String = "",
    val prenom: String = "",
    val email: String = "",
    val telephone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedRole: UserRole = UserRole.CLIENT,

    // Address (separate fields for UI)
    val rue: String = "",
    val ville: String = "",
    val codePostal: String = "",

    // Password visibility
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    // Validation errors
    val nomError: String? = null,
    val prenomError: String? = null,
    val emailError: String? = null,
    val telephoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val rueError: String? = null,
    val villeError: String? = null,
    val codePostalError: String? = null,

    // UI state
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)
