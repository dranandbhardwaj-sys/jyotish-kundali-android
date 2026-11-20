package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val JyotishDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianMaroon,
    primaryContainer = RoyalCrimson,
    onPrimaryContainer = GoldBright,
    secondary = SaffronAccent,
    onSecondary = ObsidianMaroon,
    secondaryContainer = ElevatedMaroon,
    onSecondaryContainer = WarmParchment,
    tertiary = RoyalCrimsonLight,
    onTertiary = IvoryCream,
    tertiaryContainer = CardMaroon,
    onTertiaryContainer = GoldBright,
    background = ObsidianMaroon,
    onBackground = IvoryCream,
    surface = DeepMaroonSurface,
    onSurface = IvoryCream,
    surfaceVariant = CardMaroon,
    onSurfaceVariant = MutedGoldText,
    surfaceContainerLowest = ObsidianMaroon,
    surfaceContainerLow = DeepMaroonSurface,
    surfaceContainer = CardMaroon,
    surfaceContainerHigh = ElevatedMaroon,
    surfaceContainerHighest = Color(0xFF421928),
    outline = SubtleGoldBorder,
    outlineVariant = Color(0xFF3E241C),
    error = ManglikCoral,
    onError = ObsidianMaroon,
    errorContainer = ManglikCoralBg,
    onErrorContainer = ManglikCoral
)

private val JyotishShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun JyotishKundaliTheme(
    isHindi: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JyotishDarkColorScheme,
        typography = jyotishTypography(isHindi = isHindi),
        shapes = JyotishShapes,
        content = content
    )
}
