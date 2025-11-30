package com.example.marchify.ui.livreur

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.marchify.api.models.BonDeLivraison
import com.example.marchify.api.models.DeliveryStatus
import com.example.marchify.api.models.toLabel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.DateUtils
import com.example.marchify.utils.PrefsManager

/**
 * Deliveries Screen
 * Shows livreur's accepted deliveries
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(
    onDeliveryClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: DeliveriesViewModel = viewModel(
        factory = DeliveriesViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar("Statut mis à jour")
            viewModel.clearUpdateSuccess()
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Mes Livraisons",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                        onClick = { viewModel.filterDeliveries(null) },
                        label = { Text("Toutes") },
                        leadingIcon = if (uiState.selectedFilter == null) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null
                    )
                }

                items(listOf(
                    DeliveryStatus.PENDING_PICKUP,
                    DeliveryStatus.IN_TRANSIT,
                    DeliveryStatus.DELIVERED,
                    DeliveryStatus.FAILED
                )) { status ->
                    FilterChip(
                        selected = uiState.selectedFilter == status,
                        onClick = { viewModel.filterDeliveries(status) },
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
                        onRetry = { viewModel.loadMyDeliveries() }
                    )
                }
                uiState.filteredDeliveries.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.LocalShipping,
                        title = "Aucune livraison",
                        message = "Vous n'avez pas de livraison pour le moment."
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        items(uiState.filteredDeliveries) { delivery ->
                            DeliveryCard(
                                delivery = delivery,
                                onClick = { onDeliveryClick(delivery.id) },
                                onPickup = { viewModel.pickupOrder(delivery.id) },
                                onDeliver = { viewModel.deliverOrder(delivery.id) },
                                isUpdating = uiState.isUpdating
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

@Composable
private fun DeliveryCard(
    delivery: BonDeLivraison,
    onClick: () -> Unit,
    onPickup: () -> Unit,
    onDeliver: () -> Unit,
    isUpdating: Boolean
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Livraison #${delivery.id.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                DeliveryStatusBadge(status = delivery.status)
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            // Date
            Text(
                text = DateUtils.formatIsoToReadable(delivery.dateCreation),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Delivery Address
            delivery.commande?.let { commande ->
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Column {
                        Text(
                            text = "Destination",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "${commande.adresseLivraison.rue}, ${commande.adresseLivraison.ville}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Action buttons based on status
                when (delivery.status) {
                    DeliveryStatus.PENDING_PICKUP -> {
                        Button(
                            onClick = onPickup,
                            enabled = !isUpdating,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            )
                        ) {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Text("Marquer comme récupéré")
                        }
                    }
                    DeliveryStatus.IN_TRANSIT -> {
                        Button(
                            onClick = onDeliver,
                            enabled = !isUpdating,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Success
                            )
                        ) {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Text("Marquer comme livrée")
                        }
                    }
                    else -> {
                        // No action needed for DELIVERED or FAILED
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryStatusBadge(
    status: DeliveryStatus
) {
    val (backgroundColor, textColor, label) = when (status) {
        DeliveryStatus.PENDING_PICKUP -> Triple(
            Warning.copy(alpha = 0.1f),
            Warning,
            "En attente de récupération"
        )
        DeliveryStatus.IN_TRANSIT -> Triple(
            AccentOrange.copy(alpha = 0.1f),
            AccentOrange,
            "En transit"
        )
        DeliveryStatus.DELIVERED -> Triple(
            Success.copy(alpha = 0.1f),
            Success,
            "Livrée"
        )
        DeliveryStatus.FAILED -> Triple(
            Error.copy(alpha = 0.1f),
            Error,
            "Échec de livraison"
        )
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
