package com.minimalisttodolist.pleasebethelastrecyclerview.ui.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object NoRippleTheme: RippleTheme {
    @Composable
    override fun defaultColor(): Color = Color.Unspecified

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        0.0f, 0.0f, 0.0f, 0.0f
    )
}