package com.darkblue.minimalisttodolistv4.ui.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkblue.minimalisttodolistv4.data.model.FontFamilyType
import com.darkblue.minimalisttodolistv4.data.model.ThemeType
import com.darkblue.minimalisttodolistv4.ui.components.CustomBox
import com.darkblue.minimalisttodolistv4.ui.components.CustomDropdownMenu
import com.darkblue.minimalisttodolistv4.viewmodel.DataStoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingsDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
        },
    ) {
        CustomBox {
            Column(
                // Best thing everrrrr -> IntrinsicSize
                modifier = Modifier
                    .padding(15.dp)
                    .width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Font Options",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp)
                )
                FontFamilySelector(dataStoreViewModel = dataStoreViewModel)
                FontWeightSelector(dataStoreViewModel = dataStoreViewModel)
                FontSizeSelector(dataStoreViewModel = dataStoreViewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FontFamilySelector(
    modifier: Modifier = Modifier,
    dataStoreViewModel: DataStoreViewModel
) {
    val fontFamily by dataStoreViewModel.fontFamily.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "Font Family",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        style = MaterialTheme.typography.bodyLarge
    )
    CustomDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        FontFamilyType.entries.forEach { fontFamilyType ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp, end = 25.dp)
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            dataStoreViewModel.saveFontFamily(fontFamilyType)
                        }
                    )
            ) {
                CompleteIconWithoutDelay(isChecked = fontFamily == fontFamilyType)
                Text(
                    fontFamilyType.toDisplayString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = fontFamilyType.getFontFamily()
                )
            }
        }
    }

    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FontWeightSelector(
    dataStoreViewModel: DataStoreViewModel
) {
    val fontWeight by dataStoreViewModel.fontWeight.collectAsState()
    val fontWeights = listOf(
        FontWeight.Thin, FontWeight.Light, FontWeight.Normal,
        FontWeight.Medium, FontWeight.Bold, FontWeight.Black
    )
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "Font Weight",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        style = MaterialTheme.typography.bodyLarge
    )
    CustomDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        fontWeights.forEach { fontWeightType ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp, end = 25.dp)
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            dataStoreViewModel.saveFontWeight(fontWeightType)
                        }
                    )
            ) {
                CompleteIconWithoutDelay(isChecked = fontWeight == fontWeightType)
                Text(
                    fontWeightType.toDisplayString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@Composable
fun FontSizeSelector(
    modifier: Modifier = Modifier,
    dataStoreViewModel: DataStoreViewModel
) {
    val fontSize by dataStoreViewModel.fontSize.collectAsState()

    Text(
        "Select Font Size: ${fontSize}sp",
        fontSize = 16.sp
    )
    Slider(
        value = fontSize.toFloat(),
        onValueChange = { newSize ->
            dataStoreViewModel.saveFontSize(newSize.toInt())
        },
        valueRange = 10f..30f,
        steps = 20
    )
}


fun FontWeight.toDisplayString(): String {
    return when (this) {
        FontWeight.Thin -> "Thin"
        FontWeight.ExtraLight -> "Extra Light"
        FontWeight.Light -> "Light"
        FontWeight.Normal -> "Normal"
        FontWeight.Medium -> "Medium"
        FontWeight.SemiBold -> "Semi Bold"
        FontWeight.Bold -> "Bold"
        FontWeight.ExtraBold -> "Extra Bold"
        FontWeight.Black -> "Black"
        else -> "Normal"
    }
}

fun fontWeightFromDisplayName(displayName: String): FontWeight {
    return when (displayName) {
        "Light" -> FontWeight.Light
        "Thin" -> FontWeight.Thin
        "Normal" -> FontWeight.Normal
        "Medium" -> FontWeight.Medium
        "Bold" -> FontWeight.Bold
        "Black" -> FontWeight.Black
        else -> FontWeight.Normal
    }
}
