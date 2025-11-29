package com.example.marchify.ui.vendeur

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import java.util.Calendar

/**
 * Analytics Screen
 * Shows sales statistics and order metrics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBackClick: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Statistiques",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { viewModel.refreshAnalytics() }) {
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
                    onRetry = { viewModel.refreshAnalytics() }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    // Period selector
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryGreen.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.large),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Période",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = getCurrentMonthYear(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = PrimaryGreen
                                )
                            }
                        }
                    }

                    // Revenue & Orders
                    item {
                        Text(
                            text = "Vue d'ensemble",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            MetricCard(
                                title = "Revenu total",
                                value = "${uiState.monthlyStats?.totalRevenue ?: 0.0} TND",
                                icon = Icons.Default.AttachMoney,
                                color = Success,
                                modifier = Modifier.weight(1f)
                            )

                            MetricCard(
                                title = "Commandes",
                                value = "${uiState.monthlyStats?.totalOrders ?: 0}",
                                icon = Icons.Default.Receipt,
                                color = Info,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Status breakdown
                    item {
                        Text(
                            text = "Répartition par statut",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    uiState.statusStats?.let { stats ->
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.large),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                                ) {
                                    StatusRow(
                                        label = "En attente",
                                        count = stats.pending,
                                        color = Warning
                                    )
                                    StatusRow(
                                        label = "En traitement",
                                        count = stats.processing,
                                        color = Info
                                    )
                                    StatusRow(
                                        label = "Prêtes",
                                        count = stats.ready,
                                        color = PrimaryGreen
                                    )
                                    StatusRow(
                                        label = "Expédiées",
                                        count = stats.shipped,
                                        color = AccentOrange
                                    )
                                    StatusRow(
                                        label = "Livrées",
                                        count = stats.delivered,
                                        color = Success
                                    )

                                    if (stats.cancelled > 0) {
                                        Divider()
                                        StatusRow(
                                            label = "Annulées",
                                            count = stats.cancelled,
                                            color = Error
                                        )
                                    }

                                    if (stats.returned > 0) {
                                        StatusRow(
                                            label = "Retournées",
                                            count = stats.returned,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Quick metrics
                    item {
                        Text(
                            text = "Indicateurs clés",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    uiState.monthlyStats?.let { stats ->
                        uiState.statusStats?.let { statusStats ->
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                                ) {
                                    QuickMetricCard(
                                        title = "Panier moyen",
                                        value = if (stats.totalOrders > 0) {
                                            "%.2f TND".format(stats.totalRevenue / stats.totalOrders)
                                        } else {
                                            "0.00 TND"
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    QuickMetricCard(
                                        title = "Taux succès",
                                        value = if (stats.totalOrders > 0) {
                                            "${(statusStats.delivered * 100 / stats.totalOrders)}%"
                                        } else {
                                            "0%"
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StatusRow(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, shape = MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun QuickMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

private fun getCurrentMonthYear(): String {
    val calendar = Calendar.getInstance()
    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.FRENCH)
    val year = calendar.get(Calendar.YEAR)
    return "$month $year"
}
