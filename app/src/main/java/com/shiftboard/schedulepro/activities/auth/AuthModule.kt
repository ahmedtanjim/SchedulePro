package com.shiftboard.schedulepro.activities.auth

import org.koin.dsl.module

object AuthModule {
    val module = module {
        scope<AuthActivity> {  }
    }
}