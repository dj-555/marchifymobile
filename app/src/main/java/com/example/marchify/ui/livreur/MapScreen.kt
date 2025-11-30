package com.example.marchify.ui.livreur

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Map Screen
 * Shows delivery route on map (Google Maps integration placeholder)
 * Note: Real implementation requires Google Maps SDK
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    deliveryId: String,
    onBackClick: () -> Unit,
    viewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(deliveryId) {
        viewModel.loadDeliveryRoute(deliveryId)
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Itinéraire",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { /* TODO: Center on current location */ }) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Ma position")
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }
                uiState.delivery == null -> {
                    ErrorScreen(
                        message = uiState.errorMessage ?: "Impossible de charger l'itinéraire",
                        onRetry = { viewModel.loadDeliveryRoute(deliveryId) }
                    )
                }
                else -> {
                    // Map Placeholder
                    MapPlaceholder(delivery = uiState.delivery!!)

                    // Floating info cards
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(Spacing.medium)
                    ) {
                        // Distance & Time Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            ),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.medium),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MapInfoItem(
                                    icon = Icons.Default.Navigation,
                                    label = "Distance",
                                    value = "5.2 km" // TODO: Calculate from coordinates
                                )

                                Divider(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(1.dp)
                                )

                                MapInfoItem(
                                    icon = Icons.Default.Schedule,
                                    label = "Temps",
                                    value = "15 min" // TODO: Calculate from distance
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.small))

                        // Destination Card
                        uiState.delivery!!.commande?.let { commande ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                ),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Error,
                                        modifier = Modifier.size(32.dp)
                                    )

                                    Spacer(modifier = Modifier.width(Spacing.medium))

                                    Column(modifier = Modifier.weight(1f)) {
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

                                    IconButton(
                                        onClick = { /* TODO: Open in Google Maps */ }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = "Ouvrir dans Maps",
                                            tint = PrimaryGreen
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.small))

                        // Navigation Button
                        Button(
                            onClick = { /* TODO: Start navigation */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            )
                        ) {
                            Icon(Icons.Default.Directions, null)
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Text(
                                text = "Démarrer la navigation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapPlaceholder(
    delivery: com.example.marchify.api.models.BonDeLivraison
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Map placeholder with route indicators
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Current Location
                    MapMarker(
                        icon = Icons.Default.MyLocation,
                        color = Info,
                        label = "Ma position"
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Route Line
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(100.dp)
                            .background(PrimaryGreen)
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Destination
                    MapMarker(
                        icon = Icons.Default.LocationOn,
                        color = Error,
                        label = "Destination"
                    )
                }
            }

            Spacer(modifier = Modifier.height(250.dp)) // Space for bottom cards
        }

        // Map integration note
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(Spacing.medium),
            colors = CardDefaults.cardColors(
                containerColor = Info.copy(alpha = 0.9f)
            )
        ) {
            Row(
                modifier = Modifier.padding(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "Intégration Google Maps à venir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun MapMarker(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.small))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun MapInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.small))
        Column {
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
}
