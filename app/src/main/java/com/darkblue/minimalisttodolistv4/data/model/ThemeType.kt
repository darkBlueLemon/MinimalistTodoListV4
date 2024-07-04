package com.darkblue.minimalisttodolistv4.data.model

enum class ThemeType(val displayName: String) {
    DARK("Dark"),
    LIGHT("Light"),
    AUTO("Auto");


    companion object {
        fun fromDisplayName(displayName: String): ThemeType {
            return entries.firstOrNull { it.displayName == displayName } ?: LIGHT
        }
    }

    fun toDisplayString(): String {
        return displayName
    }
}
