package com.minimalisttodolist.pleasebethelastrecyclerview.ui.dialogs

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.ClockType
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomBox
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomDropdownMenu
import com.minimalisttodolist.pleasebethelastrecyclerview.util.darkIcon
import com.minimalisttodolist.pleasebethelastrecyclerview.util.lightIcon
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.AppEvent
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.DataStoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizeDialog(
    modifier: Modifier = Modifier,
    onAppEvent: (AppEvent) -> Unit,
    onBack: () -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    val context = LocalContext.current
    BasicAlertDialog(
        onDismissRequest = {
            onAppEvent(AppEvent.HideTutorialDialog)
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
                PersonalizeTitle( onBack = onBack )
                Theme_Font(onClick = { onAppEvent(AppEvent.ShowFontSettingsDialog) })
                ClockTypeSelector( dataStoreViewModel )
                AppIconSelector( context = context )
//                Tutorial(onClick = { onAppEvent(AppEvent.ShowTutorialDialog) })
            }
        }
    }
}

@Composable
fun PersonalizeTitle(modifier: Modifier = Modifier, onBack: () -> Unit) {
    Box (
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "back arrow",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .padding(end = 12.dp)
                .clickable {
                    onBack()
                }
        )
        Text(
            text = "Personalize",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun AppIconSelector(modifier: Modifier = Modifier, context: Context) {
    var expanded by remember { mutableStateOf(false) }

    // Add this line for rotation animation
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            }
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "App Icon",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 10.dp)
        )
//        Icon(
//            imageVector = Icons.Outlined.ChevronRight,
//            contentDescription = "App Icon Selector Chevron",
//            tint = MaterialTheme.colorScheme.tertiary,
//            modifier = Modifier.rotate(rotationState) // Add this line for rotation
//        )
    }
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
}

@Composable
fun Tutorial(modifier: Modifier = Modifier, onClick: () -> Unit) {
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
            text = "Tutorial",
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
fun Theme_Font(modifier: Modifier = Modifier, onClick: () -> Unit) {
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
            text = "Theme & Font",
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
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
            .padding(top = 16.dp, bottom = 16.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Clock Type",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = clock.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            fontStyle = FontStyle.Italic,
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
}

