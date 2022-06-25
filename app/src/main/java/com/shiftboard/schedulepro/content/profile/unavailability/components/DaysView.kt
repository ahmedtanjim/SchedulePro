package com.shiftboard.schedulepro.content.profile.unavailability.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.content.theme.Primary
import com.shiftboard.schedulepro.core.network.model.profile.DaysOfWeek

@Composable
fun DaysView(
    binaryArray: Array<Boolean>,
) {
    Row {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (binaryArray.component1()) Primary else Color.Transparent)
                .border(width = 1.dp, color = Primary, shape = CircleShape)
        ) {
            Text(
                text = "S",
                modifier = Modifier.align(Alignment.Center),
                color = if (binaryArray.component1()) Color.White else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (binaryArray.component2()) Primary else Color.Transparent)
                .border(width = 1.dp, color = Primary, shape = CircleShape)
        ) {
            Text(
                text = "M",
                modifier = Modifier.align(Alignment.Center),
                color = if (binaryArray.component2()) Color.White else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (binaryArray.component3()) Primary else Color.Transparent)
                .border(width = 1.dp, color = Primary, shape = CircleShape)
        ) {
            Text(
                text = "T",
                modifier = Modifier.align(Alignment.Center),
                color = if (binaryArray.component3()) Color.White else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (binaryArray.component4()) Primary else Color.Transparent)
                .border(width = 1.dp, color = Primary, shape = CircleShape)
        ) {
            Text(
                text = "W",
                modifier = Modifier.align(Alignment.Center),
                color = if (binaryArray.component4()) Color.White else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (binaryArray.component5()) Primary else Color.Transparent)
                .border(width = 1.dp, color = Primary, shape = CircleShape)
        ) {
            Text(
                text = "T",
                modifier = Modifier.align(Alignment.Center),
                color = if (binaryArray.component5()) Color.White else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (binaryArray[5]) Primary else Color.Transparent)
                .border(width = 1.dp, color = Primary, shape = CircleShape)
        ) {
            Text(
                text = "F",
                modifier = Modifier.align(Alignment.Center),
                color = if (binaryArray[5]) Color.White else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (binaryArray[6]) Primary else Color.Transparent)
                .border(width = 1.dp, color = Primary, shape = CircleShape)
        ) {
            Text(
                text = "S",
                modifier = Modifier.align(Alignment.Center),
                color = if (binaryArray[6]) Color.White else Color.Unspecified
            )
        }
    }

}