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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.api.models.Adresse
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Checkout Screen
 * Delivery address confirmation and order placement
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onOrderSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    var rue by remember { mutableStateOf("") }
    var ville by remember { mutableStateOf("") }
    var codePostal by remember { mutableStateOf("") }
    var deliveryNotes by remember { mutableStateOf("") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Finaliser la commande",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                // Delivery Address Section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.large)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryGreen
                            )
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Text(
                                text = "Adresse de livraison",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        OutlinedTextField(
                            value = rue,
                            onValueChange = { rue = it },
                            label = { Text("Rue") },
                            placeholder = { Text("Ex: 123 Avenue Habib Bourguiba") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Home, contentDescription = null)
                            }
                        )

                        Spacer(modifier = Modifier.height(Spacing.small))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            OutlinedTextField(
                                value = ville,
                                onValueChange = { ville = it },
                                label = { Text("Ville") },
                                placeholder = { Text("Ex: Tunis") },
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(Icons.Default.LocationCity, contentDescription = null)
                                }
                            )

                            OutlinedTextField(
                                value = codePostal,
                                onValueChange = { codePostal = it },
                                label = { Text("Code postal") },
                                placeholder = { Text("1000") },
                                modifier = Modifier.weight(0.6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        OutlinedTextField(
                            value = deliveryNotes,
                            onValueChange = { deliveryNotes = it },
                            label = { Text("Instructions (optionnel)") },
                            placeholder = { Text("Ex: Sonnez à l'interphone") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Order Summary
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.05f)
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.large)
                    ) {
                        Text(
                            text = "Récapitulatif",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sous-total")
                            Text(
                                text = "${"%.2f".format(uiState.cart?.total ?: 0.0)} TND",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.small))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Livraison")
                            Text(
                                text = "5.00 TND",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        Divider()

                        Spacer(modifier = Modifier.height(Spacing.medium))

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
                                text = "${"%.2f".format((uiState.cart?.total ?: 0.0) + 5.0)} TND",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.large))

                // Place Order Button
                val isFormValid = rue.isNotBlank() && ville.isNotBlank() && codePostal.isNotBlank()

                Button(
                    onClick = {
                        if (isFormValid) {
                            isPlacingOrder = true
                            val adresse = Adresse(
                                rue = rue,
                                ville = ville,
                                codePostal = codePostal,
                                pays = "Tunisie"
                            )
                            viewModel.confirmOrder(adresse)
                        }
                    },
                    enabled = isFormValid && !isPlacingOrder && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    )
                ) {
                    if (isPlacingOrder || uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(Spacing.small))
                        Text("Commande en cours...")
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.small))
                        Text("Confirmer la commande")
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.large))
            }
        }
    }

    // Handle order success
    LaunchedEffect(uiState.orderPlaced) {
        if (uiState.orderPlaced) {
            isPlacingOrder = false
            onOrderSuccess()
        }
    }

    // Handle errors
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            isPlacingOrder = false
        }
    }
}
