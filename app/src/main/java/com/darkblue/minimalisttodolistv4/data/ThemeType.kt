package com.darkblue.minimalisttodolistv4.data

enum class ThemeType(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
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
