package com.example.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : UiState, Effect : UiEffect, Event : UiEvent> : ViewModel() {

    protected val currentState: State
        get() = _state.value

    protected val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    private val _event: Channel<Event> = Channel()
    val event = _event.receiveAsFlow()

    protected fun setState(reduce: State.() -> State) {
        _state.value = currentState.reduce()
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    fun sendEvent(event: Event) {
        viewModelScope.launch { _event.send(event) }
    }

    protected abstract fun createInitialState(): State

    abstract fun handleEvent(event: Event)
}