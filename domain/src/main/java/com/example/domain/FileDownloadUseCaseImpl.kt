package com.example.domain

import java.io.File

class FileDownloadUseCaseImpl(
    private val fileDownloadRepository: FileDownloadRepository,
    private val fileOperations: FileOperations
) : FileDownloadUseCase {

    override suspend fun downloadFile(fileUrl: String, fileName: String): Result<File> {
        val inputStream = fileDownloadRepository.getFile(fileUrl, fileName)
        return inputStream?.let {
            fileOperations.saveFile(inputStream, fileName)
        } ?: Result.createFailure(Throwable("Failed to download csv"))
    }
}
