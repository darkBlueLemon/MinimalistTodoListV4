package com.darkblue.minimalisttodolistv4.presentation

import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(TaskEvent.HideDialog)
        }
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.title,
                    onValueChange = {
                        onEvent(TaskEvent.SetTitle(it))
                    },
                    label = {
                        Text(text = "Title")
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
                )
                TextField(
                    value = state.priority.toString(),
                    onValueChange = {
                        onEvent(TaskEvent.SetPriority(it.toInt()))
                    },
                    label = {
                        Text(text = "Priority")
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
//                placeholder = {
//                    Text(text = "Priority")
//                }
                )
                TextField(
                    value = state.note,
                    onValueChange = {
                        onEvent(TaskEvent.SetNote(it))
                    },
                    label = {
                        Text(text = "Note")
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
                )

                // Date
                TextButton(
                    onClick = {
                        onEvent(TaskEvent.ShowDatePicker)
                    }
                ) {
                    Text(text = "Due Date")
                }
                TextField(
                    value = state.dueDate.toString(),
                    onValueChange = {
                        onEvent(TaskEvent.SetDueDate(it.toLong()))
                    },
                    label = {
                        Text(text = "Due Date")
                    }
                )
                state.dueDate?.let {
                    val dueDate =
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    Text(text = "Due Date: ${dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
                }
                if (state.isDatePickerVisible) {
                    Log.d("TAG", "clicked")
                    DatePicker(onDateSelected = { date ->
                        val epochMilli =
                            date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        onEvent(TaskEvent.SetDueDate(epochMilli))
                        onEvent(TaskEvent.HideDatePicker)
                    }, closeSelection = {
                        onEvent(TaskEvent.HideDatePicker)
                    })
                }

//                // Time
                TextButton(
                    onClick = {
//                        onEvent(TaskEvent.ShowTimePicker)
                    }
                ) {
                    Text(text = "Time Due")
                }

                // Recurrence
                RecurrenceTypeSelector(
                    selectedRecurrenceType = state.recurrenceType,
                    onRecurrenceTypeSelected = { recurrenceType ->
                        onEvent(TaskEvent.SetRecurrenceType(recurrenceType))
                    }
                )
                state.nextDueDate?.let {
                    val nextDueDate =
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    Text(text = "Next Due Date: ${nextDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
                }
                Button(onClick = {
                    onEvent(TaskEvent.SaveTask)
                }) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Composable
fun RecurrenceTypeSelector(
    selectedRecurrenceType: RecurrenceType,
    onRecurrenceTypeSelected: (RecurrenceType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RecurrenceType.entries.forEach { recurrenceType ->
            Text(
                text = recurrenceType.name,
                modifier = Modifier
                    .clickable { onRecurrenceTypeSelected(recurrenceType) }
                    .padding(8.dp)
                    .background(
                        if (recurrenceType == selectedRecurrenceType) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    ),
                color = if (recurrenceType == selectedRecurrenceType) Color.White else Color.Black
            )
        }
    }
}