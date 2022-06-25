package com.shiftboard.schedulepro.content.trade.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
fun CustomTextField(
    modifier: Modifier = Modifier,
    clickAction: () -> Unit = {},
    startingText: String? = null
) {
    var text by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true){
        startingText?.let {
            text = it
        }
    }

    Column {
        Surface(
            shape = RoundedCornerShape(20),
            color = Color.LightGray,
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    isEditing = true
                    clickAction()
                }
        ) {
            Row(
                content = {
                    Icon(Icons.Filled.Search, "", tint = Color.Gray)
                    if (text.isEmpty() && !isEditing) {
                        Text(
                            text = "Employee Name",
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    } else {
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it }
                        )
                    }

                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}