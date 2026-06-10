package br.com.carbuapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================
// Esquema de cores CarbuApp — espelha a identidade visual do
// frontend Web (sempre tema escuro, laranja como cor primária)
// =========================================================

private val DarkColorScheme = darkColorScheme(
    primary = CarbuPrimary,
    onPrimary = CarbuPrimaryText,
    primaryContainer = Color(0xFF2A1B0E),
    onPrimaryContainer = Color(0xFFFDBA74),

    secondary = CarbuBlue,
    onSecondary = CarbuBg,
    secondaryContainer = Color(0xFF1A2438),
    onSecondaryContainer = Color(0xFF93C5FD),

    tertiary = CarbuGreen,
    onTertiary = CarbuBg,
    tertiaryContainer = Color(0xFF16291C),
    onTertiaryContainer = Color(0xFF86EFAC),

    background = CarbuBg,
    onBackground = CarbuText,

    surface = CarbuCard,
    onSurface = CarbuText,
    surfaceVariant = CarbuCard2,
    onSurfaceVariant = CarbuMuted,
    surfaceContainer = CarbuCard,
    surfaceContainerHigh = CarbuCard2,
    surfaceContainerHighest = CarbuGrayBtn,
    surfaceContainerLow = CarbuSidebar,
    surfaceContainerLowest = CarbuBg,

    outline = CarbuBorder,
    outlineVariant = CarbuBorder,

    error = CarbuRed,
    onError = CarbuBg,
    errorContainer = Color(0xFF2C1A1A),
    onErrorContainer = Color(0xFFFCA5A5),

    inverseSurface = CarbuText,
    inverseOnSurface = CarbuBg,
    inversePrimary = CarbuPrimaryHover,
)

// O Web não possui tema claro — mantemos um claro discreto apenas
// como fallback, mas o app sempre força o tema escuro da marca.
private val LightColorScheme = lightColorScheme(
    primary = CarbuPrimary,
    onPrimary = Color.White,
    secondary = CarbuBlue,
    tertiary = CarbuGreen,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF111115),
    surface = Color.White,
    onSurface = Color(0xFF111115),
    error = CarbuRed,
)

@Composable
fun CarbuAppTheme(
    // CarbuApp tem identidade visual fixa (sempre escura), igual ao Web.
    darkTheme: Boolean = true,
    // Cores dinâmicas (Material You) desativadas para preservar a marca.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
