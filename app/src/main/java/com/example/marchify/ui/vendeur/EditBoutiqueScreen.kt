package com.example.marchify.ui.vendeur

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.marchify.api.models.Boutique
import com.example.marchify.api.models.BoutiqueRequest
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.Resource
import kotlinx.coroutines.launch

/**
 * Edit Boutique Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBoutiqueScreen(
    boutiqueId: String,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
    boutiqueRepository: BoutiqueRepository
) {
    var boutique by remember { mutableStateOf<Boutique?>(null) }
    var nom by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var categorie by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load boutique data
    LaunchedEffect(boutiqueId) {
        boutiqueRepository.getBoutiqueById(boutiqueId).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { b ->
                        boutique = b
                        nom = b.nom
                        adresse = b.adresse
                        categorie = b.categorie
                        telephone = b.telephone
                    }
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

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Modifier la boutique",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            isLoading -> LoadingScreen()
            boutique == null -> {
                ErrorScreen(
                    message = errorMessage ?: "Boutique introuvable",
                    onRetry = { /* Reload */ }
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    // Form fields
                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it },
                        label = { Text("Nom de la boutique") },
                        leadingIcon = { Icon(Icons.Default.Store, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = adresse,
                        onValueChange = { adresse = it },
                        label = { Text("Adresse") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    OutlinedTextField(
                        value = categorie,
                        onValueChange = { categorie = it },
                        label = { Text("Catégorie") },
                        leadingIcon = { Icon(Icons.Default.Category, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = telephone,
                        onValueChange = { telephone = it },
                        label = { Text("Téléphone") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.large))

                    Button(
                        onClick = {
                            if (nom.isBlank() || adresse.isBlank() || categorie.isBlank() || telephone.isBlank()) {
                                errorMessage = "Veuillez remplir tous les champs"
                                return@Button
                            }

                            scope.launch {
                                isUpdating = true
                                errorMessage = null

                                val request = BoutiqueRequest(
                                    nom = nom,
                                    adresse = adresse,
                                    localisation = boutique?.localisation,
                                    categorie = categorie,
                                    telephone = telephone,
                                    vendeurId = boutique!!.vendeurId
                                )

                                boutiqueRepository.updateBoutique(boutiqueId, request).collect { result ->
                                    when (result) {
                                        is Resource.Success -> {
                                            isUpdating = false
                                            snackbarHostState.showSnackbar("Boutique mise à jour")
                                            onSuccess()
                                        }
                                        is Resource.Error -> {
                                            isUpdating = false
                                            errorMessage = result.message ?: "Erreur de mise à jour"
                                        }
                                        is Resource.Loading -> {}
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isUpdating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        )
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enregistrer les modifications")
                        }
                    }
                }
            }
        }
    }
}
