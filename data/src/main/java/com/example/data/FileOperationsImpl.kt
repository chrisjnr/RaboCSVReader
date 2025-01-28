package com.example.data

import com.example.domain.FileOperations
import com.example.domain.Result
import java.io.File
import java.io.InputStream

class FileOperationsImpl(
    private val localFileDirectory: File?
): FileOperations {

    override suspend fun saveFile(inputStream: InputStream?, fileName: String): Result<File> {
        return try {
            if (inputStream == null) {
                return Result.createFailure(Throwable("InputStream is null"))
            }

            val file = File(localFileDirectory, fileName)
            try {
                inputStream.use { it.copyTo(file.outputStream()) }
            } catch (e: Exception) {
                return Result.createFailure(Throwable("Error during file write: ${e.message}"))
            }
            Result.createSuccess(file)
        } catch (e: Exception) {
            Result.createFailure(Throwable("Unexpected error: ${e.message}"))
        }
    }
}
