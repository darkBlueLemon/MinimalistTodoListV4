package com.darkblue.minimalisttodolistv4.data

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY;

    companion object {
        val entriesWithoutNONE = listOf(DAILY, WEEKLY, MONTHLY, YEARLY)
        val entriesWithNONE = listOf(NONE, DAILY, WEEKLY, MONTHLY, YEARLY)
    }

    fun toDisplayString(): String {
        return when (this) {
            NONE -> "None"
            DAILY -> "Daily"
            WEEKLY -> "Weekly"
            MONTHLY -> "Monthly"
            YEARLY -> "Yearly"
        }
    }
}
