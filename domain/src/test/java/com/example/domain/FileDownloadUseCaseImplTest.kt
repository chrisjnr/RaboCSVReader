package com.example.domain

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.InputStream

@ExperimentalCoroutinesApi
class FileDownloadUseCaseImplTest {

    private lateinit var fileDownloadRepository: FileDownloadRepository
    private lateinit var fileOperations: FileOperations
    private lateinit var fileDownloadUseCase: FileDownloadUseCaseImpl
    private val fileName = "file.csv"

    @Before
    fun setup() {
        fileDownloadRepository = mockk()
        fileOperations = mockk()
        fileDownloadUseCase = FileDownloadUseCaseImpl(fileDownloadRepository, fileOperations)
    }

    @Test
    fun `test downloadFile success`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val inputStream = mockk<InputStream>()
        val file = mockk<File>()

        coEvery { fileDownloadRepository.getFile(fileUrl, fileName) } returns Result.createSuccess(inputStream)

        coEvery { fileOperations.saveFile(inputStream, fileName) } returns Result.createSuccess(file)

        val result = fileDownloadUseCase.downloadFile(fileUrl, fileName)

        assertTrue(!result.isFailure())
        assertEquals(file, result.requireValue())
    }

    @Test
    fun `test downloadFile failure - repository fails`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val fileName = "file.csv"
        val errorMessage = "File not found"

        coEvery { fileDownloadRepository.getFile(fileUrl, fileName) } returns Result.createFailure(Throwable(errorMessage))

        val result = fileDownloadUseCase.downloadFile(fileUrl, fileName)

        assertTrue(result.isFailure())
        assertEquals(errorMessage, result.errorOrNull()?.message)
    }

    @Test
    fun `test downloadFile failure - save file fails`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val fileName = "file.csv"
        val inputStream = mockk<InputStream>()
        val errorMessage = "Error saving file"

        coEvery { fileDownloadRepository.getFile(fileUrl, fileName) } returns Result.createSuccess(inputStream)

        coEvery { fileOperations.saveFile(inputStream, fileName) } returns Result.createFailure(Throwable(errorMessage))

        val result = fileDownloadUseCase.downloadFile(fileUrl, fileName)

        assertTrue(result.isFailure())
        assertEquals(errorMessage, result.errorOrNull()?.message)
    }

    @Test
    fun `test downloadFile failure - repository and save file both fail`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val fileName = "file.csv"
        val repositoryError = "Repository error"

        coEvery { fileDownloadRepository.getFile(fileUrl, fileName) } returns Result.createFailure(Throwable(repositoryError))

        val result = fileDownloadUseCase.downloadFile(fileUrl, fileName)

        assertTrue(result.isFailure())
        assertEquals(repositoryError, result.errorOrNull()?.message)
    }
}
