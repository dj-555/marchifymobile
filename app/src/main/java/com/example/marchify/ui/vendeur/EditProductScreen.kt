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
import com.example.marchify.api.models.Produit
import com.example.marchify.api.models.UniteMesure
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.Resource
import kotlinx.coroutines.launch

/**
 * Edit Product Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
    productRepository: ProductRepository
) {
    var product by remember { mutableStateOf<Produit?>(null) }
    var nom by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prix by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categorie by remember { mutableStateOf("") }
    var selectedUnite by remember { mutableStateOf(UniteMesure.KILOGRAMME) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var expandedUnite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Load product data
    LaunchedEffect(productId) {
        productRepository.getProductById(productId).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { p ->
                        product = p
                        nom = p.nom
                        description = p.description
                        prix = p.prix.toString()
                        stock = p.quantite.toString()
                        categorie = p.categorie
                        selectedUnite = p.unite
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
                title = "Modifier le produit",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            isLoading -> LoadingScreen()
            product == null -> {
                ErrorScreen(
                    message = errorMessage ?: "Produit introuvable",
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
                        singleLine = true
                    )

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
                                text = if (imageUri != null) "Nouvelle image sélectionnée" else "Changer l'image",
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
                            if (nom.isBlank() || description.isBlank() || prix.isBlank() || stock.isBlank() || categorie.isBlank()) {
                                errorMessage = "Veuillez remplir tous les champs"
                                return@Button
                            }

                            scope.launch {
                                isUpdating = true
                                errorMessage = null

                                productRepository.updateProduct(
                                    productId = productId,
                                    nom = nom,
                                    description = description,
                                    prix = prix.toDoubleOrNull() ?: 0.0,
                                    stock = stock.toIntOrNull() ?: 0,
                                    categorie = categorie,
                                    unite = selectedUnite,
                                    imageUri = imageUri
                                ).collect { result: Resource<Produit> ->
                                    when (result) {
                                        is Resource.Success -> {
                                            isUpdating = false
                                            snackbarHostState.showSnackbar("Produit mis à jour")
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
