package com.darkblue.minimalisttodolistv4.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = state.title,
                onValueChange = {
                    onEvent(TaskEvent.SetTitle(it))
                },
                placeholder = {
                    Text(text = "Title")
                }
            )
            TextField(
                value = state.priority.toString(),
                onValueChange = {
                    onEvent(TaskEvent.SetPriority(it.toInt()))
                },
                placeholder = {
                    Text(text = "Priority")
                }
            )
            TextField(
                value = state.note,
                onValueChange = {
                    onEvent(TaskEvent.SetNote(it))
                },
                placeholder = {
                    Text(text = "Note")
                }
            )
            TextField(
                value = state.dueDate.toString(),
                onValueChange = {
                    onEvent(TaskEvent.SetDueDate(it.toLong()))
                },
                placeholder = {
                    Text(text = "Due Date")
                }
            )
            TextField(
                value = state.completed.toString(),
                onValueChange = {
                    onEvent(TaskEvent.SetCompleted(it.toBoolean()))
                },
                placeholder = {
                    Text(text = "Due Date")
                }
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(onClick = {
                onEvent(TaskEvent.SaveTask)
            }) {
                Text(text = "Save")
            }
        }
    }
}