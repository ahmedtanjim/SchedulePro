package com.shiftboard.schedulepro.content.trade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.fragment.navArgs
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.root.SharedStateViewModel
import com.shiftboard.schedulepro.content.trade.screens.main.TradeMainScreen
import com.shiftboard.schedulepro.content.theme.ScheduleProTheme
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate


class TradeFragment : BaseFragment() {
    override val titleRes: Int = R.string.shift_details
    override val layoutRes: Int = R.layout.shift_detail_fragment

    private val sharedState by sharedViewModel<SharedStateViewModel>()
    private val args by navArgs<TradeFragmentArgs>()
    private val viewModel by viewModel<TradeViewModel>()

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.ShiftDetail

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ScheduleProTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        TradeMainScreen(
                            finish = { from, to ->
                                finish(from = from, to = to)
                            },
                            viewModel = viewModel,
                            shiftId = args.shiftId,
                            tradeId = args.tradeId,
                            fromGroup = args.fromGroup,
                        )
                    }
                }
            }
        }
    }
    private fun finish(from: LocalDate?, to: LocalDate?) {
        tryWithNav {
            popBackStack()
            if (from != null && to != null)
                sharedState.invalidateSchedule(from, to)
            if (!args.fromGroup) popBackStack()
        }
    }

    companion object {
        private const val REQUEST_TAG = "REQUEST_FRAGMENT_TAG"
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ComposablePreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
    }
}