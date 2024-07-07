package com.minimalisttodolist.pleasebethelastrecyclerview.ui.dialogs

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.minimalisttodolist.pleasebethelastrecyclerview.ui.components.CustomBox
import com.minimalisttodolist.pleasebethelastrecyclerview.util.vibrate
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Tutorial(
    onDismiss: () -> Unit,
    onDisable: () -> Unit,
    onShowAddTaskDialog: () -> Unit,
    onShowMenuDialog: () -> Unit
) {
    val steps = listOf("Tap to add a Task", "Long Press to open the Menu")
    val pagerState = rememberPagerState(pageCount = { steps.size })

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        CustomBox {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${steps.size}",
                    modifier = Modifier.align(Alignment.End),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodySmall,
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        if (page == 0) {
                            AnimatedClickIcon(onClick = { onShowAddTaskDialog() })
                        } else {
                            AnimatedLongPressIcon(onLongClick = { onShowMenuDialog() })
                        }
                        Text(
                            text = steps[page],
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Box {
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        steps.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (index == pagerState.currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Text(
                        "Disable Tutorial",
                        color = if(pagerState.currentPage == steps.lastIndex) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.BottomEnd).clickable {
                            if(pagerState.currentPage == steps.lastIndex) {
                                onDisable()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedClickIcon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "Animated Click Icon"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = tween(durationMillis = 100), label = "Animate State for alpha"
    )

    LaunchedEffect(Unit) {
        while (true) {
            // Simulate click
            isPressed = true
            delay(250)
            isPressed = false
            delay(1000)
        }
    }

    Icon(
        imageVector = Icons.Outlined.Add,
        contentDescription = "Add task",
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(percent = 25)
            )
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(percent = 7))
            .clickable {
                onClick()
            },
        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedLongPressIcon(modifier: Modifier = Modifier, onLongClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    var isLongPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val scale by animateFloatAsState(
        targetValue = when {
            isLongPressed -> 1.2f
            isPressed -> 0.9f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "Animate Long Press Icon"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isPressed || isLongPressed) 0.7f else 1f,
        animationSpec = tween(durationMillis = 100), label = "Animate Float State for alpha"
    )

    LaunchedEffect(Unit) {
        while (true) {
            // Simulate long press
            isPressed = true
            delay(500)
            isLongPressed = true
            delay(1000)
            isLongPressed = false
            isPressed = false
            delay(1000)
        }
    }

    Icon(
        imageVector = Icons.Outlined.Add,
        contentDescription = "Add task",
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(percent = 25)
            )
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(percent = 7))
            .combinedClickable(
                onLongClick = {
                    vibrate(context = context, strength = 1)
                    onLongClick()
                },
                onClick = {

                }
            ),
        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
    )
}
