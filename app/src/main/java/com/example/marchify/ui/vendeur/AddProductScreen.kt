package com.example.marchify.ui.vendeur

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.marchify.api.models.UniteMesure
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Add Product Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
    boutiqueRepository: BoutiqueRepository,
    productRepository: ProductRepository,
    prefsManager: PrefsManager
) {
    var nom by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prix by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categorie by remember { mutableStateOf("") }
    var selectedUnite by remember { mutableStateOf(UniteMesure.KILOGRAMME) }
    var selectedBoutiqueId by remember { mutableStateOf<String?>(null) }
    var boutiques by remember { mutableStateOf<List<com.example.marchify.api.models.Boutique>>(emptyList()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var expandedUnite by remember { mutableStateOf(false) }
    var expandedBoutique by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingBoutiques by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Load vendeur's boutiques
    LaunchedEffect(Unit) {
        val vendeurId = prefsManager.getUserId()
        if (vendeurId != null) {
            boutiqueRepository.getBoutiquesByVendeurId(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        boutiques = result.data ?: emptyList()
                        if (boutiques.isNotEmpty()) {
                            selectedBoutiqueId = boutiques.first().id
                        }
                        isLoadingBoutiques = false
                    }
                    is Resource.Error -> {
                        errorMessage = result.message
                        isLoadingBoutiques = false
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Nouveau Produit",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        if (isLoadingBoutiques) {
            LoadingScreen()
        } else if (boutiques.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Store,
                title = "Aucune boutique",
                message = "Créez d'abord une boutique pour ajouter des produits."
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // Boutique selector
                ExposedDropdownMenuBox(
                    expanded = expandedBoutique,
                    onExpandedChange = { expandedBoutique = it }
                ) {
                    OutlinedTextField(
                        value = boutiques.find { it.id == selectedBoutiqueId }?.nom ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Boutique") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBoutique) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBoutique,
                        onDismissRequest = { expandedBoutique = false }
                    ) {
                        boutiques.forEach { boutique ->
                            DropdownMenuItem(
                                text = { Text(boutique.nom) },
                                onClick = {
                                    selectedBoutiqueId = boutique.id
                                    expandedBoutique = false
                                }
                            )
                        }
                    }
                }

                // Product fields
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom du produit") },
                    leadingIcon = { Icon(Icons.Default.Inventory2, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    leadingIcon = { Icon(Icons.Default.Description, null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    OutlinedTextField(
                        value = prix,
                        onValueChange = { prix = it },
                        label = { Text("Prix (TND)") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        leadingIcon = { Icon(Icons.Default.Inventory, null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                OutlinedTextField(
                    value = categorie,
                    onValueChange = { categorie = it },
                    label = { Text("Catégorie") },
                    leadingIcon = { Icon(Icons.Default.Category, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ex: Fruits, Légumes...") }
                )

                // Unit selector
                ExposedDropdownMenuBox(
                    expanded = expandedUnite,
                    onExpandedChange = { expandedUnite = it }
                ) {
                    OutlinedTextField(
                        value = selectedUnite.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unité de mesure") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnite) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnite,
                        onDismissRequest = { expandedUnite = false }
                    ) {
                        UniteMesure.values().forEach { unite ->
                            DropdownMenuItem(
                                text = { Text(unite.name) },
                                onClick = {
                                    selectedUnite = unite
                                    expandedUnite = false
                                }
                            )
                        }
                    }
                }

                // Image picker
                OutlinedCard(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.medium),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Image, null)
                        Spacer(modifier = Modifier.width(Spacing.small))
                        Text(
                            text = if (imageUri != null) "Image sélectionnée" else "Ajouter une image",
                            modifier = Modifier.weight(1f)
                        )
                        if (imageUri != null) {
                            Icon(Icons.Default.CheckCircle, null, tint = Success)
                        }
                    }
                }

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
                        if (nom.isBlank() || description.isBlank() || prix.isBlank() ||
                            stock.isBlank() || categorie.isBlank() || selectedBoutiqueId == null) {
                            errorMessage = "Veuillez remplir tous les champs"
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            productRepository.createProduct(
                                nom = nom,
                                description = description,
                                prix = prix.toDoubleOrNull() ?: 0.0,
                                stock = stock.toIntOrNull() ?: 0,
                                categorie = categorie,
                                unite = selectedUnite,
                                shopId = selectedBoutiqueId!!,
                                imageUri = imageUri
                            ).collect { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        isLoading = false
                                        snackbarHostState.showSnackbar("Produit créé avec succès")
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
                        Text("Créer le produit")
                    }
                }
            }
        }
    }
}
