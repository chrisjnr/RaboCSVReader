package com.example.domain

import java.io.File
import java.io.InputStream

interface FileOperations {
    suspend fun saveFile(inputStream: InputStream, fileName: String): Result<File>
}