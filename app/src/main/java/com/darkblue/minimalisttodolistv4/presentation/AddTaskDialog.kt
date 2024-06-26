package com.darkblue.minimalisttodolistv4.presentation

import android.app.AlarmManager
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.SpeakerNotes
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import com.darkblue.minimalisttodolistv4.ui.theme.Priority1
import com.darkblue.minimalisttodolistv4.ui.theme.Priority2
import com.darkblue.minimalisttodolistv4.ui.theme.Priority3
import com.darkblue.minimalisttodolistv4.ui.theme.translucentDark
import com.darkblue.minimalisttodolistv4.ui.theme.translucentDark
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel
) {
    BasicAlertDialog(
        onDismissRequest = { onEvent(TaskEvent.HideAddTaskDialog) },
        modifier = modifier
    ) {
        CustomBox {
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Title(state = state, onEvent = onEvent)

                PrioritySelector(priorityFromEdit = state.priority, onPriorityChange = onEvent)

                Note(state = state, onEvent = onEvent)

                DateSelector(state = state, onEvent = onEvent, viewModel = viewModel)

                if (state.dueDate != null)
                    TimeSelector(state = state, onEvent = onEvent, viewModel = viewModel)

                RecurrenceTypeSelector(
                    selectedRecurrenceType = state.recurrenceType,
                    onRecurrenceTypeSelected = { recurrenceType ->
                        onEvent(TaskEvent.SetRecurrenceType(recurrenceType))
                    }
                )
//                state.nextDueDate?.let {
//                    val nextDueDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
//                    Text(text = "Next Due Date: ${nextDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
//                }

                SaveButton(onSave = { onEvent(TaskEvent.SaveTask)}, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun Title(modifier: Modifier = Modifier, state: TaskState, onEvent: (TaskEvent) -> Unit) {
    TextField(
        value = state.title,
        onValueChange = { onEvent(TaskEvent.SetTitle(it)) },
        singleLine = true,
        placeholder = {
            Text(
                text = "I want to ...",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Light,
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
            fontWeight = FontWeight.Light,
            fontSize = MaterialTheme.typography.titleLarge.fontSize
        ),
    )
}

@Composable
fun PrioritySelector(priorityFromEdit: Int, onPriorityChange: (TaskEvent) -> Unit) {
    var selectedPriority by remember { mutableStateOf(priorityFromEdit) }
    var isPrioritySelected by remember { mutableStateOf(false) }

    if (isPrioritySelected || selectedPriority != 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
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
    } else {
        Row(
            modifier = Modifier
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
                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.bodySmall,
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
        painter = rememberVectorPainter(image = starIcon),
        contentDescription = "Priority $index",
        tint = starColor,
        modifier = Modifier
            .size(30.dp)
            .clickable { onClick(index) }
    )
}

@Composable
fun Note(modifier: Modifier = Modifier, state: TaskState, onEvent: (TaskEvent) -> Unit) {
    Row(
        modifier = Modifier.padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Note Icon",
            modifier = Modifier.size(20.dp)
        )
        TextField(
            value = state.note,
            onValueChange = { onEvent(TaskEvent.SetNote(it)) },
            placeholder = {
                Text(
                    text = "Add note",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.bodySmall,
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
                fontWeight = FontWeight.Light,
            ),
            singleLine = true,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelector(modifier: Modifier = Modifier, state: TaskState, onEvent: (TaskEvent) -> Unit, viewModel: TaskViewModel) {
    val text = viewModel.formatDueDateWithDateOnly(state.dueDate).ifEmpty { "Add Date" }

    Row(
        modifier = Modifier.padding(start = 15.dp),
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
                .padding(15.dp)
                .clickable { onEvent(TaskEvent.ShowDatePicker) },
            color = if (text == "Add Date") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodySmall,
        )
    }

    if (state.isDatePickerVisible) {
        DatePicker(
            onDateSelected = { date -> onEvent(TaskEvent.SetDueDate(date)) },
            closeSelection = { onEvent(TaskEvent.HideDatePicker) }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSelector(modifier: Modifier = Modifier, state: TaskState, onEvent: (TaskEvent) -> Unit, viewModel: TaskViewModel) {
    val text = viewModel.formatDueDateWithTimeOnly(state.dueDate).ifEmpty { "Add Time" }

    Row(
        modifier = Modifier.padding(start = 15.dp),
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
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodySmall,
        )
    }

    if (state.isTimePickerVisible) {
        TimePickerFromOldApp(
            onTimeSelected = { time -> onEvent(TaskEvent.SetDueTime(time)) },
            closeSelection = { onEvent(TaskEvent.HideTimePicker) }
        )
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
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp)
        )
    }
}


@Composable
fun RecurrenceTypeSelector(
    selectedRecurrenceType: RecurrenceType,
    onRecurrenceTypeSelected: (RecurrenceType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RecurrenceType.entries.forEach { recurrenceType ->
            Text(
                text = recurrenceType.name,
                modifier = Modifier
                    .clickable { onRecurrenceTypeSelected(recurrenceType) }
                    .background(
                        color = if (recurrenceType == selectedRecurrenceType) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (recurrenceType == selectedRecurrenceType) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

