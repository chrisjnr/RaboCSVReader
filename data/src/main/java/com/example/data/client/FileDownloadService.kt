package com.example.data.client

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileDownloadService {
    @GET
    @Streaming
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody
}