package com.minimalisttodolist.pleasebethelastrecyclerview.ui.dialogs

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.minimalisttodolist.pleasebethelastrecyclerview.R
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.ClockType
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.RecurrenceType
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.SortType
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.ThemeType
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomBox
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomDropdownMenu
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
    LocalConfiguration.current.screenWidthDp
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
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp)
                )
                Text(
                    text = "History",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAppEvent(AppEvent.ShowHistoryDialog)
                        }
                        .padding(top = 12.dp, bottom = 12.dp)
                )
                Text(
                    text = "Font Options",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAppEvent(AppEvent.ShowFontSettingsDialog)
                        }
                        .padding(top = 12.dp, bottom = 12.dp)
                )
                AppIconSelector(context = context)
//                Text(
//                    text = "Notification",
//                    modifier = Modifier
//                        .padding(bottom = 24.dp)
//                )
                RecurrenceSelector(
                    currentRecurrenceFilter = taskState.recurrenceFilter,
                    onRecurrenceFilterChange = { onEvent(TaskEvent.SetRecurrenceFilter(it)) },
                    dataStoreViewModel = dataStoreViewModel
                )
                PrioritySelector(
                    currentSortType = taskState.sortType,
                    onSortChange = { onEvent(TaskEvent.SortTasks(it) ) },
                    dataStoreViewModel = dataStoreViewModel
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

@Composable
fun AppIconSelector(modifier: Modifier = Modifier, context: Context) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "App Icon",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(top = 12.dp, bottom = 12.dp)
    )

    CustomDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconPreview(isLightIcon = true, onClick = {
                lightIcon(context)
            })
            IconPreview(isLightIcon = false, onClick = {
                darkIcon(context)
            })
        }
    }

//    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconPreview(isLightIcon: Boolean, onClick: () -> Unit) {
    val iconResId = if (isLightIcon) {
        R.drawable.logo_light
    } else {
        R.drawable.logo_dark
    }
    val backgroundColor = if (isLightIcon) {
        Color.White
    } else {
        Color.Black
    }
    val borderColor = if (isLightIcon) {
        Color.Black
    } else {
        Color.White
    }
    val scale = remember { Animatable(initialValue = 1f) }
    var selected by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selected) {
        if(selected) {
            launch {
                scale.animateTo(
                    targetValue = 0.9f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                selected = false
            }
        }
    }

    Box(
        modifier = Modifier
            .scale(scale.value)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(16.dp))
            .combinedClickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    selected = !selected
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecurrenceSelector(
    currentRecurrenceFilter: RecurrenceType,
    onRecurrenceFilterChange: (RecurrenceType) -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    val recurrenceFilter by dataStoreViewModel.recurrenceFilter.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(top = 12.dp, bottom = 12.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "View Recurring Tasks",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = recurrenceFilter.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            fontStyle = FontStyle.Italic
        )
    }

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
                            dataStoreViewModel.saveRecurrenceFilter(recurrenceType)
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
//    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrioritySelector(
    currentSortType: SortType,
    onSortChange: (SortType) -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    val sortingOption by dataStoreViewModel.priorityOption.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(top = 12.dp, bottom = 12.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Sorting Option",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = sortingOption.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            fontStyle = FontStyle.Italic
        )
    }

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
                        onClick = {
                            onSortChange(sortType)
                            dataStoreViewModel.savePriorityOption(sortType)
                        }
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
//    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemeSelector(
    dataStoreViewModel: DataStoreViewModel
) {
    val theme by dataStoreViewModel.theme.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(top = 12.dp, bottom = 12.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = theme.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            fontStyle = FontStyle.Italic
        )
    }

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

//    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClockTypeSelector(
    dataStoreViewModel: DataStoreViewModel
) {
    val clock by dataStoreViewModel.clockType.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(top = 12.dp, bottom = 12.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Clock Type",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = clock.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            fontStyle = FontStyle.Italic
        )
    }

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

//    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
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
