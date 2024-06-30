package com.darkblue.minimalisttodolistv4.data.model

enum class SortType {
    ALPHABETICAL,
    ALPHABETICAL_REV,
    DUE_DATE,
    PRIORITY;

    fun toDisplayString(): String {
        return when (this) {
            ALPHABETICAL -> "Alphabetical"
            ALPHABETICAL_REV -> "Alphabetical z-a"
            DUE_DATE -> "Time Remaining"
            PRIORITY -> "Priority"
        }
    }
}