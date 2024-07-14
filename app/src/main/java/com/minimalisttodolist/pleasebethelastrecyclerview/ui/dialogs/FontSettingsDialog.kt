package com.minimalisttodolist.pleasebethelastrecyclerview.ui.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.FontFamilyType
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomBox
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomDropdownMenu
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.DataStoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingsDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onBack: () -> Unit,
    dataStoreViewModel: DataStoreViewModel
) {
    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        modifier = modifier
            .width(350.dp)
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FontOptionsTitle(onBack = onBack)
                FontFamilySelector(dataStoreViewModel = dataStoreViewModel)
                FontWeightSelector(dataStoreViewModel = dataStoreViewModel)
                FontSizeSelector(dataStoreViewModel = dataStoreViewModel)
            }
        }
    }
}

@Composable
fun FontOptionsTitle(modifier: Modifier = Modifier, onBack: () -> Unit) {
//    Text(
//        text = "Font Options",
//        style = MaterialTheme.typography.headlineSmall,
//        modifier = Modifier
//            .align(Alignment.CenterHorizontally)
//            .padding(top = 12.dp, bottom = 12.dp)
//    )
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
//            .padding(8.dp)
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "back arrow",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .padding(start = 12.dp, end = 12.dp)
                .clickable {
                    onBack()
                }
        )
        Text(
            text = "Font Options",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
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

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Font Family",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = fontFamily.toDisplayString(),
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

//    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FontWeightSelector(
    dataStoreViewModel: DataStoreViewModel
) {
    val fontWeight by dataStoreViewModel.fontWeight.collectAsState()
    val fontWeights = listOf(
//        FontWeight.W100,
//        FontWeight.W200,
        FontWeight.W300,
        FontWeight.W400,
//        FontWeight.W500,
//        FontWeight.W600,
        FontWeight.W700,
//        FontWeight.W800,
//        FontWeight.W900
    )
//    val fontWeights = listOf(
//        FontWeight.Thin, FontWeight.Light, FontWeight.Normal,
//        FontWeight.Medium, FontWeight.Bold, FontWeight.Black
//    )
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
            text = "Font Weight",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = fontWeight.toDisplayString(),
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = fontWeightType
                )
            }
        }
    }

//    Spacer(modifier = Modifier.size(width = 0.dp, height = 24.dp))
}

@Composable
fun FontSizeSelector(
    modifier: Modifier = Modifier,
    dataStoreViewModel: DataStoreViewModel
) {
    val fontSize by dataStoreViewModel.fontSize.collectAsState()

    Text(
        "Select Font Size: $fontSize",
        fontSize = 16.sp,
        modifier = Modifier
            .padding(top = 12.dp)
    )
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            "A",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.weight(1f)
        )
        Slider(
            value = fontSize.toFloat(),
            onValueChange = { newSize ->
                dataStoreViewModel.saveFontSize(newSize.toInt())
            },
            valueRange = 10f..22f,
            steps = 20,
            modifier = Modifier.weight(1f)
        )
        Text(
            "A",
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.weight(1f)
        )
    }
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
