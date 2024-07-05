package com.darkblue.minimalisttodolistv4.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import com.darkblue.minimalisttodolistv4.data.model.FontFamilyType

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

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

    tertiary = translucentLight,

)

private val DarkColorScheme2 = lightColorScheme(
    primary = White,
    secondary = Black,
    background = Black,
    onBackground = White,
    surface = Black,

    onPrimary = Black,
    onSecondary = White,
    onTertiary = Black,
    onSurface = Black,

    tertiary = translucentDark,
)

@Composable
fun MinimalistTodoListV4Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontFamilyType: FontFamilyType = FontFamilyType.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme2
        else -> LightColorScheme2
    }

//    val customTypography = Typography.copy(
//        bodySmall = Typography.bodySmall.copy(fontFamily = fontFamilyType.getFontFamily()),
//        bodyMedium = Typography.bodyMedium.copy(fontFamily = fontFamilyType.getFontFamily()),
//        bodyLarge = Typography.bodyLarge.copy(fontFamily = fontFamilyType.getFontFamily()),
//        titleLarge = Typography.titleLarge.copy(fontFamily = fontFamilyType.getFontFamily()),
//        headlineSmall = Typography.headlineSmall.copy(fontFamily = fontFamilyType.getFontFamily()),
//    )

    val typography = getCustomTypography(fontFamilyType)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

fun getCustomTypography(fontFamilyType: FontFamilyType): Typography {
    val fontFamily = fontFamilyType.getFontFamily()

    return Typography(
        displayLarge = Typography.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = Typography.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = Typography.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = Typography.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = Typography.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = Typography.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = Typography.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = Typography.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = Typography.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = Typography.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = Typography.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = Typography.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = Typography.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = Typography.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = Typography.labelSmall.copy(fontFamily = fontFamily)
    )
}
