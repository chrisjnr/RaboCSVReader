package com.example.rabocsvreader.ui

import android.app.Application
import com.example.data.client.fileModule
import com.example.data.client.networkModule
import com.example.domain.domainModule
import com.example.rabocsvreader.di.applicationModule
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin {
            androidContext(this@Application)
            modules(applicationModule, fileModule, networkModule, domainModule)
        }
    }
}
