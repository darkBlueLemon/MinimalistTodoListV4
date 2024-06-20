package com.darkblue.minimalisttodolistv4.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkblue.minimalisttodolistv4.data.DeletedTask
import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.SortType
import com.darkblue.minimalisttodolistv4.data.Task
import com.darkblue.minimalisttodolistv4.data.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val dao: TaskDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.PRIORITY)
    private val _tasks = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.ALPHABETICAL -> dao.getTasksOrderedAlphabetically()
                SortType.ALPHABETICAL_REV -> dao.getTasksOrderedAlphabeticallyRev()
                SortType.DUE_DATE -> dao.getTasksSortedByDueDate()
                SortType.PRIORITY -> dao.getTasksSortedByPriority()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(TaskState())
    val state = combine(_state, _sortType, _tasks) { state, sortType, tasks ->
        state.copy(
            tasks = tasks,
            sortType = sortType,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskState())

    private val _deletedTasks = MutableStateFlow<List<DeletedTask>>(emptyList())
    val deletedTasks: StateFlow<List<DeletedTask>> = _deletedTasks

    init {
        viewModelScope.launch {
            dao.getDeletedTasks().collect { deletedTasks ->
                _deletedTasks.value = deletedTasks
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    val task = event.task
                    dao.deleteTask(task)
                    dao.insertDeletedTask(
                        DeletedTask(
                            title = task.title,
                            priority = task.priority,
                            note = task.note,
                            dueDate = task.dueDate,
                            recurrenceType = task.recurrenceType,
                            nextDueDate = task.nextDueDate,
                            deletedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
//            is TaskEvent.DeleteTask -> {
//                viewModelScope.launch {
//                    dao.deleteTask(event.task)
//                }
//            }
            TaskEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingTask = false
                ) }
            }
            TaskEvent.SaveTask -> {
                val title = state.value.title
                val priority = state.value.priority
                val note = state.value.note
                val dueDate = state.value.dueDate

                val recurrenceType = state.value.recurrenceType
                val nextDueDate = calculateNextDueDate(dueDate, recurrenceType)

                val id = state.value.editingTaskId

                if(title.isBlank() || note.isBlank()) {
                    return
                }

                val task = Task(
                    title = title,
                    priority = priority,
                    note = note,
                    dueDate = dueDate,
                    recurrenceType = recurrenceType,
                    nextDueDate = nextDueDate
                )
                if(id != null) task.id = id
                viewModelScope.launch {
                    dao.upsertTask(task)
                }
                _state.update {
                    it.copy(
                        isAddingTask = false,
                        title = "",
                        priority = 0,
                        note = "",
                        dueDate = null,
                        recurrenceType = RecurrenceType.NONE,
                        nextDueDate = null,
                        editingTaskId = null
                    )
                }
            }
            is TaskEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                ) }
            }
            is TaskEvent.SetPriority -> {
                _state.update { it.copy(
                    priority = event.priority
                ) }
            }
            is TaskEvent.SetNote -> {
                _state.update { it.copy(
                    note = event.note
                ) }
            }
            is TaskEvent.SetDueDate -> {
                _state.update { it.copy(
                    dueDate = event.dueDate
                ) }
            }
            TaskEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingTask = true
                ) }
            }
            is TaskEvent.SortTasks -> {
                _sortType.value = event.sortType
            }

            // Date Picker
            TaskEvent.ShowDatePicker -> {
                Log.d("TAG", "viewModel")
                _state.update { it.copy(
                    isDatePickerVisible = true
                ) }
            }
            TaskEvent.HideDatePicker -> {
                _state.update { it.copy(
                    isDatePickerVisible = false
                ) }
            }

            // Recurrence
            is TaskEvent.SetRecurrenceType -> {
                _state.update { it.copy(recurrenceType = event.recurrenceType) }
            }

            // Editing
            is TaskEvent.EditTask -> {
                _state.update {
                    it.copy(
                        title = event.task.title,
                        priority = event.task.priority,
                        note = event.task.note,
                        dueDate = event.task.dueDate,
                        recurrenceType = event.task.recurrenceType,
                        nextDueDate = event.task.nextDueDate,
                        isAddingTask = true,
                        editingTaskId = event.task.id
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextDueDate(dueDate: Long?, recurrenceType: RecurrenceType): Long? {
        if (dueDate == null) return null
        val date = Instant.ofEpochMilli(dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val nextDate = when (recurrenceType) {
            RecurrenceType.DAILY -> date.plusDays(1)
            RecurrenceType.WEEKLY -> date.plusWeeks(1)
            RecurrenceType.MONTHLY -> date.plusMonths(1)
            RecurrenceType.YEARLY -> date.plusYears(1)
            else -> return null
        }
        return nextDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}