package com.darkblue.minimalisttodolistv4.data.model

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY;

    companion object {
        val entriesWithoutNONE = listOf(DAILY, WEEKLY, MONTHLY, YEARLY)
        val entriesWithNONE = listOf(NONE, DAILY, WEEKLY, MONTHLY, YEARLY)
    }

    fun toDisplayString(): String {
        return when (this) {
            NONE -> "Show All"
            DAILY -> "Daily"
            WEEKLY -> "Weekly"
            MONTHLY -> "Monthly"
            YEARLY -> "Yearly"
        }
    }
}