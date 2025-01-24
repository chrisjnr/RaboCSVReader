package com.example.data.client

import com.example.domain.FileDownloadRepository
import com.example.domain.Result
import java.io.IOException
import java.io.InputStream

class FileDownloadRepositoryImpl(
    private val fileDownloadService: FileDownloadService,
) : FileDownloadRepository {
    override suspend fun getFile(fileUrl: String, fileName: String): Result<InputStream> {
        return try {
            val response = fileDownloadService.downloadFile(fileUrl)
            if (response.isSuccessful) {
                response.body()?.byteStream()?.let {
                    Result.createSuccess(it)
                } ?: Result.createFailure(Throwable("Error while reading Stream"))
            } else {
                Result.createFailure(Throwable("Network Error"))
            }
        } catch (e: IOException) {
            Result.createFailure(Throwable("Failed to download File $e"))
        }
    }
}
