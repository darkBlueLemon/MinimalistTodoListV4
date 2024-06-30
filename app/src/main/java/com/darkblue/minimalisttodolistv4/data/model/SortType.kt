package com.darkblue.minimalisttodolistv4.data.model

enum class SortType {
    PRIORITY,
    DUE_DATE,
    ALPHABETICAL,
    ALPHABETICAL_REV;

    fun toDisplayString(): String {
        return when (this) {
            ALPHABETICAL -> "Alphabetical"
            ALPHABETICAL_REV -> "Alphabetical z-a"
            DUE_DATE -> "Time Remaining"
            PRIORITY -> "Priority"
        }
    }
}