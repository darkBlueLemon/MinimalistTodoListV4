package com.darkblue.minimalisttodolistv4.data.model

import androidx.compose.ui.text.font.FontFamily

enum class FontFamilyType {
    DEFAULT, CURSIVE, SERIF, SANS_SERIF, MONOSPACE;

    fun getFontFamily(): FontFamily {
        return when (this) {
            DEFAULT -> FontFamily.Default
            CURSIVE -> FontFamily.Cursive
            SERIF -> FontFamily.Serif
            SANS_SERIF -> FontFamily.SansSerif
            MONOSPACE -> FontFamily.Monospace
        }
    }
}
