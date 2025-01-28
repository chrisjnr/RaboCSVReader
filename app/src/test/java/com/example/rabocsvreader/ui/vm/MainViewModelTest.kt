package com.example.rabocsvreader.ui.vm


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.core.utils.testUtils.generateCSVFile
import com.example.domain.FileDownloadUseCase
import com.example.domain.Result
import com.example.rabocsvreader.ui.models.Person
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.IOException

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var fileDownloadUseCase: FileDownloadUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        fileDownloadUseCase = mockk(relaxed = true)
        mainViewModel = MainViewModel(fileDownloadUseCase)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `Given valid file download When file exists Then uiListState contains correct people list`() =
        runTest {
            val testFile = generateCSVFile()
            coEvery { fileDownloadUseCase.downloadFile(any(), any()) } coAnswers {
                delay(500)
                Result.createSuccess(
                    testFile
                )
            }
            mainViewModel.getFileDownload("https://example.com/file.csv")

            val uiStateFlow = mainViewModel.uiListState
            uiStateFlow.test {
                skipItems(1)
                var state = awaitItem()
                val vmTestStateErrorCount = state.errorCount
                val vmTestStateList = state.peopleList
                assertEquals(vmTestStateErrorCount, 0)
                assertEquals(vmTestStateList.size, 3)
                assertEquals(
                    vmTestStateList.first(), Person(
                        "Theo",
                        "Jansen",
                        5,
                        LocalDateTime.of(1978, 1, 2, 0, 0, 0),
                        "https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
                    )
                )
            }
        }

    @Test
    fun `Given file download succeeds When file does not exist Then error effect is emitted`() =
        runTest {
            val path = ""
            val testFile = File(path)
            coEvery { fileDownloadUseCase.downloadFile(any(), any()) } coAnswers {
                delay(500)
                Result.createSuccess(
                    testFile
                )
            }
            mainViewModel.getFileDownload("https://example.com/file.csv")

            val uiEffectFlow = mainViewModel.uiEffect
            uiEffectFlow.test {
                assertEquals(MainScreenEffect.Loading(true), awaitItem())
                advanceUntilIdle()
                assertEquals(MainScreenEffect.ShowError("File does not exist: $path"), awaitItem())
            }
        }

    @Test
    fun `Given file download succeeds When file throws exception while reading Then error effect is emitted`() =
        runTest {
            val restrictedFile = mockk<File>()
            val error = "read error"
            coEvery { restrictedFile.exists() } returns true
            coEvery { restrictedFile.path } throws IOException(error)
            coEvery { fileDownloadUseCase.downloadFile(any(), any()) } coAnswers {
                delay(500)
                Result.createSuccess(
                    restrictedFile
                )
            }
            mainViewModel.getFileDownload("https://example.com/file.csv")

            val uiEffectFlow = mainViewModel.uiEffect
            uiEffectFlow.test {
                skipItems(1)
                assertEquals(
                    MainScreenEffect.ShowError("Error during file parsing: $error"),
                    awaitItem()
                )
                assertEquals(MainScreenEffect.Loading(false), awaitItem())
            }
        }

    @Test
    fun `Given file download fails When exception is thrown Then error effect is emitted`() =
        runTest {
            val errorMessage = "Download failed"
            coEvery { fileDownloadUseCase.downloadFile(any(), any()) } coAnswers {
                delay(500)
                Result.createFailure(
                    Exception(errorMessage)
                )
            }
            mainViewModel.getFileDownload("https://example.com/file.csv")
            val uiEffectFlow = mainViewModel.uiEffect
            uiEffectFlow.test {
                assertEquals(MainScreenEffect.Loading(true), awaitItem())
                assertEquals(MainScreenEffect.ShowError(errorMessage), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

