package com.darkblue.minimalisttodolistv4.presentation.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.presentation.components.CustomBox
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.AppEvent
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleExactAlarmPermissionDialog(
    modifier: Modifier = Modifier,
//    appViewModel: AppViewModel
    onDismiss: () -> Unit,
    onAllow: () -> Unit,
    onDisallow: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        CustomBox {
            Column(
                modifier = Modifier.padding(15.dp).width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Schedule Exact Alarm Permission Needed",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "We need this permission to schedule exact alarms. Would you like to allow it?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        onAllow()
                    }) {
                        Text("Allow")
                    }
                    Button(onClick = {
                        onDisallow()
                    }) {
                        Text("Don't Allow")
                    }
                }
            }
        }
    }
}