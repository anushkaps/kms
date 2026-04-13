package com.institute.ims.ui.common

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val ImsLight = lightColorScheme(
    primary = Color(0xFF3949AB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = Color(0xFF00115A),
    secondary = Color(0xFF5C5F71),
    onSecondary = Color.White,
    tertiary = Color(0xFF78536F),
    background = Color(0xFFF8F9FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFE1E2F3),
    onSurface = Color(0xFF1A1B26),
    onSurfaceVariant = Color(0xFF444654),
)

private val ImsDark = darkColorScheme(
    primary = Color(0xFFB6C0FF),
    onPrimary = Color(0xFF00207A),
    primaryContainer = Color(0xFF1F2F8F),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFC4C6DC),
    onSecondary = Color(0xFF2E3042),
    tertiary = Color(0xFFE7B8D8),
    background = Color(0xFF12131A),
    surface = Color(0xFF1A1B26),
    surfaceVariant = Color(0xFF444654),
    onSurface = Color(0xFFE3E4EF),
    onSurfaceVariant = Color(0xFFC6C6D4),
)

@Composable
fun ImsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> ImsDark
        else -> ImsLight
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ImsTypography,
        content = content,
    )
}
