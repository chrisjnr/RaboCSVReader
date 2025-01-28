package com.example.rabocsvreader.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.core.utils.testUtils.generateCSVFile
import com.example.core.utils.testUtils.generateErrorRow
import com.example.domain.FileDownloadUseCase
import com.example.domain.Result
import com.example.rabocsvreader.R
import com.example.rabocsvreader.ui.vm.MainScreenEffect
import com.example.rabocsvreader.ui.vm.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.io.File


@LargeTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@OptIn(DelicateCoroutinesApi::class)
class MainActivityTest : KoinTest {

    private val fileDownloadUseCase: FileDownloadUseCase = mockk(relaxed = true)
    private lateinit var mainViewModel: MainViewModel
    val scenario = ActivityTestRule(MainActivity::class.java, false, false)


    @Before
    fun setUp() {
        stopKoin()
        mainViewModel = MainViewModel(fileDownloadUseCase)
        startKoin {
            loadKoinModules(
                module {
                    viewModel { mainViewModel }
                }
            )
        }
    }


    @Test
    fun testNetworkError_ShowsSnackbarErrorMessage() {
        coEvery {
            fileDownloadUseCase.downloadFile(any(), any())
        } returns Result.createFailure(Throwable("Network Error"))

        GlobalScope.launch(Dispatchers.IO) {
            mainViewModel.uiEffect.collect { effect ->
                if (effect is MainScreenEffect.ShowError) {
                    onView(withId(com.google.android.material.R.id.snackbar_text))
                        .check(matches(withText("Network Error")))
                }

                if (effect is MainScreenEffect.Loading && !effect.loading) {

                    onView(withId(R.id.pbLoading))
                        .check(matches(withEffectiveVisibility(Visibility.GONE)))
                }
            }
        }

        scenario.launchActivity(null)

    }


    @Test
    fun testFileDoesNotExist_ShowsSnackbarFileDoesNotExist() {
        val path = ""
        val testFile = File(path)
        coEvery { fileDownloadUseCase.downloadFile(any(), any()) } returns Result.createSuccess(
            testFile
        )

        GlobalScope.launch(Dispatchers.IO) {
            mainViewModel.uiEffect.collect { effect ->
                if (effect is MainScreenEffect.ShowError) {
                    onView(withId(com.google.android.material.R.id.snackbar_text))
                        .check(matches(withText("File does not exist: $path")))
                }

                if (effect is MainScreenEffect.Loading && !effect.loading) {
                    onView(withId(R.id.pbLoading))
                        .check(matches(withEffectiveVisibility(Visibility.GONE)))
                }
            }
        }

        scenario.launchActivity(null)
    }

    @Test
    fun testValidData_DisplaysRecyclerViewItemsCorrectly() {
        coEvery {
            fileDownloadUseCase.downloadFile(any(), any())
        } returns Result.createSuccess(generateCSVFile())


        GlobalScope.launch(Dispatchers.IO) {
            mainViewModel.uiEffect.collect {
                if (it is MainScreenEffect.Loading && !it.loading) {
                    onView(withId(R.id.rvProfiles)).check(matches(isDisplayed()))
                    onView(withId(R.id.rvProfiles)).perform(
                        scrollToPosition<RecyclerView.ViewHolder>(
                            0
                        )
                    )
                    onView(withText("Theo Jansen")).check(matches(isDisplayed()))
                    onView(withText("02 Jan 1978")).check(matches(isDisplayed()))
                    onView(withText("5")).check(matches(isDisplayed()))
                    onView(withId(R.id.rvProfiles)).check { view, _ ->
                        val recyclerView = view as RecyclerView
                        val itemCount = recyclerView.adapter?.itemCount ?: 0
                        assertEquals("The recyclerView should have 3 items", 3, itemCount)
                    }
                }
            }
        }
        scenario.launchActivity(null)
    }


    @Test
    fun testFileWithErrors_DisplaysErrorCountInRecyclerView() {
        val errorList = generateErrorRow().take(2)
        coEvery {
            fileDownloadUseCase.downloadFile(any(), any())
        } returns Result.createSuccess(generateCSVFile(moreRows = errorList))

        GlobalScope.launch(Dispatchers.IO) {
            mainViewModel.uiEffect.collect {
                if (it is MainScreenEffect.Loading && !it.loading) {
                    onView(withId(R.id.rvProfiles)).check(matches(isDisplayed()))
                    onView(withId(R.id.rvProfiles)).perform(
                        scrollToPosition<RecyclerView.ViewHolder>(
                            0
                        )
                    )
                    onView(withText("Theo Jansen")).check(matches(isDisplayed()))
                    onView(withText("02 Jan 1978")).check(matches(isDisplayed()))
                    onView(withText("5")).check(matches(isDisplayed()))
                    onView(withId(R.id.tvNoOfErrors)).check(matches(withText(errorList.size.toString())))
                }
            }
        }

        scenario.launchActivity(null)
    }


    @After
    fun tearDown() {
        stopKoin()
        scenario.finishActivity()
    }

}
