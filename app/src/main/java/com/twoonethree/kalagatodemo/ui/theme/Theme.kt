package com.twoonethree.kalagatodemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF8C00),    // Bright Orange
    secondary = Color(0xFFFFC107),  // Amber
    background = Color(0xFFFFF3E0), // Soft Cream
    surface = Color(0xFFFFFFFF),    // Pure White for clean surfaces
    onPrimary = Color.White,        // White text/icons for contrast on orange
    onSecondary = Color.Black,      // Black text/icons for amber contrast
    onBackground = Color.Black,     // Clear black text on light backgrounds
    onSurface = Color.Black         // Ensures readability on surfaces
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFA726),    // Bright Amber-Orange for better visibility
    secondary = Color(0xFFFFC107),  // Golden Yellow for warmth
    background = Color(0xFF121212), // Deep Black for true dark mode
    surface = Color(0xFF1E1E1E),    // Dark Gray for better contrast
    onPrimary = Color.Black,        // Black text/icons for clarity on orange
    onSecondary = Color.Black,      // Ensures contrast on golden yellow
    onBackground = Color.White,     // White text/icons for high contrast
    onSurface = Color(0xFFFAFAFA)   // Slight off-white for better readability
)



// Theme Function
@Composable
fun MyComposeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor:Long,// Auto-detect system theme
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val upColors = colors.copy(primary = Color(primaryColor))

    MaterialTheme(
        colorScheme = upColors,
        typography = Typography,  // Customize typography if needed
        content = content
    )
}
