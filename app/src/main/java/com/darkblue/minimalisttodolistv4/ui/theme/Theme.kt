package com.darkblue.minimalisttodolistv4.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
)

@Composable
fun MinimalistTodoListV4Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
    var colorScheme = DarkColorScheme2
    colorScheme = LightColorScheme2

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}