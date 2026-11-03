package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp), // 24dp corner radius for cards and containers
    large = RoundedCornerShape(24.dp),  // 24dp corner radius for large cards
    extraLarge = RoundedCornerShape(28.dp)
)
