package com.darkblue.minimalisttodolistv4.data.model

import androidx.compose.ui.text.font.FontFamily

enum class FontFamilyType {
    DEFAULT,
    SERIF,
    SANS_SERIF,
    MONOSPACE,
    CURSIVE;

    fun getFontFamily(): FontFamily {
        return when (this) {
            DEFAULT -> FontFamily.Default
            CURSIVE -> FontFamily.Cursive
            SERIF -> FontFamily.Serif
            SANS_SERIF -> FontFamily.SansSerif
            MONOSPACE -> FontFamily.Monospace
        }
    }

    companion object {
        fun fromDisplayName(displayName: String): FontFamilyType {
            return when (displayName) {
                "Cursive" -> CURSIVE
                "Serif" -> SERIF
                "SansSerif" -> SANS_SERIF
                "Monospace" -> MONOSPACE
                else -> DEFAULT
            }
        }
    }

    fun toDisplayString(): String {
        return when (this) {
            DEFAULT -> "Default"
            CURSIVE -> "Cursive"
            SERIF -> "Serif"
            SANS_SERIF -> "SansSerif"
            MONOSPACE -> "Monospace"
        }
    }
}
