package com.darkblue.minimalisttodolistv4.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomBox(
//    width: Dp,
//    height: Dp,
    padding: Dp = 10.dp,
    backgroundColor: Color = Color.Red,
    borderColor: Color = MaterialTheme.colorScheme.onBackground,
    borderWidth: Dp = 2.dp,
    cornerRadiusPercent: Int = 7,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
//            .size(width = width, height = height)
//            .size(width = 350.dp, height = 360.dp)
            .size(800.dp)
            .padding(padding)
            .clip(RoundedCornerShape(percent = cornerRadiusPercent))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(percent = cornerRadiusPercent)
            )
    ) {
        content()
    }
}