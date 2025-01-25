package com.example.rabocsvreader.di

import com.example.domain.FileOperations
import com.example.rabocsvreader.ui.vm.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {
    single<FileOperations> {
        FileOperationsImpl(
            androidContext()
        )
    }

    viewModel {
        MainViewModel(
            get()
        )
    }
}