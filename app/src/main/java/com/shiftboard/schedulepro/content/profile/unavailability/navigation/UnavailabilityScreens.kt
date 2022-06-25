package com.shiftboard.schedulepro.content.profile.unavailability.navigation

sealed class UnavailabilityScreens (val route: String) {
    object UnavailabilityScreen: UnavailabilityScreens("unavailability_screen")
    object ADDUnavailabilityScreen: UnavailabilityScreens("add_unavailability_screen")
}