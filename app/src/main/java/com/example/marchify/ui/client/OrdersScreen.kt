package com.example.marchify.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.api.models.CmdStatus
import com.example.marchify.api.models.toLabel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Orders List Screen
 * Shows user's order history with filters
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onOrderClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = viewModel(
        factory = OrdersViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Mes Commandes",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.medium, vertical = Spacing.small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedFilter == null,
                        onClick = { viewModel.filterOrders(null) },
                        label = { Text("Toutes") },
                        leadingIcon = if (uiState.selectedFilter == null) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null
                    )
                }

                items(listOf(
                    CmdStatus.PENDING,
                    CmdStatus.PROCESSING,
                    CmdStatus.READY,
                    CmdStatus.SHIPPED,
                    CmdStatus.DELIVERED,
                    CmdStatus.CANCELLED,
                    CmdStatus.RETURNED
                )) { status ->
                    FilterChip(
                        selected = uiState.selectedFilter == status,
                        onClick = { viewModel.filterOrders(status) },
                        label = { Text(status.toLabel()) },
                        leadingIcon = if (uiState.selectedFilter == status) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }
                uiState.errorMessage != null -> {
                    ErrorScreen(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadOrders() }
                    )
                }
                uiState.filteredOrders.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.ShoppingBag,
                        title = "Aucune commande",
                        message = "Vous n'avez pas encore passé de commande."
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        item {
                            Text(
                                text = "${uiState.filteredOrders.size} commande(s)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        items(uiState.filteredOrders) { order ->
                            OrderCard(
                                order = order,
                                onClick = { onOrderClick(order.id) }
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
    }
}
