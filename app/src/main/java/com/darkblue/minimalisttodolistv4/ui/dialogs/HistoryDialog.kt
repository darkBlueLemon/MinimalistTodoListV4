package com.darkblue.minimalisttodolistv4.ui.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.model.DeletedTask
import com.darkblue.minimalisttodolistv4.ui.components.CustomBox
import com.darkblue.minimalisttodolistv4.ui.components.CustomDropdownMenu
import com.darkblue.minimalisttodolistv4.viewmodel.AppEvent
import com.darkblue.minimalisttodolistv4.viewmodel.TaskEvent
import com.darkblue.minimalisttodolistv4.viewmodel.TaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryDialog(
    viewModel: TaskViewModel,
    onEvent: (TaskEvent) -> Unit,
    onAppEvent: (AppEvent) -> Unit
) {
    val deletedTasks by viewModel.deletedTasks.collectAsState()

    BasicAlertDialog(onDismissRequest = {
        onAppEvent(AppEvent.HideHistoryDialog)
    }) {
        CustomBox {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .height(400.dp)
                    .width(350.dp)
            ) {
                TitleAndDeleteAll(
                    onClearHistory = { onEvent(TaskEvent.DeleteAllHistoryTasks) }
                )
                if (deletedTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Your task history is empty",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                } else {
                    HistoryList(
                        viewModel,
                        onEvent,
                        deletedTasks
                    )
                }
            }
        }
    }
}

@Composable
fun TitleAndDeleteAll(onClearHistory: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(8.dp)
    ){
        Text(
            text = "History",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
        Icon(
            Icons.Default.DeleteForever,
            contentDescription = "Clear History",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    onClearHistory()
                    expanded = false
                }
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryList(
    viewModel: TaskViewModel,
    onEvent: (TaskEvent) -> Unit,
    deletedTasks: List<DeletedTask>
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 8.dp)
            .animateContentSize()
    ) {
        items(deletedTasks, key = { it.id }) { deletedTask ->
            var visible by remember { mutableStateOf(true) }

            val coroutineScope = rememberCoroutineScope()

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
                        + shrinkVertically(animationSpec = tween(300))
            ) {
                HistoryItem(
                    deletedTask = deletedTask,
                    onEvent = onEvent,
                    viewModel = viewModel,
                    onItemDelete = {
                        coroutineScope.launch {
                            delay(300)
                            visible = false
                            onEvent(TaskEvent.DeleteForever(deletedTask))
                        }
                    },
                    onItemRecover = {
                        coroutineScope.launch {
                            delay(300)
                            visible = false
                            onEvent(TaskEvent.UndoDeleteTask(deletedTask))
                        }
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryItem(
    deletedTask: DeletedTask,
    onEvent: (TaskEvent) -> Unit,
    viewModel: TaskViewModel,
    onItemDelete: () -> Unit,
    onItemRecover: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = deletedTask.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = viewModel.formatDueDateWithDateTime(deletedTask.deletedAt),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Box {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        expanded = true
                    }
            )
            CustomDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                DropDownItem(
                    onRecoverClick = {
                        onItemRecover()
                        expanded = false
                    },
                    onDeleteClick = {
                        onItemDelete()
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun DropDownItem(modifier: Modifier = Modifier, onRecoverClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.Undo,
                contentDescription = "Recover",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onRecoverClick()
                }
            )
            Text(
                "Recover",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.width(30.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onDeleteClick()
                }
            )
            Text(
                "Delete",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}