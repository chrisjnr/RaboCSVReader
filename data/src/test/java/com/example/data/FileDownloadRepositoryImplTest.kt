package com.example.data

import com.example.data.client.FileDownloadRepositoryImpl
import com.example.data.client.FileDownloadService
import com.example.domain.FileDownloadRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException


@ExperimentalCoroutinesApi
class FileDownloadRepositoryImplTest {

    private lateinit var fileDownloadService: FileDownloadService
    private lateinit var fileDownloadRepository: FileDownloadRepository

    @Before
    fun setup() {
        fileDownloadService = mockk()
        fileDownloadRepository = FileDownloadRepositoryImpl(fileDownloadService)
    }

    @Test
    fun `test getFile success`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val fileName = "file.csv"
        val inputStream = mockk<InputStream>()
        val response = mockk<Response<ResponseBody>>()

        coEvery { fileDownloadService.downloadFile(fileUrl) } returns response
        every { response.isSuccessful } returns true
        every { response.body()?.byteStream() } returns inputStream

        val result = fileDownloadRepository.getFile(fileUrl, fileName)

        assertTrue(!result.isFailure())
        assertEquals(inputStream, result.requireValue())
    }

    @Test
    fun `test getFile failure - network error`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val fileName = "file.csv"
        val response = mockk<Response<ResponseBody>>()

        coEvery { fileDownloadService.downloadFile(fileUrl) } returns response
        every { response.isSuccessful } returns false

        val result = fileDownloadRepository.getFile(fileUrl, fileName)

        assertTrue(result.isFailure())
        assertEquals("Network Error", result.errorOrNull()?.message)
    }

    @Test
    fun `test getFile failure - IOException`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val fileName = "file.csv"

        coEvery { fileDownloadService.downloadFile(fileUrl) } throws IOException("Failed to download")

        val result = fileDownloadRepository.getFile(fileUrl, fileName)

        assertTrue(result.isFailure())
        assertEquals("Failed to download File java.io.IOException: Failed to download", result.errorOrNull()?.message)
    }

    @Test
    fun `test getFile failure - empty body`() = runTest {
        val fileUrl = "https://example.com/file.csv"
        val fileName = "file.csv"
        val response = mockk<Response<ResponseBody>>()
        coEvery { fileDownloadService.downloadFile(fileUrl) } returns response
        every { response.isSuccessful } returns true
        every { response.body()?.byteStream() } returns null

        val result = fileDownloadRepository.getFile(fileUrl, fileName)

        assertTrue(result.isFailure())
        assertEquals("Error while reading Stream", result.errorOrNull()?.message)
    }
}
