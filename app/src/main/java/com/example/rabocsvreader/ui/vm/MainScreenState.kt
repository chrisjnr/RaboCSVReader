package com.example.rabocsvreader.ui.vm

import com.example.rabocsvreader.ui.models.Person

data class MainScreenState(
    val isLoading: Boolean,
    val peopleList: List<Person>,
    val errorCount: Int
) : com.example.core.mvi.UiState()
