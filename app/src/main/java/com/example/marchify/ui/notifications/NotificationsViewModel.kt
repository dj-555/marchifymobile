package com.example.marchify.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Notification
import com.example.marchify.data.repository.NotificationRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Notifications Screen
 */
class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications(unreadOnly: Boolean = false) {
        val userId = prefsManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            notificationRepository.getNotifications(userId, unreadOnly = unreadOnly)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                notifications = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = result.message ?: "Erreur de chargement",
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {}
                    }
                }
        }
    }

    fun markAsRead(notificationId: String) {
        val userId = prefsManager.getUserId() ?: return

        viewModelScope.launch {
            notificationRepository.markAsRead(userId, notificationId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Update local state
                        _uiState.value = _uiState.value.copy(
                            notifications = _uiState.value.notifications.map { notification ->
                                if (notification.id == notificationId) {
                                    notification.copy(read = true)
                                } else {
                                    notification
                                }
                            }
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun markAllAsRead() {
        val userId = prefsManager.getUserId() ?: return

        viewModelScope.launch {
            notificationRepository.markAllAsRead(userId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Update all notifications to read
                        _uiState.value = _uiState.value.copy(
                            notifications = _uiState.value.notifications.map { it.copy(read = true) }
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        val userId = prefsManager.getUserId() ?: return

        viewModelScope.launch {
            notificationRepository.deleteNotification(userId, notificationId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Remove from local state
                        _uiState.value = _uiState.value.copy(
                            notifications = _uiState.value.notifications.filter { it.id != notificationId }
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }
}

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val errorMessage: String? = null
)
