package com.darkblue.minimalisttodolistv4.presentation

import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.SortType
import com.darkblue.minimalisttodolistv4.data.Task

data class TaskState(
    val tasks: List<Task> = emptyList(),
    val title: String = "",
    val priority: Int = 0,
    val note: String = "",
    val dueDate: Long? = null,
    val isAddingTask: Boolean = false,
    val sortType: SortType = SortType.PRIORITY,

    val isDatePickerVisible: Boolean = false,

    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val nextDueDate: Long? = null,

    val editingTaskId: Int? = null
)
