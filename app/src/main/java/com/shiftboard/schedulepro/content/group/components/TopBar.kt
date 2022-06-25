package com.shiftboard.schedulepro.content.group.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@ExperimentalMaterialApi
@Composable
fun TopBar(
    onFilterClicked: () -> Unit,
    onPreviousDatesClicked: () -> Unit,
    onNextDatesClicked: () -> Unit,
    startDate: LocalDate,
    endDate: LocalDate,
    goToTradeScreen: () -> Unit,
    tradeIconVisible: Boolean,
    onCalenderClicked: () -> Unit,
) {
    TopAppBar(
        backgroundColor = Color.White,
        contentColor = Color.Black,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                Spacer(modifier = Modifier.width(14.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "",
                    tint = Color(0XFF265AA5),
                    modifier = Modifier.clickable { onPreviousDatesClicked() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${endDate.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "",
                    tint = Color(0XFF265AA5),
                    modifier = Modifier.clickable { onNextDatesClicked() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_calender),
                    contentDescription = "",
                    tint = Color(0XFF265AA5),
                    modifier = Modifier.clickable { onCalenderClicked() }
                )
            }
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                if (tradeIconVisible){
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Color(0XFF265AA5).copy(alpha = .2f)
                            )
                            .clickable {
                                goToTradeScreen()
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_trade),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            Color(0XFF265AA5).copy(alpha = .2f)
                        )
                        .clickable {
                            onFilterClicked()
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter_icon),
                        contentDescription = "",
                        tint = Color(0XFF265AA5),
                        modifier = Modifier
                            .padding(6.dp)
                            .height(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
            }
        }
    }
}