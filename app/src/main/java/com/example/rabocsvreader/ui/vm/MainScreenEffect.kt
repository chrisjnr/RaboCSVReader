package com.example.rabocsvreader.ui.vm

sealed class MainScreenEffect {
    data class Loading(val loading: Boolean) : MainScreenEffect()
    data class ShowError(val message: String) : MainScreenEffect()
}

