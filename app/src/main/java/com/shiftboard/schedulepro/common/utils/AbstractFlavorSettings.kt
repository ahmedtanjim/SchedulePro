package com.shiftboard.schedulepro.common.utils

import android.app.Application
import com.shiftboard.schedulepro.core.Environment
import okhttp3.logging.HttpLoggingInterceptor

interface AbstractFlavorSettings {
    fun defaultEnvironment(): Environment
    fun httpLoggingInterceptor(): HttpLoggingInterceptor
    fun versionName(): String
    fun canUseDebugMode(): Boolean
    fun debugUsers(): List<String> = listOf()
    fun initFlavor(app: Application)
}