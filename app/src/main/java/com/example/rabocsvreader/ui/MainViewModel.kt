package com.example.rabocsvreader.ui

import androidx.lifecycle.viewModelScope
import com.example.core.mvi.BaseViewModel
import com.example.domain.FileDownloadUseCase
import com.example.domain.fold
import com.example.rabocsvreader.ui.models.Person
import com.example.rabocsvreader.ui.vm.MainScreenEffect
import com.example.rabocsvreader.ui.vm.MainScreenEvent
import com.example.rabocsvreader.ui.vm.MainScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val fileDownloadUseCase: FileDownloadUseCase
) : BaseViewModel<MainScreenState, MainScreenEffect, MainScreenEvent>() {

    fun getFileDownload() {
        viewModelScope.launch(Dispatchers.IO) {
            setState {
                copy(isLoading = true)
            }
            fileDownloadUseCase.downloadFile(
                "https://docs.google.com/spreadsheets/d/e/2PACX-1vSjy4ueh-wbIoUIlKu-Sf7ByRyny5tJKocbGdOj1_wQDwRf4vSqGBGdqsPw6Ase1KMEsRgQSJVYhGz3/pub?output=csv",
                "issues.csv"
            ).fold(
                onSuccess = {
                    parseCsvManually(it)
                },
                onError = {
                    sendEffect(
                        MainScreenEffect.Error("Error in saving File")
                    )
                }
            )
        }
    }


    private fun parseCsvManually(file: File) {
        if (!file.exists()) {
            MainScreenEffect.Error("File does not exist: ${file.path}")
        }
        var errors = 0
        try {
            file.bufferedReader().useLines { lines ->
                val iterator = lines.iterator()

                if (iterator.hasNext()) {
                    iterator.next()
                }

                val batch = mutableListOf<Person>()
                iterator.forEach { line ->
                    try {
                        val fields = line.split(",").map { it.trim('"') }
                        if (fields.size == EXPECTED_FIELD_SIZE) {
                            batch.add(
                                Person(
                                    firstName = fields[0],
                                    surname = fields[1],
                                    issueCount = fields[2].toInt(),
                                    dob = fields[3],
                                    avatar = fields[4]
                                )
                            )
                        }

                        if (batch.size == BATCH_SIZE) {
                            setState {
                                copy(peopleList = batch, isLoading = false)
                            }
                            batch.clear()
                        }
                    } catch (e: Exception) {
                        errors++
                        setState {
                            copy(isLoading = false, errorCount = errors)
                        }
                    }
                }

                if (batch.isNotEmpty()) {
                    setState {
                        copy(peopleList = batch, isLoading = false)
                    }
                }
            }
        } catch (e: Exception) {
            sendEffect(
                MainScreenEffect.Error("Error in parsing File")
            )
            setState {
                copy(isLoading = false)
            }
        }
    }


    companion object {
        const val EXPECTED_FIELD_SIZE = 5
        const val BATCH_SIZE = 100
    }

    override fun createInitialState(): MainScreenState {
        return MainScreenState(true, emptyList(), 0)
    }

    override fun handleEvent(event: MainScreenEvent) {

    }

}
