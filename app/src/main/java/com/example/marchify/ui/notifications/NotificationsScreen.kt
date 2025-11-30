package com.example.marchify.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.api.models.Notification
import com.example.marchify.api.models.NotificationPriority
import com.example.marchify.api.models.toLabel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.DateUtils
import com.example.marchify.utils.PrefsManager

/**
 * Notifications Screen
 * Shows user notifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNotificationClick: (Notification) -> Unit,
    onBackClick: () -> Unit,
     viewModel: NotificationsViewModel = viewModel(
        factory = NotificationsViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Notifications",
                onBackClick = onBackClick,
                actions = {
                    if (uiState.notifications.any { !it.read }) {
                        IconButton(onClick = { viewModel.markAllAsRead() }) {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = "Tout marquer comme lu"
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.refreshNotifications() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null -> {
                ErrorScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.refreshNotifications() }
                )
            }
            uiState.notifications.isEmpty() -> {
                EmptyNotificationsState()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    items(uiState.notifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = {
                                if (!notification.read) {
                                    viewModel.markAsRead(notification.id)
                                }
                                onNotificationClick(notification)
                            },
                            onDelete = { showDeleteDialog = notification.id }
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { notificationId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = { Icon(Icons.Default.Delete, null, tint = Error) },
            title = { Text("Supprimer la notification") },
            text = { Text("Voulez-vous supprimer cette notification ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteNotification(notificationId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error
                    )
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (notification.read) {
                CardBackground
            } else {
                PrimaryGreen.copy(alpha = 0.05f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.read) 1.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.Top
        ) {
            // Icon based on priority
            Surface(
                color = when (notification.priority) {
                    NotificationPriority.URGENT -> Error.copy(alpha = 0.1f)
                    NotificationPriority.HIGH -> Warning.copy(alpha = 0.1f)
                    NotificationPriority.MEDIUM -> Info.copy(alpha = 0.1f)
                    NotificationPriority.LOW -> TextTertiary.copy(alpha = 0.1f)
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getNotificationIcon(notification),
                        contentDescription = null,
                        tint = when (notification.priority) {
                            NotificationPriority.URGENT -> Error
                            NotificationPriority.HIGH -> Warning
                            NotificationPriority.MEDIUM -> Info
                            NotificationPriority.LOW -> TextSecondary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (notification.read) FontWeight.Normal else FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.read) {
                        Surface(
                            color = PrimaryGreen,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.size(8.dp)
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = DateUtils.getRelativeTimeFromIso(notification.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Supprimer",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun getNotificationIcon(notification: Notification): androidx.compose.ui.graphics.vector.ImageVector {
    return when (notification.type.name) {
        "ORDER_PLACED", "NEW_ORDER_RECEIVED" -> Icons.Default.ShoppingCart
        "ORDER_CONFIRMED", "ORDER_PROCESSING" -> Icons.Default.Inventory
        "ORDER_READY" -> Icons.Default.CheckCircle
        "ORDER_SHIPPED", "DELIVERY_ASSIGNED", "DELIVERY_PICKED_UP" -> Icons.Default.LocalShipping
        "ORDER_DELIVERED" -> Icons.Default.Done
        "ORDER_CANCELLED", "ORDER_RETURNED", "DELIVERY_FAILED" -> Icons.Default.Cancel
        "REVIEW_RECEIVED" -> Icons.Default.Star
        "PRODUCT_LOW_STOCK", "PRODUCT_OUT_OF_STOCK" -> Icons.Default.Warning
        "NEW_PRODUCT_ADDED" -> Icons.Default.NewReleases
        "PROMO_ALERT" -> Icons.Default.LocalOffer
        "SYSTEM_ANNOUNCEMENT" -> Icons.Default.Announcement
        else -> Icons.Default.Notifications
    }
}
