package com.shiftboard.schedulepro.core.messaging

import com.shiftboard.schedulepro.core.common.utils.PrefsContainer
import com.shiftboard.schedulepro.core.common.utils.PrefsManager

object MessagingPrefs : PrefsContainer("message_prefs") {
    var lastNotificationId by SharedPref(0)
}