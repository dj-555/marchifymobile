package com.example.marchify.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marchify.data.repository.AuthRepository
import com.example.marchify.utils.PrefsManager

/**
 * Factory for creating LoginViewModel with dependencies
 */
class LoginViewModelFactory(
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val authRepository = AuthRepository(prefsManager)
            return LoginViewModel(authRepository, prefsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
