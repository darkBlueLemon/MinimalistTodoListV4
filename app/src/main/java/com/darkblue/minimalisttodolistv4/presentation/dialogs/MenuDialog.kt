package com.darkblue.minimalisttodolistv4.presentation.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.data.model.RecurrenceType
import com.darkblue.minimalisttodolistv4.data.model.SortType
import com.darkblue.minimalisttodolistv4.data.model.ThemeType
import com.darkblue.minimalisttodolistv4.presentation.components.CustomBox
import com.darkblue.minimalisttodolistv4.presentation.components.CustomDropdownMenu
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.PreferencesViewModel
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskEvent
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDialog(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier,
    preferencesViewModel: PreferencesViewModel
) {
    LocalConfiguration.current.screenWidthDp
    BasicAlertDialog(
        onDismissRequest = {
            onEvent(TaskEvent.HideMenuDialog)
        },
        modifier = modifier
            .width(350.dp)

        // to stop it from expanding on box drop down menu click
//            .height(350.dp)
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(text = "History", modifier = Modifier.clickable { onEvent(TaskEvent.ShowHistoryDialog) })
                Text(text = "Notification")
                RecurrenceSelector(
                    currentRecurrenceFilter = state.recurrenceFilter,
                    onRecurrenceFilterChange = { onEvent(TaskEvent.SetRecurrenceFilter(it)) }
                )
                PrioritySelector(
                    currentSortType = state.sortType,
                    onSortChange = { onEvent(TaskEvent.SortTasks(it) ) }
                )
                ThemeSelector(preferencesViewModel)
                Text(text = "Tutorial")
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

    Text(text = "Recurrence Filter", modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true }
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
                Text(recurrenceType.toDisplayString())
            }
        }
    }
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
        .clickable { expanded = true }
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
                Text(sortType.toDisplayString())
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemeSelector(
    preferencesViewModel: PreferencesViewModel
) {
    val theme by preferencesViewModel.theme.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "Current Theme: ${theme.toDisplayString()}",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
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
                            preferencesViewModel.saveTheme(themeType)
                        }
                    )
            ) {
                CompleteIconWithoutDelay(isChecked = theme == themeType)
                Text(themeType.toDisplayString())
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

