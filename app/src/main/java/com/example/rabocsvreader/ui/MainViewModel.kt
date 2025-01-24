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
            fileDownloadUseCase.downloadFile(
                "https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/main/issues.csv",
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
                                copy(peopleList = batch)
                            }
                            batch.clear()
                        }
                    } catch (e: Exception) {
                        sendEffect(
                            MainScreenEffect.Error("Error in parsing File")
                        )
                    }
                }

                if (batch.isNotEmpty()) {
                    setState {
                        copy(peopleList = batch)
                    }
                }
            }
        } catch (e: Exception) {
            sendEffect(
                MainScreenEffect.Error("Error in parsing File")
            )
        }
    }


    companion object {
        const val EXPECTED_FIELD_SIZE = 5
        const val BATCH_SIZE = 100
    }

    override fun createInitialState(): MainScreenState {
        return MainScreenState(true, emptyList())
    }

    override fun handleEvent(event: MainScreenEvent) {

    }

}
