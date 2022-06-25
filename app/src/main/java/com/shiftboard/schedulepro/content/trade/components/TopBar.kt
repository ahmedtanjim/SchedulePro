package com.shiftboard.schedulepro.content.trade.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlin.coroutines.EmptyCoroutineContext.plus

@Composable
fun TopBar(topBarText: String) {
    Surface(
        modifier = Modifier.background(Color.White),
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(Modifier.fillMaxHeight().align(Alignment.CenterStart)) {
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = topBarText,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}