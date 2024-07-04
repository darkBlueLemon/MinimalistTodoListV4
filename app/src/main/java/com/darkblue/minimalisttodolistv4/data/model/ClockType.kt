package com.darkblue.minimalisttodolistv4.data.model

enum class ClockType {
    TWENTY_FOUR_HOUR,
    TWELVE_HOUR;

    companion object {
        fun fromDisplayName(displayName: String): ClockType {
            return when (displayName) {
                "Military (24-Hour)" -> TWENTY_FOUR_HOUR
                else -> TWELVE_HOUR
            }
        }
    }

    fun toDisplayString(): String {
        return when (this) {
            TWELVE_HOUR -> "Standard (12-Hour)"
            TWENTY_FOUR_HOUR -> "Military (24-Hour)"
        }
    }
}