package com.shiftboard.schedulepro.content.profile.unavailability.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shiftboard.schedulepro.content.profile.unavailability.screens.AddUnavailabilityScreen
import com.shiftboard.schedulepro.content.profile.unavailability.screens.UnavailabilityScreen
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.AddUnavailabilityScreenViewModel
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.SharedViewModel
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.UnavailabilityScreenViewModel

@ExperimentalMaterialApi
@Composable
fun UnavailabilityNavigation(
    navController: NavHostController,
    viewModel: UnavailabilityScreenViewModel,
    addUnavailabilityScreenViewModel: AddUnavailabilityScreenViewModel,
    finish: () -> Unit,
) {
    val sharedViewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    NavHost(
        navController = navController,
        startDestination = UnavailabilityScreens.UnavailabilityScreen.route
    ) {
        composable(UnavailabilityScreens.UnavailabilityScreen.route) {
            UnavailabilityScreen(
                finish = finish,
                viewModel = viewModel,
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        composable(
            UnavailabilityScreens.ADDUnavailabilityScreen.route,
        ) {
            AddUnavailabilityScreen(
                navController = navController,
                viewModel = addUnavailabilityScreenViewModel,
                sharedViewModel = sharedViewModel
            )
        }
    }
}