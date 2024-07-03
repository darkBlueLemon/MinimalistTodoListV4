package com.darkblue.minimalisttodolistv4.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.ui.components.CustomBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleExactAlarmPermissionDialog(
    modifier: Modifier = Modifier,
    onDismissOrDisallow: () -> Unit,
    onAllow: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onDismissOrDisallow() },
        modifier = Modifier.padding(20.dp)
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Alarms & Reminders permission",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Grant access to set alarms for scheduled notifications",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(width = 0.dp, height = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Cancel",
                        modifier.clickable {
                            onDismissOrDisallow()
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.size(width = 24.dp, height = 0.dp))
                    Text(
                        text = "Allow",
                        modifier.clickable {
                            onAllow()
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
//                    Button(onClick = {
//                        onAllow()
//                    }) {
//                        Text("Allow")
//                    }
//                    Button(onClick = {
//                        onDisallow()
//                    }) {
//                        Text("Don't Allow")
//                    }
                }
            }
        }
    }
}