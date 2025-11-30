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
import com.example.marchify.api.models.DeliveryStatus
import com.example.marchify.api.models.toLabel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Delivery Tracking Screen
 * Real-time delivery tracking with status updates
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTrackingScreen(
    bonId: String,
    onBackClick: () -> Unit,
    onOpenMap: (String) -> Unit,
    viewModel: DeliveriesViewModel = viewModel(
        factory = DeliveriesViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val bon = uiState.deliveries.find { it.id == bonId }

    var showCompleteDialog by remember { mutableStateOf(false) }
    var showFailDialog by remember { mutableStateOf(false) }
    var failureReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Livraison en cours",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        if (bon == null) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // Map placeholder
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryGreen.copy(alpha = 0.1f)
                        ),
                        onClick = { onOpenMap(bonId) }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = PrimaryGreen
                                )
                                Spacer(modifier = Modifier.height(Spacing.small))
                                Text(
                                    text = "Voir sur la carte",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }
                }

                // Delivery Status
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
                                text = "Statut de la livraison",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(Spacing.medium))

                            Surface(
                                color = PrimaryGreen.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = bon.status.toLabel(),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.large))

                            // Progress Steps
                            DeliverySteps(currentStatus = bon.status)
                        }
                    }
                }

                // Customer Info
                bon.commande?.client?.let { client ->
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
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PrimaryGreen
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text(
                                        text = "Client",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(Spacing.medium))

                                Text(
                                    text = "${client.prenom} ${client.nom}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

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
                                        text = client.telephone,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Delivery Address
                bon.commande?.let { commande ->
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
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Error
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text(
                                        text = "Adresse de livraison",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(Spacing.medium))

                                Text(
                                    text = "${commande.adresseLivraison.rue}\n" +
                                            "${commande.adresseLivraison.ville}, ${commande.adresseLivraison.codePostal}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                // Action buttons
                if (bon.status in listOf(DeliveryStatus.PENDING_PICKUP, DeliveryStatus.IN_TRANSIT)) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            if (bon.status == DeliveryStatus.PENDING_PICKUP) {
                                Button(
                                    onClick = { viewModel.pickupOrder(bonId) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Info
                                    )
                                ) {
                                    Icon(Icons.Default.LocalShipping, null)
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text("Marquer comme récupéré")
                                }
                            }

                            if (bon.status == DeliveryStatus.IN_TRANSIT) {
                                Button(
                                    onClick = { showCompleteDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Success
                                    )
                                ) {
                                    Icon(Icons.Default.CheckCircle, null)
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text("Marquer comme livrée")
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                            ) {
                                OutlinedButton(
                                    onClick = { /* TODO: Call customer */ },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Phone, null)
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text("Appeler")
                                }

                                OutlinedButton(
                                    onClick = { showFailDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Error
                                    )
                                ) {
                                    Icon(Icons.Default.Cancel, null)
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text("Échec")
                                }
                            }
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Complete delivery dialog
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Success) },
            title = { Text("Confirmer la livraison") },
            text = { Text("Confirmez-vous que la livraison a été effectuée avec succès ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deliverOrder(bonId)
                        showCompleteDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Success
                    )
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Fail delivery dialog
    if (showFailDialog) {
        AlertDialog(
            onDismissRequest = { showFailDialog = false },
            icon = { Icon(Icons.Default.Cancel, null, tint = Error) },
            title = { Text("Signaler un échec") },
            text = {
                Column {
                    Text("Pourquoi la livraison a-t-elle échoué ?")
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    OutlinedTextField(
                        value = failureReason,
                        onValueChange = { failureReason = it },
                        label = { Text("Raison") },
                        placeholder = { Text("Ex: Client absent, adresse incorrecte...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (failureReason.isNotBlank()) {
                            viewModel.failDelivery(bonId, failureReason)
                            showFailDialog = false
                            onBackClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error
                    ),
                    enabled = failureReason.isNotBlank()
                ) {
                    Text("Signaler")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFailDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun DeliverySteps(
    currentStatus: DeliveryStatus
) {
    val steps = listOf(
        "En attente de récupération" to DeliveryStatus.PENDING_PICKUP,
        "En transit" to DeliveryStatus.IN_TRANSIT,
        "Livrée" to DeliveryStatus.DELIVERED
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        steps.forEachIndexed { index, (label, status) ->
            val isCompleted = when (currentStatus) {
                DeliveryStatus.PENDING_PICKUP -> index == 0
                DeliveryStatus.IN_TRANSIT -> index <= 1
                DeliveryStatus.DELIVERED -> true
                DeliveryStatus.FAILED -> false
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isCompleted) PrimaryGreen else TextTertiary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(Spacing.medium))

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCompleted) TextPrimary else TextSecondary
                )
            }
        }
    }
}
