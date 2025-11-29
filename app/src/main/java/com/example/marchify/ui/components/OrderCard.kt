package com.example.marchify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.marchify.api.models.Commande
import com.example.marchify.api.models.CmdStatus
import com.example.marchify.api.models.toLabel
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.DateUtils

/**
 * Order card component
 */
@Composable
fun OrderCard(
    order: Commande,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Order ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Commande #${order.id.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = DateUtils.formatIsoToReadable(order.dateCommande),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items count
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${order.produits.size} article(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delivery address
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${order.adresseLivraison.rue}, ${order.adresseLivraison.ville}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = BorderLight)

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Total Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Text(
                    text = "${order.totalCommande} TND",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
        }
    }
}

/**
 * Order status badge
 */
@Composable
fun OrderStatusBadge(
    status: CmdStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        CmdStatus.PENDING -> Pair(Warning.copy(alpha = 0.1f), Warning)
        CmdStatus.PROCESSING -> Pair(Info.copy(alpha = 0.1f), Info)
        CmdStatus.READY -> Pair(PrimaryGreen.copy(alpha = 0.1f), PrimaryGreen)
        CmdStatus.SHIPPED -> Pair(AccentOrange.copy(alpha = 0.1f), AccentOrange)
        CmdStatus.DELIVERED -> Pair(Success.copy(alpha = 0.1f), Success)
        CmdStatus.CANCELLED -> Pair(Error.copy(alpha = 0.1f), Error)
        CmdStatus.RETURNED -> Pair(TextTertiary.copy(alpha = 0.1f), TextTertiary)
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.toLabel(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * Compact order card for vendeur/livreur
 */
@Composable
fun OrderCardCompact(
    order: Commande,
    onClick: () -> Unit,
    showCustomerInfo: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "#${order.id.take(8)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (showCustomerInfo && order.client != null) {
                    Text(
                        text = "${order.client.prenom} ${order.client.nom}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "${order.totalCommande} TND",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }

            OrderStatusBadge(status = order.status)
        }
    }
}
