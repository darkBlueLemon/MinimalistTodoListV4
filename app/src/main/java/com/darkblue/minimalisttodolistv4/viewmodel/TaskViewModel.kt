package com.darkblue.minimalisttodolistv4.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkblue.minimalisttodolistv4.util.NotificationHelper
import com.darkblue.minimalisttodolistv4.data.model.DeletedTask
import com.darkblue.minimalisttodolistv4.data.model.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.model.SortType
import com.darkblue.minimalisttodolistv4.data.model.Task
import com.darkblue.minimalisttodolistv4.data.database.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val dao: TaskDao,
    private val notificationHelper: NotificationHelper
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.PRIORITY)
    private val _recurrenceFilter = MutableStateFlow(RecurrenceType.NONE)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    private val _state = MutableStateFlow(TaskState())
    val state = combine(_state, _sortType, _recurrenceFilter, _tasks) { state, sortType, recurrenceType, tasks ->
        state.copy(
            tasks = tasks,
            sortType = sortType,
            recurrenceFilter = recurrenceType
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
        reloadTasks()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    val task = event.task
                    delay(500)
                    notificationHelper.cancelNotification(event.task.id.toInt())
                    dao.deleteTask(task)
                    Log.d("TAG", task.id.toString())
                    dao.insertDeletedTask(
                        DeletedTask(
                            title = task.title,
                            priority = task.priority,
                            note = task.note,
                            dueDate = task.dueDate,
                            recurrenceType = task.recurrenceType,
//                            nextDueDate = task.notificationTime,
                            deletedAt = System.currentTimeMillis()
                        )
                    )

                    // Emit new value to _tasks after deletion
//                    reloadTasks()
                }
            }
            TaskEvent.SaveTask -> {
                val title = state.value.title
                val priority = state.value.priority
                val note = state.value.note
                val dueDate = state.value.dueDate

                val recurrenceType = state.value.recurrenceType
//                val nextDueDate = calculateNextDueDate(dueDate, recurrenceType)

                val id = state.value.editingTaskId

                if(title.isBlank()) {
                    return
                }

                val task = Task(
                    id = id ?: 0,
                    title = title,
                    priority = priority,
                    note = note,
                    dueDate = dueDate,
                    recurrenceType = recurrenceType,
//                    notificationTime = nextDueDate
                )

                viewModelScope.launch {
                    val taskId = dao.upsertTask(task)
                    val savedTask = if (taskId.toInt() == -1) dao.getTaskById(id!!) else dao.getTaskById(taskId.toInt())
//                    Log.d("TAG", savedTask!!.title)
                    Log.d("TAG", id.toString())
                    savedTask?.let {
                        notificationHelper.scheduleNotification(it)
                    }

                    // Emit new value to _tasks after saving
//                    reloadTasks()
                }
                _state.update {
                    it.copy(
                        isAddTaskDialogVisible = false,
                        title = "",
                        priority = 0,
                        note = "",
                        dueDate = null,
                        dueTimeOnly = null,
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
            is TaskEvent.SortTasks -> {
                _sortType.value = event.sortType
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
//                        nextDueDate = event.task.notificationTime,
                        isAddTaskDialogVisible = true,
                        editingTaskId = event.task.id
                    )
                }
            }

            // Recurrence Filter
            is TaskEvent.SetRecurrenceFilter -> {
                _recurrenceFilter.value = event.recurrenceType
            }

            // Date Picker
            TaskEvent.ShowDatePicker -> {
                _state.update { it.copy(
                    isDatePickerVisible = true
                ) }
            }
            TaskEvent.HideDatePicker -> {
                _state.update { it.copy(
                    isDatePickerVisible = false
                ) }
            }
            is TaskEvent.SetDueDate -> {
                _state.update { it.copy(
                    dueDateOnly = event.dueDate
                ).also {
                    combineDateAndTime(it)
                } }
            }

            // Time Picker
            TaskEvent.ShowTimePicker -> {
                _state.update { it.copy(
                    isTimePickerVisible = true
                ) }
            }
            TaskEvent.HideTimePicker -> {
                _state.update { it.copy(
                    isTimePickerVisible = false
                ) }
            }
            is TaskEvent.SetDueTime -> {
                _state.update { it.copy(
                    dueTimeOnly = event.dueTime
                ).also {
                    combineDateAndTime(it)
                } }
            }

            // Deletion Events
            is TaskEvent.DeleteForever -> {
                viewModelScope.launch {
                    delay(500)
                    dao.deleteDeletedTask(event.deletedTask)
                }
            }
            is TaskEvent.UndoDeleteTask -> {
                viewModelScope.launch {
                    delay(500)
                    val deletedTask = event.deletedTask
                    val restoredTask = Task(
                        title = deletedTask.title,
                        priority = deletedTask.priority,
                        note = deletedTask.note,
                        dueDate = deletedTask.dueDate,
                        recurrenceType = deletedTask.recurrenceType,
//                        notificationTime = deletedTask.nextDueDate
                    )
                    dao.upsertTask(restoredTask)
                    dao.deleteDeletedTask(deletedTask)
                }
            }

            TaskEvent.ShowAddTaskDialog -> {
                _state.update { it.copy(
                    isAddTaskDialogVisible = true
                ) }
            }
            TaskEvent.HideAddTaskDialog -> {
                _state.update { it.copy(
                    isAddTaskDialogVisible = false,
                    title = "",
                    priority = 0,
                    note = "",
                    dueDate = null,
                    dueTimeOnly = null,
                    recurrenceType = RecurrenceType.NONE,
                    nextDueDate = null,
                    editingTaskId = null
                ) }
            }
            is TaskEvent.DeleteAllHistoryTasks -> {
                viewModelScope.launch {
                    dao.deleteAllDeletedTasks()
                }
            }

            is TaskEvent.RefreshTasks -> {
                viewModelScope.launch {
                    reloadTasks()
                }
            }
        }
    }

    fun reloadTasks() {
        viewModelScope.launch {
            combine(_sortType, _recurrenceFilter) { sortType, recurrenceType ->
                Pair(sortType, recurrenceType)
            }
                .flatMapLatest { (sortType, recurrenceType) ->
                    when (sortType) {
                        SortType.ALPHABETICAL -> dao.getTasksOrderedAlphabetically()
                        SortType.ALPHABETICAL_REV -> dao.getTasksOrderedAlphabeticallyRev()
                        SortType.DUE_DATE -> dao.getTasksSortedByDueDate()
                        SortType.PRIORITY -> dao.getTasksSortedByPriority()
                    }.map { tasks ->
                        tasks.filter { task ->
                            recurrenceType == RecurrenceType.NONE || task.recurrenceType == recurrenceType
                        }
                    }
                }
                .collect { tasks ->
                    _tasks.value = tasks
                }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun calculateNextDueDate(dueDate: Long?, recurrenceType: RecurrenceType): Long? {
//        if (dueDate == null) return null
//        val date = Instant.ofEpochMilli(dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
//        val nextDate = when (recurrenceType) {
//            RecurrenceType.DAILY -> date.plusDays(1)
//            RecurrenceType.WEEKLY -> date.plusWeeks(1)
//            RecurrenceType.MONTHLY -> date.plusMonths(1)
//            RecurrenceType.YEARLY -> date.plusYears(1)
//            else -> return null
//        }
//        return nextDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun combineDateAndTime(state: TaskState) {
        val date = state.dueDateOnly
        val time = state.dueTimeOnly
        if (date != null) {
            val combinedDateTime = if(time != null) date.atTime(time) else date.atStartOfDay()
            _state.update { it.copy(
                dueDate = combinedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            ) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDueDateWithDateTime(epochMilli: Long?): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        val dateFormatterWithoutTime = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val dateFormatterCurrentYear = DateTimeFormatter.ofPattern("MMM dd")
        val dateTimeFormatterCurrentYear = DateTimeFormatter.ofPattern("MMM dd HH:mm")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentYear = LocalDateTime.now().year
        val today = LocalDateTime.now().toLocalDate()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        epochMilli ?: return ""
        val dateTime = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val isMidnight = dateTime.hour == 0 && dateTime.minute == 0
        return when (dateTime.toLocalDate()) {
            today -> "Today" + if (!isMidnight) " ${dateTime.format(timeFormatter)}" else ""
            yesterday -> "Yesterday" + if (!isMidnight) " ${dateTime.format(timeFormatter)}" else ""
            tomorrow -> "Tomorrow" + if (!isMidnight) " ${dateTime.format(timeFormatter)}" else ""
            else -> {
                if (dateTime.year == currentYear) {
                    if (isMidnight) dateTime.format(dateFormatterCurrentYear)
                    else dateTime.format(dateTimeFormatterCurrentYear)
                } else {
                    if (isMidnight) dateTime.format(dateFormatterWithoutTime)
                    else dateTime.format(formatter)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDueDateWithDateOnly(epochMilli: Long?): String {
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val dateFormatterCurrentYear = DateTimeFormatter.ofPattern("MMM dd")
        val currentYear = LocalDateTime.now().year
        val today = LocalDateTime.now().toLocalDate()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        epochMilli ?: return ""
        val dateTime = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return when (dateTime.toLocalDate()) {
            today -> "Today"
            yesterday -> "Yesterday"
            tomorrow -> "Tomorrow"
            else -> {
                if (dateTime.year == currentYear) dateTime.format(dateFormatterCurrentYear)
                else dateTime.format(dateFormatter)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDueDateWithTimeOnly(epochMilli: Long?): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        epochMilli ?: return ""
        val dateTime = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime()
        if (dateTime.hour == 0 && dateTime.minute == 0) {
            return ""
        }
        return dateTime.format(timeFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDateFromEpochMilli(epochMilli: Long?): LocalDate {
        epochMilli ?: return LocalDate.now()
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalTimeFromEpochMilli(epochMilli: Long?): LocalTime {
        epochMilli ?: return LocalTime.now()
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalTime()
    }
}