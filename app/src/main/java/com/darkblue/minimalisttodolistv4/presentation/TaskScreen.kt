import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkblue.minimalisttodolistv4.data.SortType
import com.darkblue.minimalisttodolistv4.presentation.AddTaskDialog
import com.darkblue.minimalisttodolistv4.presentation.TaskEvent
import com.darkblue.minimalisttodolistv4.presentation.TaskState
import com.darkblue.minimalisttodolistv4.presentation.TaskViewModel

@Composable
fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    viewModel: TaskViewModel
) {
    var showColorPicker by remember { mutableStateOf(false) }

    DynamicTheme(primaryColor = viewModel.taskColor) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    onEvent(TaskEvent.ShowDialog)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task"
                    )
                }
            },
        ) { padding ->
            padding
            if (state.isAddingTask) {
                AddTaskDialog(state = state, onEvent = onEvent)
            }
            Column {
                Row {
//                    LazyColumn(
//                        contentPadding = PaddingValues(16.dp),
//                        modifier = Modifier.fillMaxSize(),
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        item {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .horizontalScroll(rememberScrollState()),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                SortType.values().forEach { sortType ->
//                                    Row(
//                                        modifier = Modifier
//                                            .clickable {
//                                                onEvent(TaskEvent.SortTasks(sortType))
//                                            },
//                                        verticalAlignment = CenterVertically
//                                    ) {
//                                        RadioButton(
//                                            selected = state.sortType == sortType,
//                                            onClick = {
//                                                onEvent(TaskEvent.SortTasks(sortType))
//                                            }
//                                        )
//                                        Text(text = sortType.name)
//                                    }
//                                }
//                            }
//                        }
//                        items(state.tasks) { task ->
//                            Row(
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Column(
//                                    modifier = Modifier.weight(1f)
//                                ) {
//                                    Text(
//                                        text = "${task.title} ${task.priority} ${task.note} ${task.dueDate} ${task.completed}",
//                                        fontSize = 20.sp
//                                    )
//                                    Text(text = task.completed.toString(), fontSize = 12.sp)
//                                }
//                                IconButton(onClick = {
//                                    onEvent(TaskEvent.DeleteTask(task))
//                                }) {
//                                    Icon(
//                                        imageVector = Icons.Default.Delete,
//                                        contentDescription = "Delete task"
//                                    )
//                                }
//                            }
//                        }
//                    }
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        showColorPicker = true
                    }) {
                        Text(text = "Theme")
                    }
                }
                if (showColorPicker) {
                    AlertDialog(
                        onDismissRequest = { showColorPicker = false },
                        confirmButton = {
                            ColorPickScreen(viewModel = viewModel)
                        }
                    )
                }
            }
        }
    }
}
