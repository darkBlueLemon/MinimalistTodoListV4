package com.darkblue.minimalisttodolistv4.data

//enum class RecurrenceType {
//    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
//}
//
//fun RecurrenceType.toDisplayString(): String {
//    return when (this) {
//        RecurrenceType.NONE -> "None"
//        RecurrenceType.DAILY -> "Daily"
//        RecurrenceType.WEEKLY -> "Weekly"
//        RecurrenceType.MONTHLY -> "Monthly"
//        RecurrenceType.YEARLY -> "Yearly"
//    }
//}

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY;

    companion object {
        val entries = listOf(DAILY, WEEKLY, MONTHLY, YEARLY)
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
