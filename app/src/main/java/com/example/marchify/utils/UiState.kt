package com.example.marchify.utils

/**
 * Generic UI state wrapper for screens
 */
data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null
) {
    val isSuccess: Boolean get() = data != null && !isLoading && error == null
    val isError: Boolean get() = error != null
}

/**
 * Extension to convert Resource to UiState
 */
fun <T> Resource<T>.toUiState(): UiState<T> {
    return when (this) {
        is Resource.Success -> UiState(isLoading = false, data = data, error = null)
        is Resource.Error -> UiState(isLoading = false, data = data, error = message)
        is Resource.Loading -> UiState(isLoading = true, data = data, error = null)
    }
}
