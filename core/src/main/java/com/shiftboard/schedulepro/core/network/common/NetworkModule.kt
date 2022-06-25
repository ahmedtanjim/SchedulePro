package com.shiftboard.schedulepro.core.network.common

import android.app.Application
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import okhttp3.Cache
import org.koin.dsl.module
import retrofit2.Retrofit

object NetworkModule {
    private const val CACHE_SIZE: Long = 4 * 1024 * 1024

    val module = module {
        single {
            Cache(directory = get<Application>().cacheDir,
                maxSize = CACHE_SIZE)
        }
        single { TokenRefresher(get()) }
        single { AuthInterceptor(get()) }
        single { UrlInterceptor(get()) }
        single { ResultInterceptor(get()) }
        single { ApiVersioningInterceptor() }

        single { NetworkManager.createMoshi() }
        single { NetworkManager.createOkHttpClient(get(), get(), get(), get(), get(), get()) }
        single { NetworkManager.createRetrofit(get(), get()) }

        single<ScheduleProApi> {
            get<Retrofit>().create(ScheduleProApi::class.java)
        }

        single { NetworkErrorHandler(get(), get(), get()) }
    }
}