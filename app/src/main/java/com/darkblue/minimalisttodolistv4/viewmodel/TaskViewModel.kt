package com.darkblue.minimalisttodolistv4.viewmodel

import android.os.Build
import android.util.Log
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
            is TaskEvent.DeleteTask -> handleDeleteTask(event.task)
            TaskEvent.SaveTask -> handleSaveTask()
            is TaskEvent.SetTitle -> _state.update { it.copy(title = event.title) }
            is TaskEvent.SetPriority -> _state.update { it.copy(priority = event.priority) }
            is TaskEvent.SetNote -> _state.update { it.copy(note = event.note) }
            is TaskEvent.SortTasks -> _sortType.value = event.sortType
            is TaskEvent.SetRecurrenceType -> _state.update { it.copy(recurrenceType = event.recurrenceType) }
            is TaskEvent.EditTask -> handleEditTask(event.task)
            is TaskEvent.SetRecurrenceFilter -> _recurrenceFilter.value = event.recurrenceType
            TaskEvent.ShowDatePicker -> _state.update { it.copy(isDatePickerVisible = true) }
            TaskEvent.HideDatePicker -> _state.update { it.copy(isDatePickerVisible = false) }
            is TaskEvent.SetDueDate -> handleSetDueDate(event.dueDate)
            TaskEvent.ShowTimePicker -> _state.update { it.copy(isTimePickerVisible = true) }
            TaskEvent.HideTimePicker -> _state.update { it.copy(isTimePickerVisible = false) }
            is TaskEvent.SetDueTime -> handleSetDueTime(event.dueTime)
            is TaskEvent.DeleteForever -> handleDeleteForever(event.deletedTask)
            is TaskEvent.UndoDeleteTask -> handleUndoDeleteTask(event.deletedTask)
            TaskEvent.ShowAddTaskDialog -> _state.update { it.copy(isAddTaskDialogVisible = true) }
            TaskEvent.HideAddTaskDialog -> resetAddTaskDialog()
            TaskEvent.DeleteAllHistoryTasks -> viewModelScope.launch { dao.deleteAllDeletedTasks() }
            TaskEvent.RefreshTasks -> reloadTasks()
        }
    }

    private fun handleDeleteTask(task: Task) {
        viewModelScope.launch {
            delay(500)
            notificationHelper.cancelNotification(task.id.toInt())
            dao.deleteTask(task)
            dao.insertDeletedTask(task.toDeletedTask())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSaveTask() {
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

        resetAddTaskDialog()
    }

    private fun handleEditTask(task: Task) {
        _state.update {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSetDueDate(dueDate: LocalDate) {
        _state.update {
            it.copy(dueDateOnly = dueDate).also { updatedState ->
                combineDateAndTime(updatedState)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSetDueTime(dueTime: LocalTime) {
        _state.update {
            it.copy(dueTimeOnly = dueTime).also { updatedState ->
                combineDateAndTime(updatedState)
            }
        }
    }

    private fun handleDeleteForever(deletedTask: DeletedTask) {
        viewModelScope.launch {
            delay(500)
            dao.deleteDeletedTask(deletedTask)
        }
    }

    private fun handleUndoDeleteTask(deletedTask: DeletedTask) {
        viewModelScope.launch {
            delay(500)
            val restoredTask = deletedTask.toTask()
            dao.upsertTask(restoredTask)
            dao.deleteDeletedTask(deletedTask)
        }
    }

    private fun resetAddTaskDialog() {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun combineDateAndTime(state: TaskState) {
        val date = state.dueDateOnly
        val time = state.dueTimeOnly
        if (date != null) {
            val combinedDateTime = if (time != null) date.atTime(time) else date.atStartOfDay()
            _state.update {
                it.copy(dueDate = combinedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            }
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