package com.aureliajets.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PinkStart = Color(0xFFE91E8C)
val PinkEnd = Color(0xFFFF6B9D)
val BluePrimary = Color(0xFF1565C0)
val BlueAccent = Color(0xFF42A5F5)
val DarkNavy = Color(0xFF0D1B3E)
val White = Color(0xFFFFFFFF)
val LightGray = Color(0xFFF0F4FF)
val CardBg = Color(0x99FFFFFF)

private val ColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = PinkStart,
    background = DarkNavy,
    surface = CardBg,
    onPrimary = White,
    onBackground = White,
    onSurface = DarkNavy
)

@Composable
fun AureliaJetsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content
    )
}
