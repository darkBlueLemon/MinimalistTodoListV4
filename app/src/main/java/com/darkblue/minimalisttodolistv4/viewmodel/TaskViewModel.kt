package com.darkblue.minimalisttodolistv4.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val dao: TaskDao,
    private val notificationHelper: NotificationHelper,
    private val dataStoreViewModel: DataStoreViewModel
) : ViewModel() {

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
       initializeDataStore()
       observeDeletedTasks()
       reloadTasks()
   }

    private fun initializeDataStore() {
        viewModelScope.launch {
            dataStoreViewModel.priorityOption.collect { _sortType.value = it }
        }
        viewModelScope.launch {
            dataStoreViewModel.recurrenceFilter.collect { _recurrenceFilter.value = it }
        }
    }

    private fun observeDeletedTasks() {
        viewModelScope.launch {
            dao.getDeletedTasks().collect { _deletedTasks.value = it }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.SaveTask -> saveTask()
            is TaskEvent.SetTitle -> updateState { it.copy(title = event.title) }
            is TaskEvent.SetPriority -> updateState { it.copy(priority = event.priority) }
            is TaskEvent.SetNote -> updateState { it.copy(note = event.note) }
            is TaskEvent.SortTasks -> _sortType.value = event.sortType
            is TaskEvent.SetRecurrenceType -> updateState { it.copy(recurrenceType = event.recurrenceType) }
            is TaskEvent.EditTask -> editTask(event.task)
            is TaskEvent.SetRecurrenceFilter -> _recurrenceFilter.value = event.recurrenceType
            is TaskEvent.ShowDatePicker -> updateState { it.copy(isDatePickerVisible = true) }
            is TaskEvent.HideDatePicker -> updateState { it.copy(isDatePickerVisible = false) }
            is TaskEvent.SetDueDate -> updateState { it.copy(dueDateOnly = event.dueDate).also(::combineDateAndTime) }
            is TaskEvent.ShowTimePicker -> updateState { it.copy(isTimePickerVisible = true) }
            is TaskEvent.HideTimePicker -> updateState { it.copy(isTimePickerVisible = false) }
            is TaskEvent.SetDueTime -> updateState { it.copy(dueTimeOnly = event.dueTime).also(::combineDateAndTime) }
            is TaskEvent.DeleteTask -> deleteTask(event.task)
            is TaskEvent.DeleteForever -> deleteForever(event.deletedTask)
            is TaskEvent.UndoDeleteTask -> undoDeleteTask(event.deletedTask)
            is TaskEvent.ShowAddTaskDialog -> showAddTaskDialog()
            is TaskEvent.HideAddTaskDialog -> hideAddTaskDialog()
            is TaskEvent.DeleteAllHistoryTasks -> deleteAllHistoryTasks()
            is TaskEvent.RefreshTasks -> reloadTasks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTask() {
        val currentState = state.value
        if (currentState.title.isBlank()) return

        val task = Task(
            id = currentState.editingTaskId ?: 0,
            title = currentState.title,
            priority = currentState.priority,
            note = currentState.note,
            dueDate = currentState.dueDate,
            recurrenceType = currentState.recurrenceType
        )

        viewModelScope.launch {
            val taskId = dao.upsertTask(task)
            val savedTask = dao.getTaskById(if (taskId.toInt() == -1) currentState.editingTaskId!! else taskId.toInt())
            savedTask?.let { notificationHelper.scheduleNotification(it) }
        }

        resetAddTaskDialogState()
    }

    private fun editTask(task: Task) {
        updateState {
            it.copy(
                title = task.title,
                priority = task.priority,
                note = task.note,
                dueDate = task.dueDate,
                recurrenceType = task.recurrenceType,
                isAddTaskDialogVisible = true,
                editingTaskId = task.id
            )
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch {
            delay(500)
            notificationHelper.cancelNotification(task.id.toInt())
            dao.deleteTask(task)
            dao.insertDeletedTask(task.toDeletedTask())
        }
    }

    private fun deleteForever(deletedTask: DeletedTask) {
        viewModelScope.launch {
            delay(500)
            dao.deleteDeletedTask(deletedTask)
        }
    }

    private fun undoDeleteTask(deletedTask: DeletedTask) {
        viewModelScope.launch {
            delay(500)
            val restoredTask = deletedTask.toTask()
            dao.upsertTask(restoredTask)
            dao.deleteDeletedTask(deletedTask)
        }
    }

    private fun showAddTaskDialog() {
        updateState { it.copy(isAddTaskDialogVisible = true) }
    }

    private fun hideAddTaskDialog() {
        updateState {
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

    private fun deleteAllHistoryTasks() {
        viewModelScope.launch {
            dao.deleteAllDeletedTasks()
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
                    }.map { tasks -> tasks.filter { it.recurrenceType == recurrenceType || recurrenceType == RecurrenceType.NONE } }
                }.collect { tasks ->
                    _tasks.value = tasks
                }
        }
    }

    private fun updateState(update: (TaskState) -> TaskState) {
        _state.value = update(_state.value)
    }

    private fun resetAddTaskDialogState() {
        updateState {
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

    private fun Task.toDeletedTask(): DeletedTask {
        return DeletedTask(
            id = this.id,
            title = this.title,
            priority = this.priority,
            note = this.note,
            dueDate = this.dueDate,
            recurrenceType = this.recurrenceType,
            deletedAt = System.currentTimeMillis()
        )
    }

    private fun DeletedTask.toTask(): Task {
        return Task(
            id = this.id,
            title = this.title,
            priority = this.priority,
            note = this.note,
            dueDate = this.dueDate,
            recurrenceType = this.recurrenceType,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun combineDateAndTime(state: TaskState) {
        val date = state.dueDateOnly
        val time = state.dueTimeOnly
        if (date != null && time != null) {
            val dueDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault())
            val nextDueDate = calculateNextDueDate(dueDateTime, state.recurrenceType)
            updateState { it.copy(dueDate = dueDateTime.toInstant().toEpochMilli(), nextDueDate = nextDueDate) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextDueDate(dueDateTime: ZonedDateTime, recurrenceType: RecurrenceType): Long? {
        return when (recurrenceType) {
            RecurrenceType.DAILY -> dueDateTime.plusDays(1).toInstant().toEpochMilli()
            RecurrenceType.WEEKLY -> dueDateTime.plusWeeks(1).toInstant().toEpochMilli()
            RecurrenceType.MONTHLY -> dueDateTime.plusMonths(1).toInstant().toEpochMilli()
            RecurrenceType.YEARLY -> dueDateTime.plusYears(1).toInstant().toEpochMilli()
            else -> null
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

class TaskViewModelFactory(
    private val dao: TaskDao,
    private val notificationHelper: NotificationHelper,
    private val dataStoreViewModel: DataStoreViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(dao, notificationHelper, dataStoreViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}