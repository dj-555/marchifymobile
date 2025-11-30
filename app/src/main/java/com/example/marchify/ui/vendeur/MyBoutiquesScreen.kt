package com.example.marchify.ui.vendeur

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
import com.example.marchify.api.models.Boutique
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * My Boutiques Screen
 * Shows vendeur's boutiques
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBoutiquesScreen(
    onBoutiqueClick: (String) -> Unit,
    onAddBoutiqueClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: MyBoutiquesViewModel = viewModel(
        factory = MyBoutiquesViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Mes Boutiques",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBoutiqueClick,
                containerColor = PrimaryGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter boutique")
            }
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null -> {
                ErrorScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadMyBoutiques() }
                )
            }
            uiState.boutiques.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.Store,
                    title = "Aucune boutique",
                    message = "Créez votre première boutique pour commencer à vendre.",
                    actionLabel = "Créer une boutique",
                    onActionClick = onAddBoutiqueClick
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
                    items(uiState.boutiques) { boutique ->
                        BoutiqueCard(
                            boutique = boutique,
                            onClick = { onBoutiqueClick(boutique.id) }
                        )
                    }

                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BoutiqueCard(
    boutique: Boutique,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                color = PrimaryGreen.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = boutique.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = boutique.adresse,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = boutique.categorie,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Rating if available
                boutique.averageRating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AccentOrange
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.1f".format(rating),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        boutique.totalReviews?.let { count ->
                            Text(
                                text = " ($count avis)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}
