package com.darkblue.minimalisttodolistv4.data

enum class ThemeType {
    LIGHT, DARK, AUTO;

    fun toDisplayString(): String {
        return when (this) {
            LIGHT -> "Light"
            DARK -> "Dark"
            AUTO -> "Auto"
        }
    }
}
