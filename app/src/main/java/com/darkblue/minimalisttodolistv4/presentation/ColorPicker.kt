import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.darkblue.minimalisttodolistv4.presentation.TaskViewModel

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var hue by remember { mutableStateOf(initialColor.toHsv()[0]) }
    var saturation by remember { mutableStateOf(initialColor.toHsv()[1]) }
    var value by remember { mutableStateOf(initialColor.toHsv()[2]) }

    val color = remember(hue, saturation, value) {
        Color.hsv(hue, saturation, value)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Hue Selector
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        hue = ((change.position.x / size.width) * 360).coerceIn(0f, 360f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                        )
                    )
                )
                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset((hue / 360) * size.width - 5.dp.toPx(), -5.dp.toPx()),
                    size = Size(10.dp.toPx(), size.height + 10.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Saturation and Value Selector
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        saturation = (change.position.x / size.width).coerceIn(0f, 1f)
                        value = (1 - (change.position.y / size.height)).coerceIn(0f, 1f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.White, Color.hsv(hue, 1f, 1f))
                    )
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black)
                    )
                )
                drawCircle(
                    color = Color.Black,
                    center = Offset(saturation * size.width, (1 - value) * size.height),
                    radius = 10.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Color Button
        Button(
            onClick = {
                onColorSelected(color)
            },
            colors = ButtonDefaults.buttonColors(containerColor = color),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(text = "Select Color", color = if (value < 0.5f) Color.White else Color.Black)
        }
    }
}

@Composable
fun DynamicTheme(
    primaryColor: Color,
    content: @Composable () -> Unit
) {
    val dynamicColors = lightColorScheme(
        primary = primaryColor,
        secondary = primaryColor,
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    MaterialTheme(
        colorScheme = dynamicColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

@Composable
fun ColorPickScreen(viewModel: TaskViewModel ) {
    DynamicTheme(primaryColor = viewModel.taskColor) {
        Surface {
            ColorPicker(
                initialColor = viewModel.taskColor,
                onColorSelected = { color ->
                    viewModel.updateTaskColor(color)
                }
            )
        }
    }
}

fun Color.toHsv(): FloatArray {
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt(),
        hsv
    )
    return hsv
}
