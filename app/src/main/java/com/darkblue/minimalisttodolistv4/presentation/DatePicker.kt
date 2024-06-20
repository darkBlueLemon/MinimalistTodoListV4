package com.darkblue.minimalisttodolistv4.presentation

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
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DatePicker(viewModel: TaskViewModel, onClickAction: () -> Unit) {
//    val state = rememberDatePickerState()
//    val openDialog = remember { mutableStateOf(true) }
//
//    if (openDialog.value) {
//        DatePickerDialog(
//            modifier = Modifier
//                .clip(shape = RoundedCornerShape(percent = 7)),
//            colors = DatePickerDefaults.colors(
//                containerColor = Color.Black,
//            ),
//            onDismissRequest = {
//                openDialog.value = false
//                viewModel.setDate("")
//                onClickAction.invoke()
//            },
//            confirmButton = {
//            },
//            dismissButton = {
//            },
//        ) {
//            Box(
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .clip(shape = RoundedCornerShape(percent = 7))
//                    .background(Color.Black)
//                    .fillMaxWidth()
////                    .size(width = 500.dp, height = 700.dp)
//                    .padding(start = 4.dp, end = 4.dp)
//                    .border(
//                        width = 2.dp,
//                        color = Color.White,
//                        shape = RoundedCornerShape(percent = 7)
//                    )
//                ,
//            ) {
//                Column (
//                    modifier = Modifier,
//
//                    ) {
//                    androidx.compose.material3.DatePicker(
//                        modifier = Modifier
////                            .size(100.dp)
////                            .align(Alignment.Start)
////                            .background(Color.Yellow)
//                        ,
//                        state = state,
//                        showModeToggle = false,
//                        colors = DatePickerDefaults.colors(
//                            containerColor = Color.Black,
//                            titleContentColor = Color.White,
//                            headlineContentColor = Color.White,
//                            weekdayContentColor = Color.White,
//                            subheadContentColor = Color.White,
//
//                            yearContentColor = Color.White,
//                            currentYearContentColor = Color.White,
//                            selectedYearContentColor = Color.Black,
//                            selectedYearContainerColor = Color.White,
//
//                            dayContentColor = Color.White,
//                            disabledDayContentColor = Color.White,
//                            selectedDayContentColor = Color.Black,
//                            disabledSelectedDayContentColor = Color.White,
//                            selectedDayContainerColor = Color.White,
//                            disabledSelectedDayContainerColor = Color.White,
//
//                            todayContentColor = Color.White,
//                            todayDateBorderColor = Color.White,
//
//                            dayInSelectionRangeContentColor = Color.White,
//                            dayInSelectionRangeContainerColor = Color.White,
//
//                            )
//                    )
//                    Row(
//                        modifier = Modifier
//                            .background(Color.Black)
//                            .fillMaxWidth()
//                            .size(100.dp)
//                            .padding(bottom = 20.dp, end = 20.dp)
//                        ,
//                        horizontalArrangement = Arrangement.End
//                    ){
//                        TextButton(
//                            onClick = {
//                                openDialog.value = false
//                                viewModel.setDate("")
//                                onClickAction.invoke()
//                            }
//                        ) {
//                            Text("CANCEL", color = Color.White)
//                        }
//                        TextButton(
//                            onClick = {
//                                openDialog.value = false
//                                onClickAction.invoke()
//                            }
//                        ) {
//                            Text("OK", color = Color.White)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
////    viewModel.setDate(state.selectedDateMillis?.let { viewModel.convertMillisToDate(it) }.toString())
////    if(state.selectedDateMillis != null) Log.d("MYTAG", state.selectedDateMillis.toString())
//    if(state.selectedDateMillis != null) viewModel.setDate(state.selectedDateMillis.toString())
//}
