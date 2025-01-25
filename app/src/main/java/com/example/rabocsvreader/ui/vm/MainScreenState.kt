package com.example.rabocsvreader.ui.vm

import com.example.rabocsvreader.ui.models.Person

sealed class MainScreenState {
    data class Loading(val loading: Boolean) : MainScreenState()
    data class ShowError(val message: String) : MainScreenState()
    data class PeopleListUpdated(val people: List<Person>) : MainScreenState()
    data class ParsingError(val errorCount: Int) : MainScreenState()
}
