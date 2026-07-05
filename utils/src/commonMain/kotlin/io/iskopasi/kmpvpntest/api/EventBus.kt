package io.iskopasi.kmpvpntest.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EventBus {
    private val _events = MutableStateFlow("")
    val events: StateFlow<String> = _events.asStateFlow()

    fun sendEvent(event: String) {
        _events.update { event }
    }
}
