package com.shiftboard.schedulepro.content.group

import com.shiftboard.schedulepro.content.group.screens.GroupScheduleScreen
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
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.root.BottomNavFragment
import com.shiftboard.schedulepro.content.theme.ScheduleProTheme
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalMaterialApi
class GroupFragment : BottomNavFragment() {
    override val titleRes: Int = R.string.shift_details
    override val layoutRes: Int = R.layout.shift_detail_fragment
    private val viewModel by viewModel<GroupViewModel>()

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.GroupSchedule

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
                        color = Color.White
                    ) {
                        GroupScheduleScreen(
                            viewModel = viewModel
                        ) {
                            tryWithNav {
                                navigate(
                                    GroupFragmentDirections.actionGroupToTradeFragment("", "", true)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    private fun finish() {
        tryWithNav {
            popBackStack()
        }
    }

}

