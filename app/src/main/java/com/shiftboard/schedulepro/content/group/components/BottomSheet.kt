package com.shiftboard.schedulepro.content.group.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.content.group.model.enums.BottomSheetState
import com.google.accompanist.flowlayout.FlowRow
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.network.model.Employee
import com.shiftboard.schedulepro.core.network.model.group.GroupFilter

@Composable
fun BottomSheet(
    onBottomSheetStateChange: (BottomSheetState) -> Unit,
    locations: List<GroupFilter>,
    positions: List<GroupFilter>,
    shiftTimes: List<GroupFilter>,
    leaveTypes: List<GroupFilter>,
    teams: List<GroupFilter>,
    onCancelClicked: () -> Unit,
    onUpdateFiltersClicked: (employees: List<Employee>) -> Unit,
    employees: List<Employee>
) {
    BackHandler {
        onCancelClicked()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        FlowRow(modifier = Modifier.padding(18.dp)) {
                            if (employees.isEmpty()) {
                                FilterText(
                                    text = stringResource(R.string.all)
                                )
                            } else {
                                employees.forEach {
                                    FilterText(
                                        text = "${it.firstName} ${it.lastName}",
                                        modifier = Modifier.padding(
                                            bottom = if (it != employees.lastOrNull()) 5.dp else 0.dp,
                                            end = 5.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = stringResource(id = R.string.employees),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = stringResource(R.string.change),
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .clickable {
                                    onBottomSheetStateChange(BottomSheetState.FilterEmployees)
                                },
                            color = Color(0XFF007aff)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        FlowRow(modifier = Modifier.padding(18.dp)) {
                            if (locations.isEmpty()) {
                                FilterText(
                                    text = stringResource(id = R.string.all)
                                )
                            } else {
                                locations.forEach {
                                    it.detailedDescription?.let { it1 ->
                                        FilterText(
                                            text = it1,
                                            modifier = Modifier.padding(
                                                bottom = if (it != locations.lastOrNull()) 5.dp else 0.dp,
                                                end = 5.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = stringResource(R.string.locations),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = stringResource(id = R.string.change),
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .clickable { onBottomSheetStateChange(BottomSheetState.FilterLocations) },
                            color = Color(0XFF007aff)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        FlowRow(modifier = Modifier.padding(18.dp)) {
                            if (positions.isEmpty()) {
                                FilterText(
                                    text = stringResource(id = R.string.all)
                                )
                            } else {
                                positions.forEach {
                                    it.detailedDescription?.let { it1 ->
                                        FilterText(
                                            text = it1,
                                            modifier = Modifier.padding(
                                                bottom = if (it != positions.lastOrNull()) 5.dp else 0.dp,
                                                end = 5.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = stringResource(R.string.positions),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = stringResource(id = R.string.change),
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .clickable { onBottomSheetStateChange(BottomSheetState.FilterPositions) },
                            color = Color(0XFF007aff)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        FlowRow(modifier = Modifier.padding(18.dp)) {
                            if (shiftTimes.isEmpty()) {
                                FilterText(
                                    text = stringResource(id = R.string.all)
                                )
                            } else {
                                shiftTimes.forEach {
                                    it.detailedDescription?.let { it1 ->
                                        FilterText(
                                            text = it1,
                                            modifier = Modifier.padding(
                                                bottom = if (it != shiftTimes.lastOrNull()) 5.dp else 0.dp,
                                                end = 5.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = stringResource(R.string.shift_times),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = stringResource(id = R.string.change),
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .clickable { onBottomSheetStateChange(BottomSheetState.FilterShiftTimes) },
                            color = Color(0XFF007aff)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        FlowRow(modifier = Modifier.padding(18.dp)) {
                            if (leaveTypes.isEmpty()) {
                                FilterText(
                                    text = stringResource(id = R.string.all)
                                )
                            } else {
                                leaveTypes.forEach {
                                    it.detailedDescription?.let { it1 ->
                                        FilterText(
                                            text = it1,
                                            modifier = Modifier.padding(
                                                bottom = if (it != leaveTypes.lastOrNull()) 5.dp else 0.dp,
                                                end = 5.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = stringResource(R.string.leave_types),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = stringResource(id = R.string.change),
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .clickable { onBottomSheetStateChange(BottomSheetState.FilterLeaveTypes) },
                            color = Color(0XFF007aff)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        FlowRow(modifier = Modifier.padding(18.dp)) {
                            if (teams.isEmpty()) {
                                FilterText(
                                    text = stringResource(id = R.string.all)
                                )
                            } else {
                                teams.forEach {
                                    it.detailedDescription?.let { it1 ->
                                        FilterText(
                                            text = it1,
                                            modifier = Modifier.padding(
                                                bottom = if (it != teams.lastOrNull()) 5.dp else 0.dp,
                                                end = 5.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = stringResource(R.string.teams),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 25.dp, top = 5.dp)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = stringResource(id = R.string.change),
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .clickable { onBottomSheetStateChange(BottomSheetState.FilterTeams) },
                            color = Color(0XFF007aff)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    vertical = 16.dp,
                    horizontal = 15.dp
                )
                .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onCancelClicked,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0XFFe8edf5),
                    contentColor = Color(0XFF265AA5),
                ),
                modifier = Modifier.padding(start = 20.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.change))
            }
            Button(
                onClick = {
                    onUpdateFiltersClicked(employees)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0XFF265AA5),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(end = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 3.dp)
            ) {
                Text(
                    text = stringResource(R.string.update_filters),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(7.dp)
                )
            }
        }
    }
}