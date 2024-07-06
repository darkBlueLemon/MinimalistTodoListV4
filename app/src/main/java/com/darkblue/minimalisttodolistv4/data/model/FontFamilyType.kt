package com.darkblue.minimalisttodolistv4.data.model

import androidx.compose.ui.text.font.FontFamily

enum class FontFamilyType(val displayName: String) {
    DEFAULT("Default"),
    SERIF("Serif"),
    SANS_SERIF("SansSerif"),
    MONOSPACE("Monospace"),
    CURSIVE("Cursive");

    companion object {
        fun fromDisplayName(displayName: String): FontFamilyType {
            return entries.firstOrNull { it.displayName == displayName } ?: DEFAULT
        }
    }

    fun toDisplayString(): String = displayName

    fun getFontFamily(): FontFamily {
        return when (this) {
            DEFAULT -> FontFamily.Default
            SERIF -> FontFamily.Serif
            SANS_SERIF -> FontFamily.SansSerif
            MONOSPACE -> FontFamily.Monospace
            CURSIVE -> FontFamily.Cursive
        }
    }
}