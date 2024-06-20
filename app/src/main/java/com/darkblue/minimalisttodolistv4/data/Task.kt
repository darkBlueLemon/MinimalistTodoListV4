package com.darkblue.minimalisttodolistv4.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val title: String,
    val priority: Int,
    val note: String,
    val dueDate: Long?, // Timestamp in milliseconds
    val completed: Boolean,

    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val nextDueDate: Long? = null,
)