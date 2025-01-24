package com.example.domain

import java.io.File

class FileDownloadUseCaseImpl(
    private val fileDownloadRepository: FileDownloadRepository,
    private val fileOperations: FileOperations
) : FileDownloadUseCase {

    override suspend fun downloadFile(fileUrl: String, fileName: String): Result<File> {
        val inputStream = fileDownloadRepository.getFile(fileUrl, fileName)
        return inputStream.fold(
            onSuccess = {
                return@fold fileOperations.saveFile(it, fileName)
            },
            onError = {
                return@fold Result.createFailure(it)
            }
        )
    }
}
