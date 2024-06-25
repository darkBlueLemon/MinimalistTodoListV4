package com.darkblue.minimalisttodolistv4.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.SortType
import com.darkblue.minimalisttodolistv4.data.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    navController: NavController
) {
    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(onClick = {
                    onEvent(TaskEvent.ShowDialog)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task"
                    )
                }
                FloatingActionButton(onClick = {
                    navController.navigate("history")
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "History"
                    )
                }
            }
        },
    ) { padding ->
        padding
        if(state.isAddingTask) {
            AddTaskDialog(state = state, onEvent = onEvent)
        }
        Column {
            SortAndFilterControls(
                currentSortType = state.sortType,
                onSortChange = { onEvent(TaskEvent.SortTasks(it)) },
                currentRecurrenceFilter = state.recurrenceFilter,
                onRecurrenceFilterChange = { onEvent(TaskEvent.SetRecurrenceFilter(it)) }
            )
            TaskList(onEvent = onEvent, state = state)
        }
    }
}

@Composable
fun TaskList(onEvent: (TaskEvent) -> Unit, state: TaskState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(state.tasks) { task ->
            TaskItem(
                task = task,
                onEdit = { onEvent(TaskEvent.EditTask(it)) },
                onDelete = { onEvent(TaskEvent.DeleteTask(it)) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(task: Task, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Column {
            Text("Title: ${task.title}")
            Text("Priority: ${task.priority}")
            Text("Note: ${task.note}")
            // Inside your Composable function
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val dueDate = task.dueDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter)
            } ?: ""
            Text("Due Date: $dueDate")
            Text("Recurrence: ${task.recurrenceType}")
            val nextDueDate = task.nextDueDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter)
            } ?: ""
            Text("Next Due Date: $nextDueDate")
        }
        Row {
            IconButton(onClick = { onEdit(task) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { onDelete(task) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun SortAndFilterControls(
    currentSortType: SortType,
    onSortChange: (SortType) -> Unit,
    currentRecurrenceFilter: RecurrenceType,
    onRecurrenceFilterChange: (RecurrenceType) -> Unit
) {
    Column {
        Text("Sort by:")
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ){
            SortType.entries.forEach { sortType ->
                RadioButton(
                    selected = currentSortType == sortType,
                    onClick = { onSortChange(sortType) }
                )
                Text(sortType.name)
            }
        }

        Text("Filter by Recurrence:")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecurrenceType.entries.forEach { recurrenceType ->
                RadioButton(
                    selected = currentRecurrenceFilter == recurrenceType,
                    onClick = { onRecurrenceFilterChange(recurrenceType) }
                )
                Text(recurrenceType.name)
            }
        }
    }
}
