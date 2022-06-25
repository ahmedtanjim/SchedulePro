package com.shiftboard.schedulepro.core.common

import com.shiftboard.schedulepro.core.messaging.MessagingRepo
import org.koin.dsl.module


object CoreInject  {
    val module = module {
        single {
            MessagingRepo(db = get(), api = get(), networkCallImpl = get())
        }
    }
}

