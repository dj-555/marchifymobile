package com.example.marchify.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.Spacing

/**
 * Products Grid Screen (Alternative layout)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    boutiqueId: String,
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProductsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(boutiqueId) {
        viewModel.loadProductsByBoutique(boutiqueId)
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Produits",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null -> {
                ErrorScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadProductsByBoutique(boutiqueId) }
                )
            }
            uiState.products.isEmpty() -> {
                EmptyProductsState(
                    onAddProduct = onBackClick
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { viewModel.addToCart(product.id) },
                            showBoutique = false
                        )
                    }
                }
            }
        }
    }
}
