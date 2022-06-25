package com.shiftboard.schedulepro.common.prefs

import com.shiftboard.schedulepro.FlavorSettings
import com.shiftboard.schedulepro.core.common.utils.PrefsContainer

object AppPrefs : PrefsContainer("app_prefs") {
    var environment by EnumPref(FlavorSettings.defaultEnvironment())
    var fcmToken by SharedPref("")
    var militaryTime by SharedPref(false)
    var tokenSynced by SharedPref(0L)
    var orgID by SharedPref("")
}