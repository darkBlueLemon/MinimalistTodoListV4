package com.darkblue.minimalisttodolistv4.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(modifier: Modifier = Modifier, onDateSelected: (LocalDate) -> Unit, closeSelection: () -> Unit) {
    val today = LocalDate.now()
    val selectedDate = remember { mutableStateOf(today) }
    val state = rememberUseCaseState(visible = true, onCloseRequest = { closeSelection() })
    CalendarDialog(
        state = state,
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Date(
            selectedDate = today // highlight today's date
        ) { newDate ->
            selectedDate.value = newDate
            onDateSelected(newDate)
        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerFromComposeSheetsDialog(
    modifier: Modifier = Modifier,
    onTimeSelected: (LocalTime) -> Unit,
    closeSelection: () -> Unit
) {
    val selectedTime = remember { mutableStateOf<LocalTime?>(null) }

    ClockDialog(
        state = rememberUseCaseState(visible = true, onCloseRequest = {
            closeSelection()
        }),
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            selectedTime.value = LocalTime.of(hours, minutes, 0)
            onTimeSelected(selectedTime.value!!)
            // closeSelection() // Uncomment this if you want to close the dialog after selection
        },
        config = ClockConfig(
            boundary = LocalTime.of(0, 0, 0)..LocalTime.of(23, 59, 0),
            defaultTime = selectedTime.value ?: LocalTime.of(8, 20, 0), // Use a default value if selectedTime is null
            is24HourFormat = true // Change to false if you want 12-hour format
        ),
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerFromOldApp(
    modifier: Modifier = Modifier,
    onTimeSelected: (LocalTime) -> Unit,
    closeSelection: () -> Unit
) {
    val selectedTime = remember { mutableStateOf<LocalTime?>(null) }
    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = false
    )
        BasicAlertDialog(
            onDismissRequest = {
                closeSelection()
            },
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(7))
                        .background(MaterialTheme.colorScheme.background)
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(7))
                        .padding(20.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor = MaterialTheme.colorScheme.background,
                                clockDialSelectedContentColor = MaterialTheme.colorScheme.background,
                                clockDialUnselectedContentColor = MaterialTheme.colorScheme.primary,
                                selectorColor = MaterialTheme.colorScheme.primary,
                                periodSelectorBorderColor = MaterialTheme.colorScheme.primary,
                                periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.background,
                                periodSelectorSelectedContentColor = MaterialTheme.colorScheme.background,
                                periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.primary,
                                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.background,
                                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.background,
                                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    closeSelection()
                                }
                            ) {
                                Text(text = "CANCEL", color = MaterialTheme.colorScheme.primary)
                            }

                            TextButton(
                                onClick = {
                                    selectedTime.value = LocalTime.of(timePickerState.hour, timePickerState.minute, 0)
                                    onTimeSelected(selectedTime.value!!)
                                    closeSelection()
                                }
                            ) {
                                Text(text = "OK", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
    }
}
