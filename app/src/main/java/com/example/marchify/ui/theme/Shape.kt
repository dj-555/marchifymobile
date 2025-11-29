package com.example.marchify.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * MarchiFy Shapes
 * Defines corner radius for components
 */
val Shapes = Shapes(
    // Small components (chips, badges)
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),

    // Medium components (buttons, cards)
    medium = RoundedCornerShape(12.dp),

    // Large components (bottom sheets, dialogs)
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
