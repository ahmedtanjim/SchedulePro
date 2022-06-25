package com.shiftboard.schedulepro.content.notifications.messages

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.inject.providers.FirebaseAnalyticsProvider
import com.shiftboard.schedulepro.content.trade.TradeViewModel
import com.shiftboard.schedulepro.content.trade.screens.trade.components.TradeDetailsSheet
import com.shiftboard.schedulepro.content.theme.ScheduleProTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate

class TradeDetailsFragment(
        private val tradeId: String,
        private val actionSuccess: OnActionSuccess
        ) : BottomSheetDialogFragment() {
        fun interface OnActionSuccess {
                fun onSuccess(fromDate: LocalDate, toDate: LocalDate, message: String)
        }
        private val firebaseAnalytics = context?.let { FirebaseAnalyticsProvider(FirebaseAnalytics.getInstance(it)) }

        private val viewModel by viewModel<TradeViewModel>()

        @OptIn(ExperimentalMaterialApi::class)
        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
        ): View {
                viewModel.initViewModel("", tradeId = tradeId, firebaseAnalytics)
                return ComposeView(requireContext()).apply {
                        setContent {
                                val progressBarState by viewModel.progressBarState.collectAsState()
                                ScheduleProTheme {
                                        val trade by viewModel.trade.collectAsState()
                                        TradeDetailsSheet(
                                            trade, "Trade Details",
                                            {viewModel.forceSubmit { _, _ -> done(context.getString(R.string.trade_submitted)) }},
                                            {viewModel.cancelTrade { _, _ -> done(context.getString(R.string.trade_cancelled)) }},
                                            {viewModel.acceptTrade { _, _ -> done(context.getString(R.string.trade_accepted)) }},
                                            {viewModel.declineTrade { _, _ -> done(context.getString(R.string.trade_declined)) }},
                                            {dismiss()},
                                            progressBarState = progressBarState
                                        )
                                }
                        }
                }
        }

        fun done(message: String) {
                actionSuccess.onSuccess(
                        viewModel.trade.value.originShifts.first().shiftDate,
                        viewModel.trade.value.recipientShifts.last().shiftDate,
                        message
                )
                dismiss()
        }
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        }
}
