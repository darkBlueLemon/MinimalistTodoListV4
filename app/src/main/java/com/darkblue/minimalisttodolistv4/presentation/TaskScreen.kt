package com.darkblue.minimalisttodolistv4.presentation

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkblue.minimalisttodolistv4.data.Task
import com.darkblue.minimalisttodolistv4.ui.theme.Priority0
import com.darkblue.minimalisttodolistv4.ui.theme.Priority1
import com.darkblue.minimalisttodolistv4.ui.theme.Priority2
import com.darkblue.minimalisttodolistv4.ui.theme.Priority3
import com.darkblue.minimalisttodolistv4.ui.theme.dateGray
import com.darkblue.minimalisttodolistv4.ui.theme.dateRed
import java.time.Instant

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    viewModel: TaskViewModel,
    navController: NavController
) {
    val context = LocalContext.current

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
                                vibrate(context = context, strength = 2)
//                                navController.navigate("history")
                                onEvent(TaskEvent.ShowMenuDialog)
                            },
                            onClick = {

                                // Vibrate on click?
                                vibrate(context = context, strength = 1)

                                onEvent(TaskEvent.ShowAddTaskDialog)
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
        if(state.isMenuOpen) {
            MenuDialog(state = state, onEvent = onEvent)
        }
        TaskList(onEvent = onEvent, state = state, viewModel = viewModel)
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
//            Text("Recurrence: ${task.recurrenceType}")
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

fun vibrate(context: Context, strength: Int) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val effect = when (strength) {
            1 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            2 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
            3 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            4 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            else -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
        }
        vibrator.vibrate(effect)
    } else {
        val duration = when (strength) {
            1 -> 50L
            2 -> 100L
            3 -> 150L
            4 -> 200L
            else -> 100L
        }
        vibrator.vibrate(duration) // Fallback for older devices
    }
}