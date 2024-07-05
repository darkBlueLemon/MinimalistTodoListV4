package com.darkblue.minimalisttodolistv4.ui.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.model.ClockType
import com.darkblue.minimalisttodolistv4.data.model.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.model.SortType
import com.darkblue.minimalisttodolistv4.data.model.ThemeType
import com.darkblue.minimalisttodolistv4.ui.components.CustomBox
import com.darkblue.minimalisttodolistv4.ui.components.CustomDropdownMenu
import com.darkblue.minimalisttodolistv4.viewmodel.AppEvent
import com.darkblue.minimalisttodolistv4.viewmodel.DataStoreViewModel
import com.darkblue.minimalisttodolistv4.viewmodel.TaskEvent
import com.darkblue.minimalisttodolistv4.viewmodel.TaskState
import kotlinx.coroutines.handleCoroutineException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDialog(
    taskState: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier,
    dataStoreViewModel: DataStoreViewModel,
    onAppEvent: (AppEvent) -> Unit
) {
    LocalConfiguration.current.screenWidthDp
    BasicAlertDialog(
        onDismissRequest = {
            onAppEvent(AppEvent.HideMenuDialog)
        },
        modifier = modifier
            .width(350.dp)
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp)
                )
                Text(
                    text = "History",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAppEvent(AppEvent.ShowHistoryDialog)
                        }
                        .padding(bottom = 24.dp)
                )
//                Text(
//                    text = "Notification",
//                    modifier = Modifier
//                        .padding(bottom = 24.dp)
//                )
                RecurrenceSelector(
                    currentRecurrenceFilter = taskState.recurrenceFilter,
                    onRecurrenceFilterChange = { onEvent(TaskEvent.SetRecurrenceFilter(it)) }
                )
                PrioritySelector(
                    currentSortType = taskState.sortType,
                    onSortChange = { onEvent(TaskEvent.SortTasks(it) ) }
                )
                ThemeSelector(
                    dataStoreViewModel
                )
//                Text(
//                    text = "Tutorial",
//                    modifier = Modifier
//                        .padding(bottom = 24.dp)
//                )
                ClockTypeSelector(
                    dataStoreViewModel = dataStoreViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecurrenceSelector(
    currentRecurrenceFilter: RecurrenceType,
    onRecurrenceFilterChange: (RecurrenceType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "View Recurring Tasks",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        style = MaterialTheme.typography.bodyLarge
//        .padding(bottom = 24.dp)
    )
    CustomDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        RecurrenceType.entriesWithNONE.forEach { recurrenceType ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp, end = 25.dp)
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            onRecurrenceFilterChange(recurrenceType)
                        }
                    )
            ) {
                CompleteIconWithoutDelay(isChecked = currentRecurrenceFilter == recurrenceType)
                Text(
                    recurrenceType.toDisplayString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrioritySelector(
    currentSortType: SortType,
    onSortChange: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(text = "Sorting Option", modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true },
        style = MaterialTheme.typography.bodyLarge
//        .padding(bottom = 24.dp)
    )
    CustomDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        SortType.entries.forEach { sortType ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp, end = 25.dp)
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onSortChange(sortType) }
                    )
            ) {
                CompleteIconWithoutDelay(isChecked = currentSortType == sortType)
                Text(
                    sortType.toDisplayString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemeSelector(
    dataStoreViewModel: DataStoreViewModel
) {
    val theme by dataStoreViewModel.theme.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Text(
//        text = "Current Theme: ${theme.toDisplayString()}",
        text = "Theme",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        style = MaterialTheme.typography.bodyLarge
//            .padding(bottom = 24.dp)
    )
    CustomDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        ThemeType.entries.forEach { themeType ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp, end = 25.dp)
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            dataStoreViewModel.saveTheme(themeType)
                        }
                    )
            ) {
                CompleteIconWithoutDelay(isChecked = theme == themeType)
                Text(
                    themeType.toDisplayString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClockTypeSelector(
    dataStoreViewModel: DataStoreViewModel
) {
    val clock by dataStoreViewModel.clockType.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Text(
//        text = "Current Clock Type: ${clock.toDisplayString()}",
        text = "Clock Type",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        style = MaterialTheme.typography.bodyLarge
//            .padding(bottom = 24.dp)
    )
    CustomDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        ClockType.entries.forEach { clockType ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp, end = 25.dp)
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            dataStoreViewModel.saveClockType(clockType)
                        }
                    )
            ) {
                CompleteIconWithoutDelay(isChecked = clock == clockType)
                Text(
                    clockType.toDisplayString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun CompleteIconWithoutDelay(isChecked: Boolean) {
    Box(
        modifier = Modifier
            .padding(8.dp) // Increase padding for a larger touch area
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Checked",
                tint = MaterialTheme.colorScheme.primary,
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = "Unchecked",
                tint = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}
