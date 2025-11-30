package com.example.marchify.ui.livreur

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Mission Detail Screen
 * Shows full mission information and accept option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailScreen(
    missionId: String,
    onAcceptMission: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: MissionDetailViewModel = viewModel(
        factory = MissionDetailViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(missionId) {
        viewModel.loadMissionDetail(missionId)
    }

    LaunchedEffect(uiState.isAccepted) {
        if (uiState.isAccepted) {
            kotlinx.coroutines.delay(500)
            onAcceptMission()
        }
    }

    LaunchedEffect(uiState.isRefused) {
        if (uiState.isRefused) {
            kotlinx.coroutines.delay(500)
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Mission #${missionId.take(8)}",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.mission == null -> {
                ErrorScreen(
                    message = uiState.errorMessage ?: "Mission introuvable",
                    onRetry = { viewModel.loadMissionDetail(missionId) }
                )
            }
            else -> {
                val mission = uiState.mission!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        // Reward Card
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = PrimaryGreen.copy(alpha = 0.1f)
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
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
                                            text = "Montant de la commande",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "${mission.commande.totalCommande} TND",
                                            style = MaterialTheme.typography.displaySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = PrimaryGreen
                                    )
                                }
                            }
                        }

                        // Route Information
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.large)
                                ) {
                                    Text(
                                        text = "Itinéraire",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(Spacing.medium))

                                    // Pickup
                                    mission.commande.boutique?.let { boutique ->
                                        RoutePoint(
                                            icon = Icons.Default.Store,
                                            iconColor = AccentOrange,
                                            label = "Point de retrait",
                                            name = boutique.nom,
                                            address = boutique.adresse
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.large))

                                    // Delivery
                                    mission.commande.client?.let { client ->
                                        RoutePoint(
                                            icon = Icons.Default.LocationOn,
                                            iconColor = Error,
                                            label = "Point de livraison",
                                            name = "${client.prenom} ${client.nom}",
                                            address = "${mission.commande.adresseLivraison.rue}, ${mission.commande.adresseLivraison.ville}",
                                            phone = client.telephone
                                        )
                                    }
                                }
                            }
                        }

                        // Trip Details
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.large)
                                ) {
                                    Text(
                                        text = "Détails du trajet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(Spacing.medium))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        InfoItem(
                                            icon = Icons.Default.Payment,
                                            label = "Montant",
                                            value = "${mission.commande.totalCommande} TND"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom action buttons
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.medium),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.refuseMission(mission.commandeId) },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isAccepting
                            ) {
                                Text("Refuser")
                            }

                            Button(
                                onClick = { viewModel.acceptMission(mission.id) },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isAccepting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                )
                            ) {
                                if (uiState.isAccepting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Check, null)
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text("Accepter")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutePoint(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    label: String,
    name: String,
    address: String,
    phone: String? = null
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(Spacing.medium))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium
            )
            if (phone != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.small))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
