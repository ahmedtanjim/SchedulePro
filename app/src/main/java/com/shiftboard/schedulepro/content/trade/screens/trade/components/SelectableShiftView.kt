package com.shiftboard.schedulepro.content.trade.screens.trade.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.trade.safeParseColor
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.network.model.group.Item
import com.shiftboard.schedulepro.core.network.model.group.ScheduleItemCollection

@Composable
fun SelectableShiftView(
    ownShifts: ScheduleItemCollection?,
    tradAbleShifts: List<TradeShift>,
    maxShiftPerRow: Int,
    fraction: Float,
    ownShift: Boolean,
    selectedOwnSchedules: List<Item>,
    addOrRemoveOwnSchedule: (Boolean, Item) -> Unit,
    selectedEmployeeSchedules: List<TradeShift>,
    addOrRemoveEmployeeSchedule: (Boolean, TradeShift) -> Unit,
    originalShiftId: String
) {
    fun Color.Companion.parse(colorString: String): Color =
        Color(color = safeParseColor(colorString))
    Column(
        modifier = Modifier
            .fillMaxWidth(fraction = fraction)
            .height(
                when (maxShiftPerRow) {
                    1 -> 70.dp
                    2 -> 140.dp
                    3 -> 210.dp
                    4 -> 280.dp
                    5 -> 350.dp
                    else -> 70.dp
                }
            ),
        verticalArrangement = Arrangement.Center
    ) {
        if (ownShift) {
            ownShifts?.items?.forEach { item ->
                var selected by remember {
                    mutableStateOf(if (originalShiftId == item.id) true else selectedOwnSchedules.contains(item))
                }
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            addOrRemoveOwnSchedule(!selected, item)
                            selected = !selected
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier
                                .width(5.dp)
                                .fillMaxHeight()
                                .background(Color.parse(item.color))

                        ) {}
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color
                                        .parse(item.color)
                                        .copy(alpha = 0.1f)
                                )
                        ) {
                            item.shiftTimeCode?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(start = 5.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            item.leaveTypeCode?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(start = 5.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            item.positionCode?.let {
                                Text(
                                    text = it,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                            item.locationCode?.let {
                                Text(
                                    text = it,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        }
                    }


                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 10.dp)
                    ) {
                        if (selected) {
                            Image(
                                painterResource(R.drawable.ic_signupchecked),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painterResource(R.drawable.ic_signupcheckempty),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        } else {
            tradAbleShifts.forEach { item ->
                var selected by remember {
                    mutableStateOf(selectedEmployeeSchedules.contains(item))
                }
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            addOrRemoveEmployeeSchedule(!selected, item)
                            selected = !selected
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier
                                .width(5.dp)
                                .fillMaxHeight()
                                .background(Color.parse(item.Color!!))

                        ) {}
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.parse(item.Color!!).copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = item.shiftCode,
                                modifier = Modifier.padding(start = 5.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.positionCode,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = item.locationCode,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                    }


                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 10.dp)
                    ) {
                        if (selected) {
                            Image(
                                painterResource(R.drawable.ic_signupchecked),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painterResource(R.drawable.ic_signupcheckempty),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }

}