package com.darkz.live.live

import com.darkz.live.events.LiveEventManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

class LiveStreamConnector(
    private val eventManager: LiveEventManager,
    private val scope: CoroutineScope
) {
    private val _connectionState = MutableStateFlow(ConnectionState.CONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _activeConfig = MutableStateFlow(LiveConnectionConfig())
    val activeConfig: StateFlow<LiveConnectionConfig> = _activeConfig.asStateFlow()

    fun updateConfig(config: LiveConnectionConfig) {
        _activeConfig.value = config
    }

    fun connect() {
        scope.launch(Dispatchers.IO) {
            _connectionState.value = ConnectionState.CONNECTING
            // Simulated connection handshake / future real socket connection
            kotlinx.coroutines.delay(800)
            _connectionState.value = ConnectionState.CONNECTED
            eventManager.setConnectionStatus(true)
        }
    }

    fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTED
        eventManager.setConnectionStatus(false)
    }
}
