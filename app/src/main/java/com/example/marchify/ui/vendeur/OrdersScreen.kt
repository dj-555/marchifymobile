package com.example.marchify.ui.vendeur

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.api.models.CmdStatus
import com.example.marchify.api.models.toLabel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*

/**
 * Vendeur Orders Screen
 * Manage incoming orders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onOrderClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Commandes",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Status Filters
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
                    CmdStatus.SHIPPED
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
                uiState.isLoading -> LoadingScreen()
                uiState.errorMessage != null -> {
                    ErrorScreen(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadOrders() }
                    )
                }
                uiState.filteredOrders.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.Receipt,
                        title = "Aucune commande",
                        message = "Aucune commande pour le moment."
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        items(uiState.filteredOrders) { order ->
                            OrderCardCompact(
                                order = order,
                                onClick = { onOrderClick(order.id) },
                                showCustomerInfo = true
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
