package com.darkblue.minimalisttodolistv4.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.darkblue.minimalisttodolistv4.data.model.FontFamilyType

private val LightColorScheme2 = lightColorScheme(
    primary = Black,
    secondary = White,
    background = White,
    onBackground = Black,
    surface = White,

    onPrimary = White,
    onSecondary = Black,
    onTertiary = White,
    onSurface = White,

    tertiary = TranslucentLight,

    // Calender Arrow Icon Background
    secondaryContainer = Black,

    tertiaryContainer = BulletSecondaryLight
)

private val DarkColorScheme2 = darkColorScheme(
    primary = White,
    secondary = Black,
    background = Black,
    onBackground = White,
    surface = Black,

    onPrimary = Black,
    onSecondary = White,
    onTertiary = Black,
    onSurface = Black,

    tertiary = TranslucentDark,

    // Calender Arrow Icon Background
    secondaryContainer = White,

    tertiaryContainer = BulletSecondaryDark
)

@Composable
fun MinimalistTodoListV4Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontFamilyType: FontFamilyType = FontFamilyType.DEFAULT,
    baseFontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme2 else LightColorScheme2

    val customTypography = Typography(
        displayLarge = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 2.125).sp),
        displayMedium = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 1.5).sp),
        displaySmall = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 1.25).sp),
        headlineLarge = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 2.0).sp),
        headlineMedium = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 1.75).sp),
        headlineSmall = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 1.5).sp),
        titleLarge = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 1.25).sp),
        titleMedium = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 1.0).sp),
        titleSmall = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 0.875).sp),
        bodyLarge = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 1.0).sp),
        bodyMedium = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 0.875).sp),
        bodySmall = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 0.75).sp),
        labelLarge = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 0.875).sp),
        labelMedium = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 0.75).sp),
        labelSmall = TextStyle(fontFamily = fontFamilyType.getFontFamily(), fontWeight = fontWeight, fontSize = (baseFontSize * 0.625).sp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}


