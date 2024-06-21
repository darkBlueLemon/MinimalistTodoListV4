package com.darkblue.minimalisttodolistv4.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkblue.minimalisttodolistv4.data.SortType
import com.darkblue.minimalisttodolistv4.data.Task
import com.darkblue.minimalisttodolistv4.data.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val dao: TaskDao
): ViewModel() {
    public var taskColor by mutableStateOf(Color(0xFF6200EE)) // Default color


    fun updateTaskColor(newColor: Color) {
        taskColor = newColor
    }

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

    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    dao.deleteTask(event.task)
                }
            }
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
                val completed = state.value.completed

                if(title.isBlank() || note.isBlank()) {
                    return
                }

                val task = Task(
                    title = title,
                    priority = priority,
                    note = note,
                    dueDate = dueDate,
                    completed = completed
                )
                viewModelScope.launch {
                    dao.upsertTask(task)
                }
                _state.update { it.copy(
                    isAddingTask = false,
                    title = "",
                    priority = 0,
                    note = ""
                ) }
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
                    note = event.dueDate.toString()
                ) }
            }
            is TaskEvent.SetCompleted -> {
                _state.update { it.copy(
                    note = event.completed.toString()
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
        }
    }
}