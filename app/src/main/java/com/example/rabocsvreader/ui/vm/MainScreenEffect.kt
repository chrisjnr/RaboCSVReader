package com.example.rabocsvreader.ui.vm

sealed class MainScreenEffect : com.example.core.mvi.UiEffect() {
    data class Error(val message: String = "") : MainScreenEffect()
    data object EmptyList : MainScreenEffect()
}