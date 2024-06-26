package com.darkblue.minimalisttodolistv4.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDialog(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(TaskEvent.HideMenuDialog)
        }
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
fun SortAndFilterControls(
    currentSortType: SortType,
    onSortChange: (SortType) -> Unit,
    currentRecurrenceFilter: RecurrenceType,
    onRecurrenceFilterChange: (RecurrenceType) -> Unit
) {
    Column {
        Text("Sort by:")
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ){
            SortType.entries.forEach { sortType ->
                RadioButton(
                    selected = currentSortType == sortType,
                    onClick = { onSortChange(sortType) }
                )
                Text(sortType.name)
            }
        }
        Text("Filter by Recurrence:")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecurrenceType.entries.forEach { recurrenceType ->
                RadioButton(
                    selected = currentRecurrenceFilter == recurrenceType,
                    onClick = { onRecurrenceFilterChange(recurrenceType) }
                )
                Text(recurrenceType.name)
            }
        }
    }
}

