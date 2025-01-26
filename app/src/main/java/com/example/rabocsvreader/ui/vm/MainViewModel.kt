package com.example.rabocsvreader.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.utils.getFileName
import com.example.core.utils.parseStringToLocalDateTime
import com.example.domain.FileDownloadUseCase
import com.example.domain.fold
import com.example.rabocsvreader.ui.models.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val fileDownloadUseCase: FileDownloadUseCase
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<MainScreenEffect>(replay = 1)
    val uiEffect: SharedFlow<MainScreenEffect> = _uiEffect

    private val _uiListState= MutableStateFlow(MainScreenState(emptyList(), 0))
    val uiListState: StateFlow<MainScreenState> = _uiListState

    fun getFileDownload(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiEffect.emit(MainScreenEffect.Loading(true))
            fileDownloadUseCase.downloadFile(
                url,
                getFileName(url)
            ).fold(
                onSuccess = {
                    parseCsvManually(it)
                },
                onError = {
                    _uiEffect.emit(MainScreenEffect.ShowError("${it.message}"))
                    _uiEffect.emit(MainScreenEffect.Loading(false))
                }
            )
        }
    }

    private suspend fun parseCsvManually(file: File) {
        if (!file.exists()) {
            _uiEffect.emit(MainScreenEffect.ShowError("File does not exist: ${file.path}"))
            _uiEffect.emit(MainScreenEffect.Loading(false))
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
                            _uiListState.updateState(batch, errors)
                            batch.clear()
                        }
                    } catch (e: Exception) {
                        errors++
                        _uiListState.updateState(batch, errors)
                        batch.clear()
                    }
                }

                if (batch.isNotEmpty()) {
                    _uiListState.updateState(batch, errors)
                }
            }
        } catch (e: Exception) {
            _uiEffect.emit(MainScreenEffect.ShowError("Error during file parsing: ${e.message}"))
        } finally {
            _uiEffect.emit(MainScreenEffect.Loading(false))
        }
    }


    private fun MutableStateFlow<MainScreenState>.updateState(
        batch: List<Person>,
        errors: Int
    ) {
        val list = mutableListOf<Person>().apply {
            addAll(_uiListState.value.peopleList)
            addAll(batch)
        }
        update { MainScreenState(list, errors) }
    }

    companion object {
        const val EXPECTED_FIELD_SIZE = 5
        const val BATCH_SIZE = 20
    }
}
