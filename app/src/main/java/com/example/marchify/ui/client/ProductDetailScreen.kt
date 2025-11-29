package com.example.marchify.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*

/**
 * Product Detail Screen
 * Shows full product information and add to cart functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onAddToCart: () -> Unit,
    onCartClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(uiState.showAddedToCartMessage) {
        if (uiState.showAddedToCartMessage) {
            snackbarHostState.showSnackbar("Produit ajouté au panier")
            viewModel.clearAddedMessage()
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Détails",
                onBackClick = onBackClick,
                showCart = true,
                onCartClick = onCartClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.product != null) {
                AddToCartBottomBar(
                    quantity = uiState.quantity,
                    price = uiState.product!!.prix,
                    onIncrementQuantity = viewModel::incrementQuantity,
                    onDecrementQuantity = viewModel::decrementQuantity,
                    onAddToCart = {
                        viewModel.addToCart()
                        onAddToCart()
                    },
                    isLoading = uiState.isAddingToCart
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
                    onRetry = { viewModel.loadProduct(productId) }
                )
            }
            uiState.product != null -> {
                ProductDetailContent(
                    product = uiState.product!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: com.example.marchify.api.models.Produit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Product Image
        AsyncImage(
            model = product.image,
            contentDescription = product.nom,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(Spacing.large)
        ) {
            // Pinned/Featured Badge
            if (product.Ispinned) {
                Surface(
                    color = AccentOrange,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PRODUIT VEDETTE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.medium))
            }

            // Product Name
            Text(
                text = product.nom,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            // Category
            Surface(
                color = PrimaryGreen.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = product.categorie,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Price
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${product.prix} TND",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "/ ${product.unite.name.lowercase()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            Divider()

            Spacer(modifier = Modifier.height(Spacing.large))

            // Boutique Info
            if (product.boutique != null) {
                Text(
                    text = "Boutique",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = MaterialTheme.shapes.small,
                        color = PrimaryGreen.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Spacing.medium))

                    Column {
                        Text(
                            text = product.boutique.nom,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = product.boutique.adresse,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.large))

                Divider()

                Spacer(modifier = Modifier.height(Spacing.large))
            }

            // Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // Stock Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (product.quantite > 0)
                        Icons.Default.CheckCircle
                    else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (product.quantite > 0) Success else Error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = if (product.quantite > 0)
                        "En stock (${product.quantite} ${product.unite.name.lowercase()})"
                    else "Rupture de stock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (product.quantite > 0) Success else Error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Bottom spacing for bottom bar
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun AddToCartBottomBar(
    quantity: Int,
    price: Double,
    onIncrementQuantity: () -> Unit,
    onDecrementQuantity: () -> Unit,
    onAddToCart: () -> Unit,
    isLoading: Boolean
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
            // Quantity Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                FilledIconButton(
                    onClick = onDecrementQuantity,
                    enabled = quantity > 1 && !isLoading,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer",
                        tint = PrimaryGreen
                    )
                }

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                FilledIconButton(
                    onClick = onIncrementQuantity,
                    enabled = !isLoading,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Augmenter",
                        tint = PrimaryGreen
                    )
                }
            }

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                modifier = Modifier.height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Column {
                        Text(
                            text = "Ajouter",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "%.2f TND".format(price * quantity),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
