package com.darkblue.minimalisttodolistv4.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.model.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.model.Task
import com.darkblue.minimalisttodolistv4.ui.components.CustomBox
import com.darkblue.minimalisttodolistv4.ui.components.emptyStateMessages
import com.darkblue.minimalisttodolistv4.viewmodel.DataStoreViewModel
import com.darkblue.minimalisttodolistv4.viewmodel.TaskEvent
import com.darkblue.minimalisttodolistv4.viewmodel.TaskState
import com.darkblue.minimalisttodolistv4.viewmodel.TaskViewModel
import com.darkblue.minimalisttodolistv4.ui.dialogs.AddTaskDialog
import com.darkblue.minimalisttodolistv4.ui.dialogs.FontSettingsDialog
import com.darkblue.minimalisttodolistv4.ui.dialogs.HistoryDialog
import com.darkblue.minimalisttodolistv4.ui.dialogs.MenuDialog
import com.darkblue.minimalisttodolistv4.ui.dialogs.ScheduleExactAlarmPermissionDialog
import com.darkblue.minimalisttodolistv4.ui.dialogs.Tutorial
import com.darkblue.minimalisttodolistv4.viewmodel.AppEvent
import com.darkblue.minimalisttodolistv4.viewmodel.AppState
import com.darkblue.minimalisttodolistv4.ui.theme.Priority0
import com.darkblue.minimalisttodolistv4.ui.theme.Priority1
import com.darkblue.minimalisttodolistv4.ui.theme.Priority2
import com.darkblue.minimalisttodolistv4.ui.theme.Priority3
import com.darkblue.minimalisttodolistv4.ui.theme.DateRed
import com.darkblue.minimalisttodolistv4.util.vibrate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    taskState: TaskState,
    appState: AppState,
    onEvent: (TaskEvent) -> Unit,
    onAppEvent: (AppEvent) -> Unit,
    taskViewModel: TaskViewModel,
    dataStoreViewModel: DataStoreViewModel,
) {
    // Necessary for the vibrate function
    val context = LocalContext.current

    // Generate a random message when the task list becomes empty
    val randomMessage = remember(taskState.tasks.isEmpty()) {
        if (taskState.tasks.isEmpty()) {
            emptyStateMessages.random()
        } else {
            ""
        }
    }

    val tutorialVisibility = dataStoreViewModel.tutorialVisibility.collectAsState()

    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (tutorialVisibility.value) {
                    FloatingActionButton(
                        onClick = {},
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(percent = 50))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(percent = 50)
                            )
                            .size(48.dp),
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Add task",
                            modifier = Modifier
                                .clickable {
                                    onAppEvent(AppEvent.ShowTutorialDialog)
                                },
                            tint = Priority1
                        )
                    }
                }
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
                                    vibrate(context = context, strength = 1)
                                    onAppEvent(AppEvent.ShowMenuDialog)
                                },
                                onClick = {
//                                vibrate(context = context, strength = 1)
                                    onEvent(TaskEvent.ShowAddTaskDialog)
                                },
                            )
                    )
                }
            }
        },
    ) { padding ->
        if (taskState.isAddTaskDialogVisible) {
            AddTaskDialog(taskState, onEvent, taskViewModel, dataStoreViewModel, onAppEvent)
        }
        if (appState.isMenuDialogVisible) {
            MenuDialog(taskState, onEvent, dataStoreViewModel = dataStoreViewModel, onAppEvent = onAppEvent)
        }
        if (appState.isHistoryDialogVisible) {
            HistoryDialog(taskViewModel, onEvent, onAppEvent = onAppEvent)
        }
        if (appState.isScheduleExactAlarmPermissionDialogVisible) {
            ScheduleExactAlarmPermissionDialog(
                onDismissOrDisallow = {
                    onAppEvent(AppEvent.HideScheduleExactAlarmPermissionDialog)
                    onAppEvent(AppEvent.IncrementPostNotificationDenialCount)
                },
                onAllow = {
                    onAppEvent(AppEvent.ShowScheduleExactAlarmPermissionIntent)
                    onAppEvent(AppEvent.HideScheduleExactAlarmPermissionDialog)
                },
            )
        }
        if (appState.isFontSettingsDialogVisible) {
            FontSettingsDialog(
                dataStoreViewModel = dataStoreViewModel,
                onDismiss = { onAppEvent(AppEvent.HideFontSettingsDialog) }
            )
        }
        if (appState.isTutorialDialogVisible) {
            Tutorial(
                onDismiss = {
                    onAppEvent(AppEvent.HideTutorialDialog)
                },
                onDisable = {
                    onAppEvent(AppEvent.HideTutorialDialog)
                    onAppEvent(AppEvent.DisableTutorialDialog)
                },
                onShowAddTaskDialog = {
                    onEvent(TaskEvent.ShowAddTaskDialog)
                },
                onShowMenuDialog = {
                    onAppEvent(AppEvent.ShowMenuDialog)
                }
            )
        }
        if (taskState.tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = randomMessage,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        } else {
            TaskList(onEvent, taskState, taskViewModel, padding)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(onEvent: (TaskEvent) -> Unit, taskState: TaskState, viewModel: TaskViewModel, padding: PaddingValues) {
//    val taskState by viewModel.state.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ,
    ) {
        items(taskState.tasks, key = { it.id }) { task ->
            var visible by remember { mutableStateOf(true) }

            // For calling the Delay before onEvent(Delete)
            val coroutineScope = rememberCoroutineScope()

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
                        + shrinkVertically(animationSpec = tween(300)),
            ) {
                TaskItem(
                    task = task,
                    onEdit = {
                        onEvent(TaskEvent.EditTask(it))
                    },
                    onDelete = {
                        coroutineScope.launch {
                            delay(300)
                            visible = false
                            onEvent(TaskEvent.DeleteTask(task))
                        }
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(task: Task, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit, viewModel: TaskViewModel) {

    // Colors for the Priority Icon
    val priorityColor = when (task.priority) {
        3 -> Priority3
        2 -> Priority2
        1 -> Priority1
        else -> Priority0
    }

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
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
                .clickable {
                    onEdit(task)
                }
        ) {
            Text(
                task.title,
                style = MaterialTheme.typography.titleLarge,
            )
            DueDate_Recurrence_Note(task = task, viewModel = viewModel)
        }
        CompleteIcon(onDelete = onDelete, task = task)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DueDate_Recurrence_Note(
    modifier: Modifier = Modifier,
    task: Task,
    viewModel: TaskViewModel
) {
    val dueDate = viewModel.formatDueDateWithDateTime(task.dueDate)
//    val nextDueDate = viewModel.formatDueDateWithDateTime(task.notificationTime)
    val note = task.note

    val textColor = if (task.dueDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now().plusDays(1))
        } == true) {
        DateRed
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Column {
        if (dueDate.isNotEmpty()) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(dueDate)
                    }
                    if(task.recurrenceType != RecurrenceType.NONE) {
                        withStyle(style = SpanStyle(color = textColor)) {
                            append(", " + task.recurrenceType.toDisplayString())
                        }
                    }
                    if (note.isNotBlank()) {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                            append(" | ")
                            append(note)
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        } else if(note.isNotEmpty()) {
            Text(
                text = note,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CompleteIcon(modifier: Modifier = Modifier, onDelete: (Task) -> Unit, task: Task) {
    var isChecked by remember { mutableStateOf(false) }
    val scale = remember { Animatable(initialValue = 1f) }
    var selected by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selected) {
        if(selected) {
            launch {
                scale.animateTo(
                    targetValue = 0.6f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .scale(scale.value)
            .clickable {
                selected = !selected
                isChecked = !isChecked
                onDelete(task)
            }
            .padding(8.dp)
    ) {
        AnimatedContent(
            targetState = isChecked,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }, label = "Complete Task Icon"
        ) { targetChecked ->
            if (targetChecked) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Checked",
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Unchecked",
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}
