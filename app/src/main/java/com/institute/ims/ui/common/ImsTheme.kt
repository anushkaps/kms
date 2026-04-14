package com.institute.ims.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ImsLight = lightColorScheme(
    primary = LedgerPalette.Cobalt,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE8FF),
    onPrimaryContainer = Color(0xFF0C2D64),
    secondary = LedgerPalette.Forest,
    onSecondary = Color.White,
    tertiary = LedgerPalette.Plum,
    onTertiary = Color.White,
    background = LedgerPalette.Parchment,
    onBackground = LedgerPalette.Ink,
    surface = LedgerPalette.Surface,
    onSurface = LedgerPalette.Ink,
    surfaceVariant = Color(0xFFF0ECE3),
    onSurfaceVariant = LedgerPalette.MutedText,
    outlineVariant = LedgerPalette.Rule,
)

private val ImsDark = darkColorScheme(
    primary = Color(0xFF9DC0FF),
    onPrimary = Color(0xFF062757),
    primaryContainer = Color(0xFF133C7D),
    onPrimaryContainer = Color(0xFFDDE8FF),
    secondary = Color(0xFF67C0A9),
    onSecondary = Color(0xFF083F34),
    tertiary = Color(0xFFD3BCFF),
    onTertiary = Color(0xFF341765),
    background = Color(0xFF13120F),
    onBackground = Color(0xFFEAE4D9),
    surface = Color(0xFF1B1916),
    onSurface = Color(0xFFEAE4D9),
    surfaceVariant = Color(0xFF2B2721),
    onSurfaceVariant = Color(0xFFC4BEB2),
    outlineVariant = Color(0xFF4F483E),
)

@Composable
fun ImsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        useDarkTheme -> ImsDark
        else -> ImsLight
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ImsTypography,
        content = content,
    )
}
