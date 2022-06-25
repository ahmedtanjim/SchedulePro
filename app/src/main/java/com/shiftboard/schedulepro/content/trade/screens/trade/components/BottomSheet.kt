package com.shiftboard.schedulepro.content.trade.screens.trade.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.network.model.TradeDetailsElement

@Composable
fun TradeDetailsSheet(
    trade: TradeDetailsElement, title: String,
    submitAction: () -> Unit,
    cancelAction: () -> Unit,
    approveAction: () -> Unit,
    declineAction: () -> Unit,
    closeAction: () -> Unit,
    progressBarState: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                elevation = 3.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Divider(
                        modifier = Modifier
                            .height(3.dp)
                            .width(30.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    ) {
                        Row(
                            Modifier
                                .align(Alignment.CenterStart)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_swap_horiz_24),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .width(43.dp)
                                    .height(28.dp)
                                    .padding(start = 12.dp)
                                    .align(Alignment.CenterVertically),
                                colorFilter = ColorFilter.tint(Color.Black)
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    trade.originShifts.forEach { item ->
                        ShiftView(
                            shift = item,
                            showName = item == trade.originShifts.first(),
                            modifier = Modifier,
                            extraContent = {},
                            employee = trade.originEmployee
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    trade.recipientShifts.forEach {
                        ShiftView(
                            shift = it,
                            showName = it == trade.recipientShifts.first(),
                            modifier = Modifier,
                            extraContent = {},
                            employee = trade.recipientEmployee
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                if (trade.actions.contains("SubmitTradeCancel")) {
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            cancelAction()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0XFF265AA5),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                }
                if (trade.actions.contains("SubmitTradeAccept")) {
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            approveAction()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0XFF265AA5),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Accept")
                    }
                }
                if (trade.actions.contains("SubmitTradeDecline")) {
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            declineAction()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0XFFE9EEF6),
                            contentColor = Color(0XFF265AA5)
                        )
                    ) {
                        Text(text = "Decline")
                    }
                }
                if (trade.actions.contains("SubmitTradeRequest")) {
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            submitAction()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0XFF265AA5),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Submit")
                    }
                }

                Spacer(Modifier.weight(1f))
                Button(
                    onClick = {
                        closeAction()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0XFF265AA5),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Close")
                }
            }
        }
        if (progressBarState) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0XFF265AA5)
            )
        }
    }
}