package com.example.marchify.ui.vendeur

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
import com.example.marchify.api.models.Produit
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * My Products Screen
 * Shows vendeur's products
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProductsScreen(
    onProductClick: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onEditProductClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: MyProductsViewModel = viewModel(
        factory = MyProductsViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Mes Produits",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (uiState.boutiques.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = PrimaryGreen
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter produit")
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingScreen()
                uiState.boutiques.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.Store,
                        title = "Aucune boutique",
                        message = "Créez d'abord une boutique pour ajouter des produits."
                    )
                }
                else -> {
                    // Boutique selector
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Spacing.medium, vertical = Spacing.small),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        items(uiState.boutiques) { boutique ->
                            FilterChip(
                                selected = boutique.id == uiState.selectedBoutiqueId,
                                onClick = { viewModel.loadProductsForBoutique(boutique.id) },
                                label = { Text(boutique.nom) },
                                leadingIcon = if (boutique.id == uiState.selectedBoutiqueId) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                    }

                    Divider()

                    // Products list
                    when {
                        uiState.isLoadingProducts -> LoadingScreen()
                        uiState.products.isEmpty() -> {
                            EmptyState(
                                icon = Icons.Default.Inventory2,
                                title = "Aucun produit",
                                message = "Ajoutez votre premier produit à cette boutique.",
                                actionLabel = "Ajouter un produit",
                                onActionClick = onAddProductClick
                            )
                        }
                        else -> {
                            LazyColumn(
                                contentPadding = PaddingValues(Spacing.medium),
                                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                            ) {
                                items(uiState.products) { product ->
                                    VendeurProductCard(
                                        product = product,
                                        onClick = { onProductClick(product.id) },
                                        onEdit = { onEditProductClick(product.id) },
                                        onDelete = { showDeleteDialog = product.id }
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
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { productId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = { Icon(Icons.Default.Delete, null, tint = Error) },
            title = { Text("Supprimer le produit") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce produit ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(productId)
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
private fun VendeurProductCard(
    product: Produit,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${product.prix} TND / ${product.unite}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Stock: ${product.quantite}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (product.quantite > 10) Success else Warning
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, null, tint = PrimaryGreen)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, null, tint = Error)
                    }
                }
            }
        }
    }
}
