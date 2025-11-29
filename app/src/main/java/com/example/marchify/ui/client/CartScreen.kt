package com.example.marchify.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.marchify.api.models.CartItem
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*

/**
 * Shopping Cart Screen
 * Shows cart items and allows checkout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onCheckoutClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Mon Panier",
                onBackClick = onBackClick,
                actions = {
                    if (uiState.cart?.produits?.isNotEmpty() == true) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Vider le panier"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.cart?.produits?.isNotEmpty() == true) {
                CartBottomBar(
                    total = uiState.cart!!.total,
                    onCheckout = onCheckoutClick
                )
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
                    onRetry = { viewModel.loadCart() }
                )
            }
            uiState.cart == null || uiState.cart!!.produits.isEmpty() -> {
                EmptyCartState(
                    onStartShopping = onBackClick
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
                    // Cart items
                    items(uiState.cart!!.produits) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onProductClick = { onProductClick(cartItem.produit.id) },
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(cartItem.produit.id, newQuantity)
                            },
                            onRemove = { viewModel.removeItem(cartItem.produit.id) }
                        )
                    }

                    // Summary Card
                    item {
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        CartSummaryCard(
                            subtotal = uiState.cart!!.total,
                            delivery = 5.0,
                            total = uiState.cart!!.total + 5.0
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }

    // Clear cart confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Vider le panier") },
            text = { Text("Êtes-vous sûr de vouloir vider votre panier ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCart()
                        showClearDialog = false
                    }
                ) {
                    Text("Vider", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onProductClick: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    var showRemoveDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium)
        ) {
            // Product Image
            AsyncImage(
                model = cartItem.produit.image,
                contentDescription = cartItem.produit.nom,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            // Product Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.produit.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Text(
                    text = cartItem.produit.boutique?.nom ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                // Price per unit
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${cartItem.produit.prix} TND",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = " / kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Quantity selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    FilledIconButton(
                        onClick = {
                            if (cartItem.quantite > 1) {
                                onQuantityChange(cartItem.quantite - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = PrimaryGreen.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Diminuer",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${cartItem.quantite} kg",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = 60.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    FilledIconButton(
                        onClick = { onQuantityChange(cartItem.quantite + 1) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = PrimaryGreen.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Augmenter",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Remove button
                    IconButton(
                        onClick = { showRemoveDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = Error
                        )
                    }
                }
            }
        }

        // Subtotal
        Surface(
            color = PrimaryGreen.copy(alpha = 0.05f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sous-total: ${cartItem.prixTotal} TND",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                modifier = Modifier.padding(Spacing.medium)
            )
        }
    }

    // Remove confirmation dialog
    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Retirer du panier") },
            text = { Text("Voulez-vous retirer ${cartItem.produit.nom} du panier ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove()
                        showRemoveDialog = false
                    }
                ) {
                    Text("Retirer", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun CartSummaryCard(
    subtotal: Double,
    delivery: Double,
    total: Double
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large)
        ) {
            Text(
                text = "Résumé de la commande",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sous-total",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Text(
                    text = "$subtotal TND",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            // Delivery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Livraison",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Text(
                    text = "$delivery TND",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            Divider()

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$total TND",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    total: Double,
    onCheckout: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "${total + 5.0} TND",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }

            Button(
                onClick = onCheckout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Commander")
                Spacer(modifier = Modifier.width(Spacing.small))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}
