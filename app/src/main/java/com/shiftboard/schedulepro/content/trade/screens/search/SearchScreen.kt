package com.shiftboard.schedulepro.content.trade.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.content.trade.components.SearchTopBar
import com.shiftboard.schedulepro.core.network.model.Employee

@Composable
fun SearchScreen(
    employees: List<Employee>,
    onSearchTextChanged: (String) -> Unit,
    onEmployeeSelected: (String) -> Unit,
    searchText: String,
    onCancelClicked: () -> Unit,
) {
    BackHandler {
        onCancelClicked()
    }
    Scaffold(
        topBar = {
            SearchTopBar(
                searchText = searchText
            ) {
                onSearchTextChanged(it)
            }
        }
    ) {
        Column(Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(employees.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onEmployeeSelected(employees[index].id)
                            }) {
                            Text(
                                text = "${employees[index].firstName} ${employees[index].lastName}",
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 45.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}