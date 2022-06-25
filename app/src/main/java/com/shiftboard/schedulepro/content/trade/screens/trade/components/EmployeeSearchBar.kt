package com.shiftboard.schedulepro.content.trade.screens.trade.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmployeeSearchBar(
    modifier: Modifier = Modifier,
    clickAction: () -> Unit = {},
    startingText: String? = null
) {
    Column {
        Surface(
            shape = RoundedCornerShape(20),
            color = Color.LightGray,
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    clickAction()
                }
        ) {
            Row(
                content = {
                    Icon(Icons.Filled.Search, "", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = startingText ?: "Employee Name",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}