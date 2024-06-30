package com.darkblue.minimalisttodolistv4.presentation.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.model.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.model.Task
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.PreferencesViewModel
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskEvent
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskState
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskViewModel
import com.darkblue.minimalisttodolistv4.presentation.dialogs.AddTaskDialog
import com.darkblue.minimalisttodolistv4.presentation.dialogs.HistoryDialog
import com.darkblue.minimalisttodolistv4.presentation.dialogs.MenuDialog
import com.darkblue.minimalisttodolistv4.ui.theme.Priority0
import com.darkblue.minimalisttodolistv4.ui.theme.Priority1
import com.darkblue.minimalisttodolistv4.ui.theme.Priority2
import com.darkblue.minimalisttodolistv4.ui.theme.Priority3
import com.darkblue.minimalisttodolistv4.ui.theme.dateRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    taskViewModel: TaskViewModel,
    preferencesViewModel: PreferencesViewModel
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
                                onEvent(TaskEvent.ShowMenuDialog)
                            },
                            onClick = {
                                // Vibrate on click?
                            vibrate(context = context, strength = 1)
                                onEvent(TaskEvent.ShowAddTaskDialog)
                            },
                        )
                )
            }
        },
    ) { padding ->
        if(state.isAddTaskDialogVisible) {
            AddTaskDialog(state, onEvent, taskViewModel, preferencesViewModel)
        }
        if(state.isMenuDialogVisible) {
            MenuDialog(state, onEvent, preferencesViewModel = preferencesViewModel)
        }
        if(state.isHistoryDialogVisible) {
            HistoryDialog(taskViewModel, onEvent)
        }
        TaskList(onEvent, state, taskViewModel, padding)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(onEvent: (TaskEvent) -> Unit, state: TaskState, viewModel: TaskViewModel, padding: PaddingValues) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ,
    ) {
        items(state.tasks, key = { it.id }) { task ->
            var visible by remember { mutableStateOf(true) }
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

@OptIn(ExperimentalFoundationApi::class)
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
    val nextDueDate = viewModel.formatDueDateWithDateTime(task.nextDueDate)
    val note = task.note

//    val textColor = if (task.dueDate?.let { Instant.ofEpochMilli(it).isBefore(Instant.now()) } == true) dateRed else MaterialTheme.colorScheme.tertiary
    val textColor = if (task.dueDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now().plusDays(1))
        } == true) {
        dateRed
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
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun CompleteIcon(modifier: Modifier = Modifier, onDelete: (Task) -> Unit, task: Task) {
    var isChecked by remember { mutableStateOf(false) }

    val scaleB = remember { Animatable(initialValue = 1f) }
    var selected by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selected) {
        if(selected) {
            launch {
                scaleB.animateTo(
                    targetValue = 0.6f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scaleB.animateTo(
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
            .scale(scaleB.value)
            .clickable {
                selected = !selected
                isChecked = !isChecked
                onDelete(task)
            }
            .padding(8.dp) // Increase padding for a larger touch area
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
//                    modifier = Modifier.size(36.dp) // Increase icon size if needed
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Unchecked",
                    tint = MaterialTheme.colorScheme.tertiary,
//                    modifier = Modifier.size(36.dp) // Increase icon size if needed
                )
            }
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

