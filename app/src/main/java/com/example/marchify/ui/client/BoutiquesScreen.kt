package com.example.marchify.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.Spacing

/**
 * Boutiques List Screen
 * Displays all available boutiques with search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoutiquesScreen(
    onBoutiqueClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: BoutiquesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Boutiques",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::searchBoutiques,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium)
            )

            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }
                uiState.errorMessage != null -> {
                    ErrorScreen(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadBoutiques() }
                    )
                }
                uiState.filteredBoutiques.isEmpty() -> {
                    if (uiState.searchQuery.isNotBlank()) {
                        EmptySearchState(searchQuery = uiState.searchQuery)
                    } else {
                        EmptyState(
                            icon = Icons.Default.Store,
                            title = "Aucune boutique",
                            message = "Aucune boutique disponible pour le moment."
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        item {
                            Text(
                                text = "${uiState.filteredBoutiques.size} boutique(s) disponible(s)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(Spacing.small))
                        }

                        items(uiState.filteredBoutiques) { boutique ->
                            BoutiqueCard(
                                boutique = boutique,
                                onClick = { onBoutiqueClick(boutique.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Rechercher une boutique...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Effacer"
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}
