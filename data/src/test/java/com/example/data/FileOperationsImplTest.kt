package com.example.data

import com.example.core.utils.testUtils.generateCSVFile
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.InputStream

@ExperimentalCoroutinesApi
class FileOperationsImplTest {


    private lateinit var tempDirectory: File

    @MockK
    private lateinit var mockInputStream: InputStream

    private lateinit var fileOperations: FileOperationsImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        tempDirectory = File.createTempFile("testDir", "").apply {
            delete()
            mkdir()
        }
        fileOperations = FileOperationsImpl(tempDirectory)
    }

    @After
    fun tearDown() {
        unmockkAll()
        tempDirectory.deleteRecursively()
    }

    @Test
    fun `test saveFile success`() = runTest {
        val file = generateCSVFile()
        val fileName = file.name
        fileOperations = FileOperationsImpl(file.parentFile)
        val result = fileOperations.saveFile(file.inputStream(), fileName)
        assertFalse(result.isFailure())
        assertEquals(result.valueOrNull(), file)
        assertEquals(file, result.valueOrNull())
    }


    @Test
    fun `test saveFile failure error in input stream`() = runTest {
        val file = File("")
        val error = "input stream error"
            fileOperations = FileOperationsImpl(file.parentFile)
            val result = fileOperations.saveFile(file.parentFile?.inputStream(), error)
            assertEquals(result.errorOrNull()!!.message, "InputStream is null")
    }

    @Test
    fun `test saveFile failure error in reading file`() = runTest {
      val error = "error in saving file"
        val fileName = "testFile.txt"
            coEvery { mockInputStream.read(any()) } throws Exception(error)
            val result = fileOperations.saveFile(mockInputStream, fileName)
            assertEquals(result.errorOrNull()!!.message, "Error during file write: $error")
    }
}
