package com.example.domain

import org.koin.dsl.module

val domainModule = module {
    single<FileDownloadUseCase> {
        FileDownloadUseCaseImpl(
            get(), get()
        )
    }
}
