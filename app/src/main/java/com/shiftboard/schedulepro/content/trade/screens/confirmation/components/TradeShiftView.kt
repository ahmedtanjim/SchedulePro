package com.shiftboard.schedulepro.content.trade.screens.confirmation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.network.model.group.Item

@Composable
fun TradeShiftView(
    ownShifts: List<Item>,
    tradAbleShifts: List<TradeShift>,
    maxShiftPerRow: Int,
    fraction: Float,
    ownShift: Boolean,
    selectedOwnSchedule: List<Item>,
    selectedEmployeeSchedules: List<TradeShift>
) {
    fun Color.Companion.parse(colorString: String): Color =
        Color(color = android.graphics.Color.parseColor(colorString))
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
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(height = if (ownShifts.size > 1) 7.dp else 5.dp))
        if (ownShift) {
            ownShifts.forEach { tradeItem ->

                val itemInEmployeeShifts by remember {
                    mutableStateOf(selectedEmployeeSchedules.find { it.id == tradeItem.id })
                }

                val containedInEmployeeShifts by remember {
                    mutableStateOf(itemInEmployeeShifts != null)
                }

                val item by remember {
                    mutableStateOf(
                        tradeItem.toTradeShift()
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(bottom = if (ownShifts.last() == tradeItem) 0.dp else 5.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
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
                                .background(Color.parse(item.Color!!))
                        ) {}
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color
                                        .parse(item.Color!!)
                                        .copy(alpha = 0.1f)
                                )
                        ) {
                            Text(
                                text = item.shiftCode,
                                modifier = Modifier.padding(start = 5.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.positionCode,
                                modifier = Modifier.padding(start = 5.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = item.locationCode,
                                modifier = Modifier.padding(start = 5.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (containedInEmployeeShifts) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 15.dp),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_trade),
                                contentDescription = ""
                            )
                        }
                    }

                }
            }
        } else {
            tradAbleShifts.forEach { tradeItem ->
                val itemInOriginShifts by remember {
                    mutableStateOf(selectedOwnSchedule.find { it.id == tradeItem.id })
                }

                val containedInOriginShift by remember {
                    mutableStateOf(itemInOriginShifts != null)
                }

                val item by remember {
                    mutableStateOf(
                        tradeItem
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(bottom = if (tradAbleShifts.last() == tradeItem) 0.dp else 5.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
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
                                .background(Color.parse(item.Color!!))
                        ) {}
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color
                                        .parse(item.Color!!)
                                        .copy(alpha = 0.1f)
                                )
                        ) {
                            Text(
                                text = item.shiftCode,
                                modifier = Modifier.padding(start = 5.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.positionCode,
                                modifier = Modifier.padding(start = 5.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = item.locationCode,
                                modifier = Modifier.padding(start = 5.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                    if (containedInOriginShift) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 15.dp),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_trade),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}