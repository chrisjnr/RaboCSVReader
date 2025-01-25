package com.example.rabocsvreader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.utils.getFileName
import com.example.core.utils.parseStringToLocalDateTime
import com.example.domain.FileDownloadUseCase
import com.example.domain.fold
import com.example.rabocsvreader.ui.models.Person
import com.example.rabocsvreader.ui.vm.MainScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val fileDownloadUseCase: FileDownloadUseCase
) : ViewModel() {

    private val _uiStateFlow = MutableSharedFlow<MainScreenState>(replay = 1)
    val uiStateFlow: SharedFlow<MainScreenState> = _uiStateFlow

    fun getFileDownload(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiStateFlow.emit(MainScreenState.Loading(true))
            fileDownloadUseCase.downloadFile(
                url,
                getFileName(url)
            ).fold(
                onSuccess = {
                    parseCsvManually(it)
                },
                onError = {
                    _uiStateFlow.emit(MainScreenState.ShowError("${it.message}"))
                    _uiStateFlow.emit(MainScreenState.Loading(false))
                }
            )
        }
    }

    private suspend fun parseCsvManually(file: File) {
        if (!file.exists()) {
            _uiStateFlow.emit(MainScreenState.ShowError("File does not exist: ${file.path}"))
            _uiStateFlow.emit(MainScreenState.Loading(false))
            return
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
                                    dob = parseStringToLocalDateTime(fields[3]),
                                    avatar = fields[4]
                                )
                            )
                        }

                        if (batch.size == BATCH_SIZE) {
                            _uiStateFlow.emit(MainScreenState.PeopleListUpdated(batch.toList()))
                            batch.clear()
                        }
                    } catch (e: Exception) {
                        errors++
                        _uiStateFlow.emit(MainScreenState.ParsingError(errors))
                    }
                }

                if (batch.isNotEmpty()) {
                    _uiStateFlow.emit(MainScreenState.PeopleListUpdated(batch.toList()))
                }
            }
        } catch (e: Exception) {
            _uiStateFlow.emit(MainScreenState.ShowError("Error during file parsing: ${e.message}"))
        } finally {
            _uiStateFlow.emit(MainScreenState.Loading(false))
        }
    }

    companion object {
        const val EXPECTED_FIELD_SIZE = 5
        const val BATCH_SIZE = 20
    }
}
