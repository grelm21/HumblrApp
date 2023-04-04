package com.example.humblr

import android.app.Application
import com.example.humblr.koin.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                jsonModule,
                networkModule,
                repoModule,
                preferencesModule,
                viewModelModule,
            )
        }
    }
}