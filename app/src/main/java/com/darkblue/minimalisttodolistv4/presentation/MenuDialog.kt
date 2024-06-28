package com.darkblue.minimalisttodolistv4.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDialog(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LocalConfiguration.current.screenWidthDp
    BasicAlertDialog(
        onDismissRequest = {
            onEvent(TaskEvent.HideMenuDialog)
        },
        modifier = modifier.width(250.dp)
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                HistoryOption(
                    onHistoryDialogToggle = {
                    onEvent(TaskEvent.ShowHistoryDialog)
                })
                SortAndFilterControls(
                    currentSortType = state.sortType,
                    onSortChange = { onEvent(TaskEvent.SortTasks(it)) },
                    currentRecurrenceFilter = state.recurrenceFilter,
                    onRecurrenceFilterChange = { onEvent(TaskEvent.SetRecurrenceFilter(it)) }
                )
            }
        }
    }
}

@Composable
fun HistoryOption(onHistoryDialogToggle: () -> Unit) {
    Text(text = "History", modifier = Modifier.clickable { onHistoryDialogToggle() })
}

@Composable
fun SortAndFilterControls(
    currentSortType: SortType,
    onSortChange: (SortType) -> Unit,
    currentRecurrenceFilter: RecurrenceType,
    onRecurrenceFilterChange: (RecurrenceType) -> Unit
) {
    Column {
        ExpandableSection(
            title = "Priority Order",
            content = {
                SortOptions(
                    currentSortType = currentSortType,
                    onSortChange = onSortChange
                )
            }
        )
        ExpandableSection(
            title = "Recurrence Filter",
            content = {
                RecurrenceOptions(
                    currentRecurrenceFilter = currentRecurrenceFilter,
                    onRecurrenceFilterChange = onRecurrenceFilterChange
                )
            }
        )
    }
}

@Composable
fun ExpandableSection(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        TextButton(onClick = { expanded = !expanded }) {
            Text(title)
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }
        AnimatedVisibility(visible = expanded) {
            content()
        }
    }
}

@Composable
fun SortOptions(
    currentSortType: SortType,
    onSortChange: (SortType) -> Unit
) {
    Column {
        SortType.entries.forEach { sortType ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = currentSortType == sortType,
                    onClick = { onSortChange(sortType) }
                )
                Text(sortType.name)
            }
        }
    }
}

@Composable
fun RecurrenceOptions(
    currentRecurrenceFilter: RecurrenceType,
    onRecurrenceFilterChange: (RecurrenceType) -> Unit
) {
    Column {
        RecurrenceType.entries.forEach { recurrenceType ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = currentRecurrenceFilter == recurrenceType,
                    onClick = { onRecurrenceFilterChange(recurrenceType) }
                )
                Text(recurrenceType.name)
            }
        }
    }
}
