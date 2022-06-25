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
import com.shiftboard.schedulepro.core.network.model.group.GroupFilter

@Composable
fun FilterComponents(
    list: List<GroupFilter>,
    groupFilterList: List<GroupFilter>,
    onFilterChange: (groupFilter: GroupFilter, selected: Boolean) -> Unit,
    onAllSelected: (groupFilters: List<GroupFilter>, Boolean) -> Unit,
    onApplyFilter: () -> Unit,
    filterType: String
) {
    var tempList = remember {
        groupFilterList.toMutableList()
    }

    var allSelected by remember {
        mutableStateOf(list.size == groupFilterList.size && tempList.size == list.size && groupFilterList.size > 0)
    }

    var allSelectedState by remember {
        mutableStateOf(list.size == groupFilterList.size && tempList.size == list.size && groupFilterList.size > 0)
    }

    var allDeSelected by remember {
        mutableStateOf(list.size != groupFilterList.size && tempList.size != list.size && groupFilterList.isEmpty())
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
                            text = filterType,
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
                                    if (allSelected) {
                                        Color(
                                            0XFF265AA5
                                        )
                                    } else Color.White
                                )
                                .selectable(
                                    selected = allSelected,
                                    onClick = {
                                        if (allSelected) {
                                            onAllSelected(list, false)
                                            allDeSelected = true
                                        } else {
                                            onAllSelected(list, true)
                                        }
                                        allSelected = !allSelected
                                        allSelectedState = !allSelectedState
                                        if (allSelected) {
                                            tempList = groupFilterList.toMutableList()
                                        } else {
                                            tempList = mutableListOf()
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
                list.forEach {
                    var filterItem = groupFilterList.find { filterItem ->
                        it == filterItem
                    }
                    var tempItem = tempList.find { item ->
                        it == item
                    }
                    val selected = remember {
                        mutableStateOf(filterItem != null && tempItem != null)
                    }

                    LaunchedEffect(key1 = allSelectedState, key2 = allDeSelected) {
                        if (allSelectedState) selected.value =
                            true else if (allDeSelected) selected.value =
                            false
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
                                        onFilterChange(it, false)
                                        tempList.remove(it)
                                    } else {
                                        onFilterChange(it, true)
                                        allSelected = !allSelected
                                        allSelectedState = !allSelectedState
                                        tempList.add(it)
                                    }
                                    filterItem = groupFilterList.find { filterItem ->
                                        it == filterItem
                                    }
                                    tempItem = tempList.find { item ->
                                        it == item
                                    }
                                    selected.value = filterItem != null && tempItem != null
                                    allSelected = groupFilterList.size == list.size && list.size > 0
                                    allSelectedState =
                                        groupFilterList.size == list.size && list.size > 0
                                }
                            )
                    ) {
                        it.detailedDescription?.let { it1 ->
                            Text(
                                text = it1,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 14.dp),
                                style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
                                overflow = TextOverflow.Clip
                            )
                        }
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
                                    if (selected.value)
                                        Color(
                                            0XFF265AA5
                                        ) else Color.White
                                )
                                .selectable(
                                    selected = selected.value,
                                    onClick = {
                                        if (selected.value) {
                                            onFilterChange(it, false)
                                            tempList.remove(it)
                                        } else {
                                            onFilterChange(it, true)
                                            allSelected = !allSelected
                                            allSelectedState = !allSelectedState
                                            tempList.add(it)
                                        }
                                        filterItem = groupFilterList.find { filterItem ->
                                            it == filterItem
                                        }
                                        tempItem = tempList.find { item ->
                                            it == item
                                        }
                                        selected.value = filterItem != null && tempItem != null
                                        allSelected =
                                            groupFilterList.size == list.size && list.size > 0
                                        allSelectedState =
                                            groupFilterList.size == list.size && list.size > 0
                                    }
                                )

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                contentDescription = "",
                                tint = if (selected.value) Color.White else Color.LightGray.copy(
                                    alpha = .2f
                                ),
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