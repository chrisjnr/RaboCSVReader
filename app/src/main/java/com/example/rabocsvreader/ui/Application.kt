package com.example.rabocsvreader.ui

import android.app.Application
import com.example.data.client.fileModule
import com.example.data.client.networkModule
import com.example.domain.domainModule
import com.example.rabocsvreader.ui.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(applicationModule, fileModule, networkModule, domainModule)
        }
    }
}
