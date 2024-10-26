package com.minimalisttodolist.pleasebethelastrecyclerview.ui.dialogs

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.PriorityColor
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomBox
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.theme.LocalDarkTheme
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.AppEvent
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.AppState
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.AppViewModel
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.TaskEvent
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.TaskState
import com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel.TaskViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    onTaskEvent: (TaskEvent) -> Unit,
    taskState: TaskState,
    onAppEvent: (AppEvent) -> Unit,
    onBack: () -> Unit,
    onHide: () -> Unit,
    appState: AppState
) {
    BasicAlertDialog(
        onDismissRequest = { onAppEvent(AppEvent.HideFeedbackDialog) },
        modifier = Modifier
            .width(350.dp)
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val focusRequester = remember { FocusRequester() }
                val keyboardController = LocalSoftwareKeyboardController.current
                LaunchedEffect(Unit) {
                    delay(100)
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }

                FeedbackTitle (onBack = onBack)
                FeedbackText(appState = appState, focusRequester = focusRequester, onAppEvent = onAppEvent, taskState = TaskState(), onTaskEvent = onTaskEvent)
                UploadFeedbackButton(onAppEvent = onAppEvent, onHide = onHide)
            }
        }
    }
}

@Composable
fun FeedbackTitle(modifier: Modifier = Modifier, onBack: () -> Unit) {
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
            text = "Feedback",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun FeedbackText(
    onTaskEvent: (TaskEvent) -> Unit,
    taskState: TaskState,
    appState: AppState,
    onAppEvent: (AppEvent) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
) {
    val feedbackTextFieldValue = remember(appState.feedbackText) {
        mutableStateOf(TextFieldValue(appState.feedbackText, TextRange(appState.feedbackText.length)))
    }

    TextField(
        value = feedbackTextFieldValue.value,
        onValueChange = {
            feedbackTextFieldValue.value = it
            onAppEvent(AppEvent.SetFeedbackText(it.text))
        },
        maxLines = 10,
        placeholder = {
            Text(
                text = "Share your feedback..",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        ),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )
}

@Composable
fun UploadFeedbackButton(
    onAppEvent: (AppEvent) -> Unit,
    onHide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = 30.dp)
            .clickable {
                onAppEvent(AppEvent.UploadFeedbackText)
                onAppEvent(AppEvent.ClearFeedbackText)
                onHide()
            }
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        Text(
            text = "SEND",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp)
        )
    }
}
