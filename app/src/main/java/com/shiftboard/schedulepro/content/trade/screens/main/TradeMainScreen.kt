package com.shiftboard.schedulepro.content.trade.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.trade.TradeViewModel
import com.shiftboard.schedulepro.content.trade.screens.confirmation.TradeConfirmationScreen
import com.shiftboard.schedulepro.content.trade.screens.search.SearchScreen
import com.shiftboard.schedulepro.content.trade.screens.trade.TradeScreen
import com.shiftboard.schedulepro.content.trade.screens.trade.components.TradeDetailsSheet
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.concurrent.schedule

@ExperimentalMaterialApi
@Composable
fun TradeMainScreen(
    viewModel: TradeViewModel = viewModel(),
    finish: (from: LocalDate?, to: LocalDate?) -> Unit,
    shiftId: String,
    tradeId: String,
    fromGroup: Boolean,
) {
    val screenState by viewModel.screenState.collectAsState()
    val alertState by viewModel.alertState.collectAsState()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val employees by viewModel.employees.collectAsState()
    val trade by viewModel.trade.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val originalShift by viewModel.originalShift.collectAsState()
    val ownSchedule by viewModel.ownSchedules.collectAsState()
    val ownScheduleLoaded by viewModel.ownScheduleLoaded.collectAsState()
    val progressBarState by viewModel.progressBarState.collectAsState()

//    LaunchedEffect(key1 = true) {
//        Timer().schedule(2000) {373
//            if (!fromGroup) {
//                viewModel.clearOwnSchedule()
//                viewModel.addOrRemoveOwnSchedule(true, originalShift.toItem())
//            }
//        }
//    }

    when (screenState) {
        TradeScreenState.Trade ->
            ModalBottomSheetLayout(
                sheetContent = {
                    TradeDetailsSheet(
                        trade,
                        "Trade Confirmation",
                        submitAction = { viewModel.validateTrade(finish = finish) },
                        cancelAction = {},
                        approveAction = {},
                        declineAction = {},
                        closeAction = {
                            coroutineScope.launch {
                                bottomState.animateTo(ModalBottomSheetValue.Hidden)
                            }
                        },
                        progressBarState = progressBarState
                    )
                },
                sheetState = bottomState,
            ) {
                TradeScreen(
                    confirmation = {
                        if (ownScheduleLoaded && ownSchedule == null) {
                            coroutineScope.launch {
                                viewModel.tradeConfirmation()
                                bottomState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        } else {
                            viewModel.changeScreenState(TradeScreenState.Confirmation)
                        }
                    },
                    onCancelClicked = {
                        finish(null, null)
                    },
                    viewModel = viewModel,
                    fromGroup = fromGroup,
                )
            }
        TradeScreenState.Search ->
            SearchScreen(
                employees = employees,
                onSearchTextChanged = viewModel::changeSearchText,
                onEmployeeSelected = {
                    viewModel.selectEmployee(it)
                },
                searchText = searchText,
            ) {
                finish(null, null)
            }

        TradeScreenState.Loading -> {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    Modifier
                        .width(45.dp)
                        .height(45.dp)
                )
            }
        }
        TradeScreenState.Confirmation -> {
            TradeConfirmationScreen(
                trade = trade,
                onCancelClicked = {
                    viewModel.changeScreenState(TradeScreenState.Trade)
                },
                viewModel = viewModel,
                submitTrade = { viewModel.validateTrade(finish = finish) },
                progressBarState = progressBarState
            )
        }
        else -> {}
    }
    when (alertState) {
        AlertState.NONE -> {

        }
        AlertState.WARN -> {
            AlertDialog(onDismissRequest = viewModel::hideAlert, confirmButton = {
                TextButton(onClick = { viewModel.forceSubmit(finish) })
                { Text(text = stringResource(R.string.submit_anyways)) }
            },
                dismissButton = {
                    TextButton(onClick = viewModel::hideAlert)
                    { Text(text = stringResource(R.string.cancel)) }
                },
                title = { Text(text = stringResource(R.string.violations_detected)) },
                text = { Text(text = viewModel.alertText) }
            )
        }
        AlertState.ERROR -> {
            AlertDialog(onDismissRequest = viewModel::hideAlert, confirmButton = {
                TextButton(onClick = viewModel::hideAlert)
                { Text(text = stringResource(R.string.cancel)) }
            },
                title = { Text(text = stringResource(R.string.critical_violations)) },
                text = { Text(text = viewModel.alertText) }
            )
        }
    }
}