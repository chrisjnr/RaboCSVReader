package com.example.data.client

import com.example.domain.FileDownloadRepository
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://apiwillberelplaced")
            .client(get<OkHttpClient>())
            .build()
    }

    single {
        get<Retrofit>().create(FileDownloadService::class.java)
    }
}

// todo: think of a better name
val fileModule = module {
    single<FileDownloadRepository> {
        FileDownloadRepositoryImpl(
            get()
        )
    }
}