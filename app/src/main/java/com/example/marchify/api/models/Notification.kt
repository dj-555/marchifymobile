package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id")
    val id: String,
    val type: NotificationType,
    val priority: NotificationPriority = NotificationPriority.MEDIUM,
    val title: String,
    val message: String,
    val read: Boolean = false,
    val createdAt: String,  // ISO date string
    val readAt: String? = null,
    val userId: String,
    val commandeId: String? = null,
    val actionUrl: String? = null,
    val metadata: NotificationMetadata? = null
)

// Metadata can contain extra info
data class NotificationMetadata(
    val produitId: String? = null,
    val boutiqueId: String? = null,
    val orderId: String? = null,
    val imageUrl: String? = null,
    val amount: Double? = null
)

// Response models
data class UnreadCountResponse(
    val count: Int
)
