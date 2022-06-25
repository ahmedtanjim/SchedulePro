package com.shiftboard.schedulepro.content.group.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.network.model.Employee

@Composable
fun EmployeeFilter(
    employeeList: List<Employee>,
    filteredEmployeeList: List<Employee>,
    onFilterChange: (name: Employee, selected: Boolean) -> Unit,
    onAllSelected: (Boolean, List<Employee>) -> Unit,
    onApplyFilter: () -> Unit,
) {
    var tempEmployeeList = remember {
        filteredEmployeeList.toMutableList()
    }

    var allSelected by remember {
        mutableStateOf(filteredEmployeeList.size == employeeList.size && tempEmployeeList.size == employeeList.size && filteredEmployeeList.size > 1)
    }

    var allSelectedState by remember {
        mutableStateOf(filteredEmployeeList.size == employeeList.size && tempEmployeeList.size == employeeList.size && filteredEmployeeList.size > 1)
    }

    var allDeselectedState by remember {
        mutableStateOf(filteredEmployeeList.size != employeeList.size && tempEmployeeList.size != employeeList.size && filteredEmployeeList.isEmpty())
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            item {
                Row(modifier = Modifier.padding(top = 10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Text(
                            text = "Employees",
                            modifier = Modifier.align(Alignment.CenterStart),
                            style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 15.dp)
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    shape = CircleShape,
                                    color = Color.LightGray
                                )
                                .background(
                                    if (allSelected)
                                        Color(
                                            0XFF265AA5
                                        ) else Color.White
                                )
                                .selectable(
                                    selected = allSelected,
                                    onClick = {
                                        if (allSelected) {
                                            onAllSelected(false, employeeList)
                                            allDeselectedState = true
                                        } else {
                                            onAllSelected(true, employeeList)
                                        }
                                        allSelected = !allSelected
                                        allSelectedState = !allSelectedState
                                        tempEmployeeList = if (allSelected) {
                                            filteredEmployeeList.toMutableList()
                                        } else {
                                            mutableListOf()
                                        }

                                    }
                                )

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                contentDescription = "",
                                tint = if (allSelected) Color.White else Color.LightGray.copy(
                                    alpha = .2f
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))

                employeeList.forEach {
                    var employee = filteredEmployeeList.find { employee ->
                        employee.id == it.id
                    }

                    var tempEmployee = tempEmployeeList.find { emp ->
                        emp.id == it.id
                    }

                    val selected = remember {
                        mutableStateOf(employee != null && tempEmployee != null)
                    }

                    LaunchedEffect(key1 = allSelectedState, key2 = allDeselectedState) {
                        if (allSelectedState) selected.value =
                            true else if (allDeselectedState) selected.value = false
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0XFF265AA5),
                                shape = RoundedCornerShape(3.dp)
                            )
                            .selectable(
                                selected = selected.value,
                                onClick = {
                                    if (selected.value) {
                                        onFilterChange(
                                            it,
                                            false
                                        )
                                        tempEmployeeList.remove(it)
                                    } else {
                                        onFilterChange(it, true)
                                        allSelected = !allSelected
                                        allSelectedState = !allSelectedState
                                        tempEmployeeList.add(it)
                                    }
                                    employee = filteredEmployeeList.find { employee ->
                                        it.id == employee.id
                                    }
                                    tempEmployee = tempEmployeeList.find { employee ->
                                        it.id == employee.id
                                    }
                                    selected.value = employee != null && tempEmployee != null
                                    allSelected =
                                        filteredEmployeeList.size == employeeList.size && employeeList.size > 1
                                    allSelectedState =
                                        filteredEmployeeList.size == employeeList.size && employeeList.size > 1
                                }
                            )
                    ) {
                        Text(
                            text = "${it.firstName} ${it.lastName}",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 14.dp),
                            style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
                            overflow = TextOverflow.Clip
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 15.dp, top = 5.dp, bottom = 5.dp)
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    shape = CircleShape,
                                    color = Color.LightGray
                                )
                                .background(
                                    when {
                                        selected.value -> Color(
                                            0XFF265AA5
                                        )
                                        else -> Color.White
                                    }
                                )
                                .selectable(
                                    selected = selected.value,
                                    onClick = {
                                        if (selected.value) {
                                            onFilterChange(
                                                it,
                                                false
                                            )
                                            tempEmployeeList.remove(it)
                                        } else {
                                            onFilterChange(it, true)
                                            allSelected = !allSelected
                                            allSelectedState = !allSelectedState
                                            tempEmployeeList.add(it)
                                        }
                                        employee = filteredEmployeeList.find { employee ->
                                            it.id == employee.id
                                        }
                                        tempEmployee = tempEmployeeList.find { employee ->
                                            it.id == employee.id
                                        }
                                        selected.value = employee != null && tempEmployee != null
                                        allSelected =
                                            filteredEmployeeList.size == employeeList.size && employeeList.size > 1
                                        allSelectedState =
                                            filteredEmployeeList.size == employeeList.size && employeeList.size > 1
                                    }
                                )

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                contentDescription = "",
                                tint = when {
                                    selected.value -> Color.White
                                    !allSelected -> Color.LightGray.copy(alpha = .2f)
                                    else -> Color.LightGray.copy(alpha = .2f)
                                },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    vertical = 16.dp,
                    horizontal = 20.dp
                )
                .align(Alignment.BottomStart)
        ) {
            Button(
                onClick = { onApplyFilter() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0XFF265AA5),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(end = 20.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Apply Filters")
            }
        }
    }
}