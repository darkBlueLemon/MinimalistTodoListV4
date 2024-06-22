package com.darkblue.minimalisttodolistv4.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.DeletedTask
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(viewModel: TaskViewModel) {
    val deletedTasks by viewModel.deletedTasks.collectAsState()

    Column {
        Text("Deleted Tasks History", style = MaterialTheme.typography.headlineSmall)

        LazyColumn {
            items(deletedTasks) { deletedTask ->
                DeletedTaskItem(deletedTask, viewModel::onEvent)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeletedTaskItem(deletedTask: DeletedTask, onEvent: (TaskEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = deletedTask.title, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Deleted at: ${Instant.ofEpochMilli(deletedTask.deletedAt).atZone(ZoneId.systemDefault()).toLocalDateTime().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}", style = MaterialTheme.typography.bodySmall)

        Row {
            Button(onClick = { onEvent(TaskEvent.RemoveDeletedTask(deletedTask)) }) {
                Text("Remove")
            }
            Button(onClick = { onEvent(TaskEvent.UndoDeleteTask(deletedTask)) }) {
                Text("Undo")
            }
        }
    }
}
