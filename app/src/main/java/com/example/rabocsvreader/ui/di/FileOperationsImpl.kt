package com.example.rabocsvreader.ui.di

import android.content.Context
import android.util.Log
import com.example.domain.FileOperations
import com.example.domain.Result
import java.io.File
import java.io.InputStream

class FileOperationsImpl(
    private val context: Context
): FileOperations {

    override suspend fun saveFile(inputStream: InputStream, fileName: String): Result<File> {
        val file = File(context.filesDir, fileName)
        try {
            inputStream.copyTo(file.outputStream())
        }catch (e:Exception) {
            Log.e("error", "copy failed")
            return Result.createFailure(Throwable("Error"))
        }
        inputStream.close()
        return Result.createSuccess(file)
    }
}