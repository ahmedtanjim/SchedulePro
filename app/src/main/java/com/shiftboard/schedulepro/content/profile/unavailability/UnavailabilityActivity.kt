package com.shiftboard.schedulepro.content.profile.unavailability

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.jakewharton.threetenabp.AndroidThreeTen
import com.shiftboard.schedulepro.content.profile.unavailability.navigation.UnavailabilityNavigation
import com.shiftboard.schedulepro.content.profile.unavailability.screens.AddUnavailabilityScreen
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.AddUnavailabilityScreenViewModel
import com.shiftboard.schedulepro.content.profile.unavailability.screens.UnavailabilityScreen
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.UnavailabilityScreenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalMaterialApi
class UnavailabilityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            AndroidThreeTen.init(this)
            val viewModel by viewModel<UnavailabilityScreenViewModel>()
            val addUnavailabilityScreenViewModel by viewModel<AddUnavailabilityScreenViewModel>()
            val screenState by viewModel.screenState.collectAsState()
//            if (screenState == ScreenState.UnavailabilityScreen) {
//                UnavailabilityScreen(
//                    finish = { finish() },
//                    viewModel = viewModel,
//                    changeScreenState = { viewModel.changeScreenState(it) })
//            } else AddUnavailabilityScreen(
//                changeScreenState = {
//                    viewModel.changeScreenState(it)
//                    viewModel.getData()
//                },
//                viewModel = addUnavailabilityScreenViewModel
//            )
            val navController = rememberNavController()
            UnavailabilityNavigation(
                navController = navController,
                viewModel = viewModel,
                addUnavailabilityScreenViewModel = addUnavailabilityScreenViewModel
            ) {
                finish()
            }

        }
    }
}

