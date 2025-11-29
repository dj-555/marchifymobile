package com.example.marchify.data.repository

import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.Notification
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for notification operations
 */
class NotificationRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)

    /**
     * Get user notifications
     */
    fun getNotifications(
        userId: String,
        limit: Int = 20,
        unreadOnly: Boolean = false
    ): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getNotifications(userId, limit, unreadOnly)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load notifications"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get unread notification count
     */
    fun getUnreadCount(userId: String): Flow<Resource<Int>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getUnreadNotificationCount(userId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.count))
            } else {
                emit(Resource.Error("Failed to load count"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Mark notification as read
     */
    fun markAsRead(userId: String, notificationId: String): Flow<Resource<Notification>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.markNotificationAsRead(userId, notificationId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to mark as read"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Mark all notifications as read
     */
    fun markAllAsRead(userId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.markAllNotificationsAsRead(userId)

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to mark all as read"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Delete notification
     */
    fun deleteNotification(userId: String, notificationId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.deleteNotificationById(userId, notificationId)

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to delete notification"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}
