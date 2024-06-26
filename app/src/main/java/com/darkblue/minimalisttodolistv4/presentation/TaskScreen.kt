package com.darkblue.minimalisttodolistv4.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.SortType
import com.darkblue.minimalisttodolistv4.data.Task
import com.darkblue.minimalisttodolistv4.ui.theme.Priority0
import com.darkblue.minimalisttodolistv4.ui.theme.Priority1
import com.darkblue.minimalisttodolistv4.ui.theme.Priority2
import com.darkblue.minimalisttodolistv4.ui.theme.Priority3
import com.darkblue.minimalisttodolistv4.ui.theme.dateGray
import com.darkblue.minimalisttodolistv4.ui.theme.dateRed
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    viewModel: TaskViewModel,
    navController: NavController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),

                modifier = Modifier
                    .clip(shape = RoundedCornerShape(percent = 7))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(percent = 25)
                    ),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add task",
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = {
                                navController.navigate("history")
                            }, onClick = {
                                onEvent(TaskEvent.ShowDialog)
                            }
                        )
                )
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
            TaskList(onEvent = onEvent, state = state, viewModel = viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(onEvent: (TaskEvent) -> Unit, state: TaskState, viewModel: TaskViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(state.tasks) { task ->
            TaskItem(
                task = task,
                onEdit = { onEvent(TaskEvent.EditTask(it)) },
                onDelete = { onEvent(TaskEvent.DeleteTask(it)) },
                viewModel = viewModel
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(task: Task, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit, viewModel: TaskViewModel) {
    val priorityColor = when (task.priority) {
        3 -> Priority3
        2 -> Priority2
        1 -> Priority1
        else -> Priority0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Priority",
            tint = priorityColor,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .clickable { onEdit(task) }
        ) {
            Text(
                task.title,
                modifier = Modifier
//                    .padding(start = 5.dp)
//                    .padding(end = 8.dp),
                    .widthIn(max = 280.dp)
            )
            Text("Recurrence: ${task.recurrenceType}")
            DueDateNote(task = task, viewModel = viewModel)
        }
        IconButton(onClick = { onDelete(task) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DueDateNote(
    modifier: Modifier = Modifier,
    task: Task,
    viewModel: TaskViewModel
) {
    val dueDate = viewModel.formatDueDate(task.dueDate)
    val nextDueDate = viewModel.formatDueDate(task.nextDueDate)
    val note = task.note.orEmpty()

    val textColor = if (task.dueDate?.let { Instant.ofEpochMilli(it).isBefore(Instant.now()) } == true) dateRed else dateGray

    Column {
        if (dueDate.isNotEmpty()) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(dueDate)
                    }
                    if (note.isNotBlank()) {
                        append(" | ")
                        append(note)
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (nextDueDate.isNotEmpty() && nextDueDate != dueDate) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Transparent)) {
                        append(nextDueDate)
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
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
