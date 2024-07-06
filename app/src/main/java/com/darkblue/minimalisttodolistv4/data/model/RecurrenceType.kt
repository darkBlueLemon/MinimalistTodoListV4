package com.darkblue.minimalisttodolistv4.data.model

import com.darkblue.minimalisttodolistv4.data.model.SortType.ALPHABETICAL
import com.darkblue.minimalisttodolistv4.data.model.SortType.ALPHABETICAL_REV
import com.darkblue.minimalisttodolistv4.data.model.SortType.DUE_DATE
import com.darkblue.minimalisttodolistv4.data.model.SortType.PRIORITY

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY;

    companion object {
        val entriesWithoutNONE = listOf(DAILY, WEEKLY, MONTHLY, YEARLY)
        val entriesWithNONE = listOf(NONE, DAILY, WEEKLY, MONTHLY, YEARLY)
        fun fromDisplayName(displayName: String): RecurrenceType {
            return when(displayName) {
                "Show All" -> NONE
                "Daily" -> DAILY
                "Weekly" -> WEEKLY
                "Monthly" -> MONTHLY
                "Yearly" -> YEARLY
                else -> NONE
            }
        }
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
