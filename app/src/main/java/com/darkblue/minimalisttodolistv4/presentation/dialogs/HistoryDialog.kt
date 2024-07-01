package com.darkblue.minimalisttodolistv4.presentation.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Menu
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.model.DeletedTask
import com.darkblue.minimalisttodolistv4.presentation.components.CustomBox
import com.darkblue.minimalisttodolistv4.presentation.components.CustomDropdownMenu
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.AppEvent
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskEvent
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskViewModel

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
                    onClearHistory = { onEvent(TaskEvent.DeleteAllHistoryTasks)}
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .animateContentSize()
                ) {
                    items(deletedTasks) { deletedTask ->
                        HistoryItem(deletedTask, viewModel::onEvent, viewModel)
                    }
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
            .padding(15.dp)
    ){
        Text(
            text = "History",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
        Box (
            modifier =  Modifier
                .fillMaxHeight()
        ){
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu Toggle",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { expanded = true }
            )
            CustomDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                ) {
                    Icon(
                        Icons.Rounded.DeleteForever,
                        contentDescription = "Clear History",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            onClearHistory()
                            expanded = false
                        }
                    )
                    Text(
                        "Clear History",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryItem(
    deletedTask: DeletedTask,
    onEvent: (TaskEvent) -> Unit,
    viewModel: TaskViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = deletedTask.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = viewModel.formatDueDateWithDateTime(deletedTask.deletedAt)
            )
        }
        Box {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                modifier = Modifier
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
                        onEvent(TaskEvent.UndoDeleteTask(deletedTask))
                        expanded = false
                    },
                    onDeleteClick = {
                        onEvent(TaskEvent.DeleteForever(deletedTask))
                        expanded = false
                    })
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