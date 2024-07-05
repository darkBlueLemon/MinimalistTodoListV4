package com.darkblue.minimalisttodolistv4.data.model

enum class ClockType {
    TWENTY_FOUR_HOUR,
    TWELVE_HOUR;

    companion object {
        fun fromDisplayName(displayName: String): ClockType {
            return when (displayName) {
                "24-Hour" -> TWENTY_FOUR_HOUR
                else -> TWELVE_HOUR
            }
        }
    }

    fun toDisplayString(): String {
        return when (this) {
            TWELVE_HOUR -> "12-Hour"
            TWENTY_FOUR_HOUR -> "24-Hour"
        }
    }
}