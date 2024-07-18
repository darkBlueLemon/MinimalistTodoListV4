package com.minimalisttodolist.pleasebethelastrecyclerview.ui.dialogs

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.minimalisttodolist.pleasebethelastrecyclerview.R
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.ClockType
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.DueDateFilterType
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.RecurrenceType
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.SortType
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CompleteIconWithoutDelay
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomBox
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomDropdownMenu
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.FilterSelector
import com.minimalisttodolist.pleasebethelastrecyclerview.util.darkIcon
import com.minimalisttodolist.pleasebethelastrecyclerview.util.lightIcon
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.AppEvent
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.DataStoreViewModel
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.TaskEvent
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.TaskState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDialog(
    taskState: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier,
    dataStoreViewModel: DataStoreViewModel,
    onAppEvent: (AppEvent) -> Unit
) {
    val context = LocalContext.current
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
                MenuTitle( modifier = Modifier.align(Alignment.CenterHorizontally) )
                Personalize ( onClick = {
                    onAppEvent(AppEvent.ShowPersonalizeDialog)
                })
                RecurrenceSelector(
                    onRecurrenceFilterChange = { onEvent(TaskEvent.SetRecurrenceFilter(it)) },
                    dataStoreViewModel = dataStoreViewModel
                )
                PrioritySelector(
                    onSortChange = { onEvent(TaskEvent.SortTasks(it) ) },
                    dataStoreViewModel = dataStoreViewModel
                )
                DueDateFilterSelector(
                    onDueDateFilterChange = { onEvent(TaskEvent.SetDueDateFilter(it)) },
                    dataStoreViewModel = dataStoreViewModel
                )
                History( onClick = { onAppEvent(AppEvent.ShowHistoryDialog) } )
            }
        }
    }
}

@Composable
fun MenuTitle(modifier: Modifier = Modifier) {
    Text(
        text = "Menu",
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier
            .padding(bottom = 12.dp)
    )
}

@Composable
fun Personalize(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(top = 16.dp, bottom = 16.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Personalize",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 10.dp)
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = "History Chevron",
            tint = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun RecurrenceSelector(
    onRecurrenceFilterChange: (RecurrenceType) -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    FilterSelector(
        title = "Recurrence Filter",
        onFilterChange = onRecurrenceFilterChange,
        dataStoreViewModel = dataStoreViewModel,
        filterOptions = RecurrenceType.entriesWithNONE.toList(),
        getDisplayString = { it.toDisplayString() },
        saveFilter = { vm, filter -> vm.saveRecurrenceFilter(filter) },
        collectFilter = { it.recurrenceFilter },
        initialValue = RecurrenceType.NONE
    )
}

@Composable
fun PrioritySelector(
    onSortChange: (SortType) -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    FilterSelector(
        title = "Sort By",
        onFilterChange = onSortChange,
        dataStoreViewModel = dataStoreViewModel,
        filterOptions = SortType.entries.toList(),
        getDisplayString = { it.toDisplayString() },
        saveFilter = { vm, filter -> vm.savePriorityOption(filter) },
        collectFilter = { it.priorityOption },
        initialValue = SortType.PRIORITY
    )
}

@Composable
fun DueDateFilterSelector(
    onDueDateFilterChange: (DueDateFilterType) -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    FilterSelector(
        title = "Due Date Filter",
        onFilterChange = onDueDateFilterChange,
        dataStoreViewModel = dataStoreViewModel,
        filterOptions = DueDateFilterType.entries.toList(),
        getDisplayString = { it.toDisplayString() },
        saveFilter = { vm, filter -> vm.saveDueDateFilter(filter) },
        collectFilter = { it.dueDateFilter },
        initialValue = DueDateFilterType.NONE
    )
}

@Composable
fun History(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(top = 16.dp, bottom = 16.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "History",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 10.dp)
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = "History Chevron",
            tint = MaterialTheme.colorScheme.tertiary,
        )
    }
}
