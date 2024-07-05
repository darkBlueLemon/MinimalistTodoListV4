package com.darkblue.minimalisttodolistv4.ui.dialogs

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkblue.minimalisttodolistv4.data.model.RecurrenceType
import com.darkblue.minimalisttodolistv4.ui.components.CustomBox
import com.darkblue.minimalisttodolistv4.ui.components.DatePicker
import com.darkblue.minimalisttodolistv4.viewmodel.TaskEvent
import com.darkblue.minimalisttodolistv4.viewmodel.TaskState
import com.darkblue.minimalisttodolistv4.viewmodel.TaskViewModel
import com.darkblue.minimalisttodolistv4.ui.components.TimePickerFromOldApp
import com.darkblue.minimalisttodolistv4.viewmodel.AppEvent
import com.darkblue.minimalisttodolistv4.viewmodel.DataStoreViewModel
import com.darkblue.minimalisttodolistv4.ui.theme.Priority1
import com.darkblue.minimalisttodolistv4.ui.theme.Priority2
import com.darkblue.minimalisttodolistv4.ui.theme.Priority3

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    taskState: TaskState,
    onEvent: (TaskEvent) -> Unit,
    viewModel: TaskViewModel,
    dataStoreViewModel: DataStoreViewModel,
    onAppEvent: (AppEvent) -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = { onEvent(TaskEvent.HideAddTaskDialog) },
    ) {
        CustomBox {
            Column(
                // Best thing everrrrr -> IntrinsicSize
                modifier = Modifier.padding(15.dp).width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Title(taskState = taskState, onEvent = onEvent)

                PrioritySelector(priorityFromEdit = taskState.priority, onPriorityChange = onEvent)

                Note(taskState = taskState, onEvent = onEvent)

                DateSelector(taskState = taskState, onEvent = onEvent, viewModel = viewModel, onAppEvent = onAppEvent)

                if (taskState.dueDate != null) {
                    TimeSelector(taskState = taskState, onEvent = onEvent, viewModel = viewModel, dataStoreViewModel = dataStoreViewModel)
                    RecurrenceSelector(
                        recurrenceFromEdit = taskState.recurrenceType,
                        onRecurrenceTypeSelected = { recurrenceType ->
                            onEvent(TaskEvent.SetRecurrenceType(recurrenceType))
                        }
                    )
                }

//                state.nextDueDate?.let {
//                    val nextDueDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
//                    Text(text = "Next Due Date: ${nextDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
//                }

                SaveButton(
                    onSave = {
                        onEvent(TaskEvent.SaveTask)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun Title(modifier: Modifier = Modifier, taskState: TaskState, onEvent: (TaskEvent) -> Unit) {
    TextField(
        value = taskState.title,
        onValueChange = { onEvent(TaskEvent.SetTitle(it)) },
        singleLine = true,
        placeholder = {
            Text(
                text = "I want to ...",
                color = MaterialTheme.colorScheme.tertiary,
//                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary
        ),
        textStyle = LocalTextStyle.current.copy(
//            fontWeight = FontWeight.Light,
            fontSize = MaterialTheme.typography.titleLarge.fontSize
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PrioritySelector(
    priorityFromEdit: Int,
    onPriorityChange: (TaskEvent) -> Unit
) {
    var selectedPriority by remember { mutableStateOf(priorityFromEdit) }
    var isPrioritySelected by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isPrioritySelected || selectedPriority != 0,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            PriorityStar(index = 1, selectedPriority, Priority1) { priority ->
                selectedPriority = priority
                onPriorityChange(TaskEvent.SetPriority(priority))
            }
            PriorityStar(index = 2, selectedPriority, Priority2) { priority ->
                selectedPriority = priority
                onPriorityChange(TaskEvent.SetPriority(priority))
            }
            PriorityStar(index = 3, selectedPriority, Priority3) { priority ->
                selectedPriority = priority
                onPriorityChange(TaskEvent.SetPriority(priority))
            }
        }
    }

    if (!isPrioritySelected && selectedPriority == 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .clickable {
                    isPrioritySelected = true
                    selectedPriority = 1
                    onPriorityChange(TaskEvent.SetPriority(1))
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = "Priority Toggle Icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Add priority",
                color = MaterialTheme.colorScheme.tertiary,
//                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}

@Composable
fun PriorityStar(index: Int, selectedPriority: Int, color: Color, onClick: (Int) -> Unit) {
    val starIcon = if (index == selectedPriority) Icons.Rounded.Star else Icons.Rounded.StarOutline
    val starColor = if (index == selectedPriority) color else Color.Gray

    Icon(
        imageVector = starIcon,
        contentDescription = "Priority $index",
        tint = starColor,
        modifier = Modifier
            .size(30.dp)
            .clickable { onClick(index) }
    )
}

@Composable
fun Note(modifier: Modifier = Modifier, taskState: TaskState, onEvent: (TaskEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Note Icon",
            modifier = Modifier.size(20.dp)
        )
        TextField(
            value = taskState.note,
            onValueChange = { onEvent(TaskEvent.SetNote(it)) },
            placeholder = {
                Text(
                    text = "Add note",
                    color = MaterialTheme.colorScheme.tertiary,
//                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = LocalTextStyle.current.copy(
//                fontWeight = FontWeight.Light,
            ),
            singleLine = true,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelector(modifier: Modifier = Modifier, taskState: TaskState, onEvent: (TaskEvent) -> Unit, viewModel: TaskViewModel, onAppEvent: (AppEvent) -> Unit) {
    val text = viewModel.formatDueDateWithDateOnly(taskState.dueDate).ifEmpty { "Add date" }

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 4.dp, bottom = 4.dp)
            .clickable { onEvent(TaskEvent.ShowDatePicker) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.DateRange,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Date Icon",
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(15.dp),
            color = if (text == "Add date") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
//            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    if (taskState.isDatePickerVisible) {
        DatePicker(
            onDateSelected = { date ->
                onEvent(TaskEvent.SetDueDate(date))
            },
            closeSelection = {
                onEvent(TaskEvent.HideDatePicker)
                if(viewModel.state.value.dueDate != null) {
                    onAppEvent(AppEvent.CheckNotificationPermissions)
                }
            },
            initialDate = viewModel.getLocalDateFromEpochMilli(taskState.dueDate)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSelector(modifier: Modifier = Modifier, taskState: TaskState, onEvent: (TaskEvent) -> Unit, viewModel: TaskViewModel, dataStoreViewModel: DataStoreViewModel) {
    val text = viewModel.formatDueDateWithTimeOnly(taskState.dueDate).ifEmpty { "Add Time" }

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.AccessTime,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Time Icon",
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(15.dp)
                .clickable { onEvent(TaskEvent.ShowTimePicker) },
            color = if (text == "Add Time") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
//            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    if (taskState.isTimePickerVisible) {
        TimePickerFromOldApp(
            onTimeSelected = { time -> onEvent(TaskEvent.SetDueTime(time)) },
            closeSelection = { onEvent(TaskEvent.HideTimePicker) },
            initialTime = viewModel.getLocalTimeFromEpochMilli(taskState.dueDate),
            dataStoreViewModel = dataStoreViewModel
        )
    }
}

@Composable
fun RecurrenceSelector(
    modifier: Modifier = Modifier,
    recurrenceFromEdit: RecurrenceType,
    onRecurrenceTypeSelected: (RecurrenceType) -> Unit
) {
    var selectedRecurrenceType by remember { mutableStateOf(recurrenceFromEdit) }
    var isRecurrenceSelected by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isRecurrenceSelected || selectedRecurrenceType != RecurrenceType.NONE,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 11.dp, bottom = 11.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RecurrenceType.entriesWithoutNONE.forEach { recurrenceType ->
                Text(
                    text = recurrenceType.toDisplayString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .clickable {
                            if (selectedRecurrenceType == recurrenceType) {
                                selectedRecurrenceType = RecurrenceType.NONE
                            } else {
                                selectedRecurrenceType = recurrenceType
                            }
                            onRecurrenceTypeSelected(selectedRecurrenceType)
                        }
                        .background(
                            color = if (recurrenceType == selectedRecurrenceType) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (recurrenceType == selectedRecurrenceType) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    if (!isRecurrenceSelected && selectedRecurrenceType == RecurrenceType.NONE) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, top = 4.dp, bottom = 4.dp)
                .clickable {
                    isRecurrenceSelected = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Repeat,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = "Recurrence Toggle Icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Add recurrence",
                color = MaterialTheme.colorScheme.tertiary,
//                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}

@Composable
fun SaveButton(
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onSave() }
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(percent = 50)
            ),
    ) {
        Text(
            text = "SAVE",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp)
        )
    }
}
