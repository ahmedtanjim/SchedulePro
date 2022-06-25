package com.shiftboard.schedulepro.content.profile.unavailability.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.profile.unavailability.components.RecurrenceView
import com.shiftboard.schedulepro.content.profile.unavailability.components.UnavailabilityView
import com.shiftboard.schedulepro.content.profile.unavailability.navigation.UnavailabilityScreens
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.SharedViewModel
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.UnavailabilityScreenViewModel
import com.shiftboard.schedulepro.content.theme.Primary

@ExperimentalMaterialApi
@Composable
fun UnavailabilityScreen(
    finish: () -> Unit,
    viewModel: UnavailabilityScreenViewModel,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val recurrences by viewModel.recurrences.collectAsState()
    val recurrencesLoading by viewModel.recurrencesLoading.collectAsState()
    val unavailabilities by viewModel.unavailabilities.collectAsState()
    val unavailabilitiesLoading by viewModel.unavailabilitiesLoading.collectAsState()
    val tabIndex by sharedViewModel.tabIndex.collectAsState()
    val tabTitles =
        listOf(stringResource(R.string.recurrence), stringResource(R.string.unavailabilities))
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.getData()
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { finish() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "",
                            tint = Primary
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.manage_unavailability),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = {
                            sharedViewModel.changeIsEditing(false)
                            sharedViewModel.changeSelectedUnavailability(null)
                            sharedViewModel.changeSelectedRecurrence(null)
                            navController.navigate(UnavailabilityScreens.ADDUnavailabilityScreen.route)
                        }, modifier = Modifier
                            .padding(end = 3.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_add_circle_outline_24),
                            contentDescription = "",
                            tint = Primary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = tabIndex,
                        backgroundColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth(.9f)
                            .align(Alignment.Center),
                        indicator = {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(it[tabIndex]),
                                color = Primary
                            )
                        }
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = tabIndex == index,
                                onClick = { sharedViewModel.changeTabIndex(index) },
                                text = { Text(text = title) },
                            )
                        }
                    }
                }
            }
            if (tabIndex == 0) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (recurrencesLoading) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (recurrences.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 150.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_event_available_24),
                                contentDescription = "",
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = stringResource(R.string.there_are_no_unavailabilities_curreently),
                                style = MaterialTheme.typography.h5,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Button(
                                onClick = {
                                    sharedViewModel.changeIsEditing(false)
                                    sharedViewModel.changeRecurrence(true)
                                    sharedViewModel.changeSelectedUnavailability(null)
                                    sharedViewModel.changeSelectedRecurrence(null)
                                    navController.navigate(UnavailabilityScreens.ADDUnavailabilityScreen.route)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth(.6f)
                            ) {
                                Text(text = stringResource(R.string.create_new))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(top = 25.dp)
                                .fillMaxSize()
                        ) {
                            items(recurrences) { recurrence ->
                                RecurrenceView(recurrence = recurrence) {
                                    sharedViewModel.changeIsEditing(isEditing = true)
                                    sharedViewModel.changeRecurrence(true)
                                    sharedViewModel.changeSelectedRecurrence(recurrence = it)
                                    navController.navigate(UnavailabilityScreens.ADDUnavailabilityScreen.route)
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (unavailabilitiesLoading) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (unavailabilities.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 150.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_event_available_24),
                                contentDescription = "",
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = stringResource(R.string.there_are_no_unavailabilities_curreently),
                                style = MaterialTheme.typography.h5,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Button(
                                onClick = {
                                    sharedViewModel.changeIsEditing(false)
                                    sharedViewModel.changeSelectedUnavailability(null)
                                    sharedViewModel.changeSelectedRecurrence(null)
                                    navController.navigate(UnavailabilityScreens.ADDUnavailabilityScreen.route)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth(.6f)
                            ) {
                                Text(text = stringResource(R.string.create_new))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(top = 25.dp)
                                .fillMaxSize()
                        ) {
                            items(unavailabilities) { unavailability ->
                                UnavailabilityView(
                                    unavailability = unavailability,
                                    onEditButtonClicked = {
                                        sharedViewModel.changeIsEditing(isEditing = true)
                                        sharedViewModel.changeRecurrence(false)
                                        sharedViewModel.changeSelectedUnavailability(unavailability = it)
                                        navController.navigate(UnavailabilityScreens.ADDUnavailabilityScreen.route)
                                    },
                                    onRepeatButtonClicked = { recurrenceId ->
                                        val recurrence = recurrences.find { it.id == recurrenceId }
                                        if (recurrence != null) {
                                            sharedViewModel.changeIsEditing(isEditing = true)
                                            sharedViewModel.changeRecurrence(true)
                                            sharedViewModel.changeSelectedRecurrence(recurrence = recurrence)
                                            navController.navigate(UnavailabilityScreens.ADDUnavailabilityScreen.route)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getText(R.string.existing_unavailabilities_error),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
