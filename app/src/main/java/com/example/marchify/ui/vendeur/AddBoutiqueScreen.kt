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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.api.models.BoutiqueRequest
import com.example.marchify.api.models.Localisation
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.launch

/**
 * Add Boutique Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBoutiqueScreen(
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
    boutiqueRepository: BoutiqueRepository,
    prefsManager: PrefsManager
) {
    var nom by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var categorie by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Nouvelle Boutique",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

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
                singleLine = true,
                placeholder = { Text("Ex: Fruits, Légumes, Épices...") }
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

            // Error message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Submit button
            Button(
                onClick = {
                    if (nom.isBlank() || adresse.isBlank() || categorie.isBlank() || telephone.isBlank()) {
                        errorMessage = "Veuillez remplir tous les champs"
                        return@Button
                    }

                    val vendeurId = prefsManager.getUserId()
                    if (vendeurId == null) {
                        errorMessage = "Erreur d'authentification"
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        val request = BoutiqueRequest(
                            nom = nom,
                            adresse = adresse,
                            localisation = null,
                            categorie = categorie,
                            telephone = telephone,
                            vendeurId = vendeurId
                        )

                        boutiqueRepository.createBoutique(request).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    isLoading = false
                                    snackbarHostState.showSnackbar("Boutique créée avec succès")
                                    onSuccess()
                                }
                                is Resource.Error -> {
                                    isLoading = false
                                    errorMessage = result.message ?: "Erreur lors de la création"
                                }
                                is Resource.Loading -> {}
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Créer la boutique")
                }
            }
        }
    }
}
