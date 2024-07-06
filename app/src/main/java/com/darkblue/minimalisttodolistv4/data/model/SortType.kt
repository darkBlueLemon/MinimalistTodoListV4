package com.darkblue.minimalisttodolistv4.data.model

enum class SortType {
    PRIORITY,
    DUE_DATE,
    ALPHABETICAL,
    ALPHABETICAL_REV;

    companion object {
        fun fromDisplayName(displayName: String): SortType {
            return when(displayName) {
                "Alphabetical" -> ALPHABETICAL
                "Alphabetical z-a" -> ALPHABETICAL_REV
                "Time Remaining" -> DUE_DATE
                "Priority" -> PRIORITY
                else -> PRIORITY
            }
        }
    }

    fun toDisplayString(): String {
        return when (this) {
            ALPHABETICAL -> "Alphabetical"
            ALPHABETICAL_REV -> "Alphabetical z-a"
            DUE_DATE -> "Time Remaining"
            PRIORITY -> "Priority"
        }
    }
}