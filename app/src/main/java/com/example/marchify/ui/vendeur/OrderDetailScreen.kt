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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.marchify.api.models.CmdStatus
import com.example.marchify.api.models.Commande
import com.example.marchify.api.models.toLabel
import com.example.marchify.data.repository.OrderRepository
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.DateUtils
import com.example.marchify.utils.Resource
import kotlinx.coroutines.launch

/**
 * Order Detail Screen for Vendeur
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBackClick: () -> Unit,
    orderRepository: OrderRepository
) {
    var order by remember { mutableStateOf<Commande?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load order
    LaunchedEffect(orderId) {
        orderRepository.getOrderById(orderId).collect { result ->
            when (result) {
                is Resource.Success -> {
                    order = result.data
                    isLoading = false
                }
                is Resource.Error -> {
                    errorMessage = result.message
                    isLoading = false
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateStatus(newStatus: CmdStatus) {
        scope.launch {
            isUpdating = true
            orderRepository.updateOrderStatus(orderId, newStatus).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        order = result.data
                        isUpdating = false
                        snackbarHostState.showSnackbar("Statut mis à jour")
                    }
                    is Resource.Error -> {
                        isUpdating = false
                        snackbarHostState.showSnackbar(result.message ?: "Erreur")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Commande #${orderId.take(8)}",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            isLoading -> LoadingScreen()
            order == null -> {
                ErrorScreen(
                    message = errorMessage ?: "Commande introuvable",
                    onRetry = { /* Reload */ }
                )
            }
            else -> {
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
                        // Status Card
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
                                        text = "Statut",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.small))
                                    Surface(
                                        color = PrimaryGreen.copy(alpha = 0.1f),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = order!!.status.toLabel(),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(Spacing.small))
                                    Text(
                                        text = DateUtils.formatIsoToReadable(order!!.dateCommande),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // Customer Info
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.large)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Person, null, tint = PrimaryGreen)
                                        Spacer(modifier = Modifier.width(Spacing.small))
                                        Text(
                                            text = "Client",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(Spacing.medium))
                                    order!!.client?.let { client ->
                                        Text(
                                            text = "${client.prenom} ${client.nom}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = client.telephone,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // Delivery Address
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.large)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.LocationOn, null, tint = Error)
                                        Spacer(modifier = Modifier.width(Spacing.small))
                                        Text(
                                            text = "Adresse de livraison",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(Spacing.medium))
                                    Text(
                                        text = "${order!!.adresseLivraison.rue}\n" +
                                                "${order!!.adresseLivraison.ville}, ${order!!.adresseLivraison.codePostal}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        // Products
                        item {
                            Text(
                                text = "Produits",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(order!!.produits) { item ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.medium),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.produit?.nom ?: "Produit",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Quantité: ${item.quantite}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = "${item.prixTotal} TND",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                            }
                        }

                        // Total
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.large),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${order!!.totalCommande} TND",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                            }
                        }
                    }

                    // Action Buttons
                    if (order!!.status in listOf(CmdStatus.PENDING, CmdStatus.PROCESSING, CmdStatus.READY)) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.medium),
                                verticalArrangement = Arrangement.spacedBy(Spacing.small)
                            ) {
                                when (order!!.status) {
                                    CmdStatus.PENDING -> {
                                        Button(
                                            onClick = { updateStatus(CmdStatus.PROCESSING) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            enabled = !isUpdating,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = PrimaryGreen
                                            )
                                        ) {
                                            Icon(Icons.Default.CheckCircle, null)
                                            Spacer(modifier = Modifier.width(Spacing.small))
                                            Text("Accepter la commande")
                                        }
                                    }
                                    CmdStatus.PROCESSING -> {
                                        Button(
                                            onClick = { updateStatus(CmdStatus.READY) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            enabled = !isUpdating,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = PrimaryGreen
                                            )
                                        ) {
                                            Icon(Icons.Default.Inventory, null)
                                            Spacer(modifier = Modifier.width(Spacing.small))
                                            Text("Marquer comme prête")
                                        }
                                    }
                                    CmdStatus.READY -> {
                                        Button(
                                            onClick = { updateStatus(CmdStatus.SHIPPED) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            enabled = !isUpdating,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = PrimaryGreen
                                            )
                                        ) {
                                            Icon(Icons.Default.LocalShipping, null)
                                            Spacer(modifier = Modifier.width(Spacing.small))
                                            Text("Expédier")
                                        }
                                    }
                                    else -> {}
                                }

                                if (order!!.status in listOf(CmdStatus.PENDING, CmdStatus.PROCESSING)) {
                                    OutlinedButton(
                                        onClick = { updateStatus(CmdStatus.CANCELLED) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = !isUpdating,
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Error
                                        )
                                    ) {
                                        Icon(Icons.Default.Cancel, null)
                                        Spacer(modifier = Modifier.width(Spacing.small))
                                        Text("Annuler la commande")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
