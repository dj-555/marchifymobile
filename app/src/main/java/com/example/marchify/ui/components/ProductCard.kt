package com.example.marchify.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.marchify.api.models.Produit
import com.example.marchify.ui.theme.*

/**
 * Product card component with safe click handling
 */
@Composable
fun ProductCard(
    product: Produit,
    onClick: (String) -> Unit,  // pass productId explicitly for validation
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
    showBoutique: Boolean = true
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clickable {
                if (product.id.isNotEmpty()) {
                    Log.d("ProductCard", "Product clicked with ID: ${product.id}")
                    onClick(product.id)
                } else {
                    Log.e("ProductCard", "Error: Product ID is empty!")
                    // Additional error handling if needed
                }

            },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Product Image
            Box {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.nom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Pinned badge (if product is featured)
                if (product.Ispinned) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = AccentOrange,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "★ Featured",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Product Info
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Product Name
                Text(
                    text = product.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Boutique name (optional)
                if (showBoutique && product.boutique != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = product.boutique.nom,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Price and Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${product.prix} TND",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            fontSize = 18.sp
                        )
                        Text(
                            text = product.unite.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            fontSize = 9.sp
                        )
                    }

                    // Add to cart button
                    FilledIconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = PrimaryGreen
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Ajouter au panier",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Stock indicator
                if (product.quantite < 10) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (product.quantite == 0) "Rupture de stock" else "Stock limité",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (product.quantite == 0) Error else Warning
                    )
                }
            }
        }
    }
}

/**
 * Compact product card for lists with safe click handling
 */
@Composable
fun ProductCardCompact(
    product: Produit,
    onClick: (String) -> Unit,  // pass productId explicitly for validation
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (product.id.isNotEmpty()) {
                    onClick(product.id)
                } else {
                    // Log or handle invalid product id
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = product.image,
                contentDescription = product.nom,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.prix} TND/${product.unite.name.lowercase()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )

                    if (product.quantite < 10) {
                        Text(
                            text = if (product.quantite == 0) "Rupture" else "Stock limité",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (product.quantite == 0) Error else Warning
                        )
                    }
                }
            }

            // Add button
            IconButton(
                onClick = onAddToCart,
                enabled = product.quantite > 0,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = PrimaryGreen.copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter",
                    tint = PrimaryGreen
                )
            }
        }
    }
}
