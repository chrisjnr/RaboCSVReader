package com.example.domain

import java.io.InputStream

interface FileDownloadRepository {
    suspend fun getFile(fileUrl: String, fileName: String): InputStream?
}