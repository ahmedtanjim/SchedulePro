package com.shiftboard.schedulepro.content.trade.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.R

@Composable
fun SearchTopBar(searchText: String, onValueChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.background(Color.White),
        elevation = 3.dp
    ) {
        val focusRequester = remember { FocusRequester() }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {

            var isEditing by remember { mutableStateOf(false) }
            Column {
                Surface(
                    shape = RoundedCornerShape(20),
                    color = Color.LightGray,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .fillMaxWidth()
                        .clickable {
                            isEditing = true
                            focusRequester.requestFocus()
                        }
                ) {
                    Row(
                        content = {
                            Icon(Icons.Filled.Search, "", tint = Color.Gray)
                            Box(modifier = Modifier.fillMaxWidth().padding(start = 6.dp)){
                                if (searchText.isEmpty() && !isEditing) {
                                    Text(
                                        text = stringResource(R.string.employee_name),
                                        color = Color.DarkGray,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 2.5.dp)
                                    )
                                }
                                BasicTextField(
                                    value = searchText,
                                    onValueChange = { onValueChange(it) },
                                    modifier = Modifier
                                        .focusRequester(focusRequester)
                                        .padding(top = 2.5.dp)
                                )

                            }
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }


        }
    }
}