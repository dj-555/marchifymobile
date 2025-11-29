package com.example.marchify.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.api.models.UserRole
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Register Screen
 * Multi-step registration form with role selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(
            PrefsManager(androidx.compose.ui.platform.LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Navigate on successful registration
    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) {
            kotlinx.coroutines.delay(1500) // Show success message
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // Background decorations
        BackgroundDecorations()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            TopAppBar(
                title = { Text("Créer un compte") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Register Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Subtitle
                        Text(
                            text = "Remplissez le formulaire pour créer votre compte",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Role Selection
                        RoleSelection(
                            selectedRole = uiState.selectedRole,
                            onRoleChange = viewModel::onRoleChange
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Personal Information
                        PersonalInfoSection(
                            uiState = uiState,
                            viewModel = viewModel,
                            focusManager = focusManager
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Address Section
                        AddressSection(
                            uiState = uiState,
                            viewModel = viewModel,
                            focusManager = focusManager
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Error message
                        if (uiState.errorMessage != null) {
                            ErrorMessage(
                                message = uiState.errorMessage!!,
                                onDismiss = viewModel::clearError
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Success message
                        if (uiState.isRegisterSuccessful) {
                            SuccessMessage()
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Register button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.register()
                            },
                            enabled = !uiState.isLoading && !uiState.isRegisterSuccessful,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Inscription...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "S'inscrire",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Divider(modifier = Modifier.weight(1f))
                            Text(
                                text = "Ou",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Divider(modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Login link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Déjà un compte ? ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Se connecter",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen,
                                modifier = Modifier.clickable { onLoginClick() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

/**
 * Role selection chips
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleSelection(
    selectedRole: UserRole,
    onRoleChange: (UserRole) -> Unit
) {
    Column {
        Text(
            text = "Je suis :",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedRole == UserRole.CLIENT,
                onClick = { onRoleChange(UserRole.CLIENT) },
                label = { Text("Client") },
                leadingIcon = if (selectedRole == UserRole.CLIENT) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null,
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = selectedRole == UserRole.VENDEUR,
                onClick = { onRoleChange(UserRole.VENDEUR) },
                label = { Text("Vendeur") },
                leadingIcon = if (selectedRole == UserRole.VENDEUR) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null,
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = selectedRole == UserRole.LIVREUR,
                onClick = { onRoleChange(UserRole.LIVREUR) },
                label = { Text("Livreur") },
                leadingIcon = if (selectedRole == UserRole.LIVREUR) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Personal information section
 */
@Composable
private fun PersonalInfoSection(
    uiState: RegisterUiState,
    viewModel: RegisterViewModel,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column {
        // Nom
        OutlinedTextField(
            value = uiState.nom,
            onValueChange = viewModel::onNomChange,
            label = { Text("Nom") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = uiState.nomError != null,
            supportingText = uiState.nomError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Prenom
        OutlinedTextField(
            value = uiState.prenom,
            onValueChange = viewModel::onPrenomChange,
            label = { Text("Prénom") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = uiState.prenomError != null,
            supportingText = uiState.prenomError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Telephone
        OutlinedTextField(
            value = uiState.telephone,
            onValueChange = viewModel::onTelephoneChange,
            label = { Text("Téléphone") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            isError = uiState.telephoneError != null,
            supportingText = uiState.telephoneError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = viewModel::togglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (uiState.isPasswordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            isError = uiState.passwordError != null,
            supportingText = uiState.passwordError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text("Confirmer le mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = viewModel::toggleConfirmPasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.isConfirmPasswordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (uiState.isConfirmPasswordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            isError = uiState.confirmPasswordError != null,
            supportingText = uiState.confirmPasswordError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Address section
 */
@Composable
private fun AddressSection(
    uiState: RegisterUiState,
    viewModel: RegisterViewModel,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column {
        Text(
            text= "Adresse",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Rue
        OutlinedTextField(
            value = uiState.rue,
            onValueChange = viewModel::onRueChange,
            label = { Text("Rue") },
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
            isError = uiState.rueError != null,
            supportingText = uiState.rueError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ville
        OutlinedTextField(
            value = uiState.ville,
            onValueChange = viewModel::onVilleChange,
            label = { Text("Ville") },
            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
            isError = uiState.villeError != null,
            supportingText = uiState.villeError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Code Postal
        OutlinedTextField(
            value = uiState.codePostal,
            onValueChange = viewModel::onCodePostalChange,
            label = { Text("Code postal") },
            leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
            isError = uiState.codePostalError != null,
            supportingText = uiState.codePostalError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Background decorations
 */
@Composable
private fun BoxScope.BackgroundDecorations() {
    Box(
        modifier = Modifier
            .size(350.dp)
            .align(Alignment.TopEnd)
            .offset(x = 150.dp, y = (-150).dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryGreen.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
    )

    Box(
        modifier = Modifier
            .size(350.dp)
            .align(Alignment.BottomStart)
            .offset(x = (-150).dp, y = 150.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        AccentOrange.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * Error message card
 */
@Composable
private fun ErrorMessage(message: String, onDismiss: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ErrorBackground),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Error,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Error)
            }
        }
    }
}

/**
 * Success message card
 */
@Composable
private fun SuccessMessage() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Success.copy(alpha = 0.1f)
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Success,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Inscription réussie ! Redirection...",
                style = MaterialTheme.typography.bodyMedium,
                color = Success,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
