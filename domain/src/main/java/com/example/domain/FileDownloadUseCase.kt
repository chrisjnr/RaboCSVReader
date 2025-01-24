package com.example.domain

import java.io.File

interface FileDownloadUseCase {
    suspend fun downloadFile(fileUrl: String, fileName: String): Result<File>
}