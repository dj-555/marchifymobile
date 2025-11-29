package com.example.marchify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.marchify.ui.theme.*

/**
 * Generic empty state component
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = TextTertiary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    )
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

/**
 * Empty cart state
 */
@Composable
fun EmptyCartState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.ShoppingCart,
        title = "Votre panier est vide",
        message = "Commencez vos achats et ajoutez des produits à votre panier.",
        actionLabel = "Commencer les achats",
        onActionClick = onStartShopping,
        modifier = modifier
    )
}

/**
 * Empty orders state
 */
@Composable
fun EmptyOrdersState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Receipt,
        title = "Aucune commande",
        message = "Vous n'avez pas encore passé de commande.",
        actionLabel = "Parcourir les produits",
        onActionClick = onStartShopping,
        modifier = modifier
    )
}

/**
 * Empty products state (for vendeur)
 */
@Composable
fun EmptyProductsState(
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Inventory,
        title = "Aucun produit",
        message = "Ajoutez des produits à votre boutique pour commencer à vendre.",
        actionLabel = "Ajouter un produit",
        onActionClick = onAddProduct,
        modifier = modifier
    )
}

/**
 * Empty boutiques state
 */
@Composable
fun EmptyBoutiquesState(
    onAddBoutique: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Store,
        title = "Aucune boutique",
        message = "Créez votre première boutique pour commencer à vendre.",
        actionLabel = "Créer une boutique",
        onActionClick = onAddBoutique,
        modifier = modifier
    )
}

/**
 * Empty missions state (for livreur)
 */
@Composable
fun EmptyMissionsState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Assignment,
        title = "Aucune mission",
        message = "Il n'y a aucune mission disponible pour le moment.\nRevenez plus tard.",
        modifier = modifier
    )
}

/**
 * Empty search results
 */
@Composable
fun EmptySearchState(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.SearchOff,
        title = "Aucun résultat",
        message = "Aucun résultat trouvé pour \"$searchQuery\".\nEssayez avec d'autres mots-clés.",
        modifier = modifier
    )
}

/**
 * Empty notifications state
 */
@Composable
fun EmptyNotificationsState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Notifications,
        title = "Aucune notification",
        message = "Vous êtes à jour ! Aucune nouvelle notification.",
        modifier = modifier
    )
}
