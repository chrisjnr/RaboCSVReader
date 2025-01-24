package com.example.data.client

import com.example.domain.FileDownloadRepository
import java.io.IOException
import java.io.InputStream

class FileDownloadRepositoryImpl(
    private val fileDownloadService: FileDownloadService,
) : FileDownloadRepository {
    override suspend fun getFile(fileUrl: String, fileName: String): InputStream? {
        return try {
            val response = fileDownloadService.downloadFile(fileUrl)
            if (response.isSuccessful) {
                response.body()?.byteStream() ?: throw Exception("Empty file body")
            } else {
                println("Failed to download file")
                null
            }
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}
