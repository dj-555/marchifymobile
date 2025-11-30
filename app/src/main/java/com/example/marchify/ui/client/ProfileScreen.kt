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
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Profile Screen
 * Shows user information and settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Mon Profil",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.user != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // User Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.medium),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryGreen.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.large),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = PrimaryGreen
                            )

                            Spacer(modifier = Modifier.height(Spacing.medium))

                            Text(
                                text = "${uiState.user!!.nom} ${uiState.user!!.prenom}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = uiState.user!!.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(Spacing.small))

                            Surface(
                                color = PrimaryGreen,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = uiState.user!!.role.name,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextOnPrimary
                                )
                            }
                        }
                    }

                    // Contact Info
                    ProfileSection(title = "Informations de contact") {
                        ProfileItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = uiState.user!!.email
                        )

                        ProfileItem(
                            icon = Icons.Default.Phone,
                            label = "Téléphone",
                            value = uiState.user!!.telephone
                        )
                    }

                    // Address
                    ProfileSection(title = "Adresse") {
                        ProfileItem(
                            icon = Icons.Default.LocationOn,
                            label = "Adresse",
                            value = "${uiState.user!!.adresse}\n" +
                                    "${uiState.user!!.adresse}, ${uiState.user!!.adresse}\n" +
                                    uiState.user!!.adresse
                        )
                    }

                    // Actions
                    ProfileSection(title = "Actions") {
                        ProfileActionItem(
                            icon = Icons.Default.Edit,
                            label = "Modifier le profil",
                            onClick = { /* TODO */ }
                        )

                        ProfileActionItem(
                            icon = Icons.Default.Lock,
                            label = "Changer le mot de passe",
                            onClick = { /* TODO */ }
                        )

                        ProfileActionItem(
                            icon = Icons.Default.Logout,
                            label = "Se déconnecter",
                            onClick = { showLogoutDialog = true },
                            isDestructive = true
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.large))
                }
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = { Text("Se déconnecter") },
            text = { Text("Êtes-vous sûr de vouloir vous déconnecter ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error
                    )
                ) {
                    Text("Se déconnecter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(Spacing.medium)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = Spacing.small)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.small),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = PrimaryGreen
        )

        Spacer(modifier = Modifier.width(Spacing.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProfileActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive)
                Error.copy(alpha = 0.05f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) Error else PrimaryGreen
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) Error else TextPrimary
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }

    Spacer(modifier = Modifier.height(Spacing.small))
}
