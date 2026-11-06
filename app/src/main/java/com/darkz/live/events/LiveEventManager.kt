package com.darkz.live.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class EventStats(
    val totalLikes: Int = 0,
    val totalComments: Int = 0,
    val totalGifts: Int = 0,
    val totalSpecialGifts: Int = 0,
    val activeViewers: Int = 1250,
    val isConnected: Boolean = true
)

class LiveEventManager {

    // Hot flow for gameplay mechanics and real-time triggers
    private val _eventFlow = MutableSharedFlow<LiveEvent>(extraBufferCapacity = 64)
    val eventFlow: SharedFlow<LiveEvent> = _eventFlow.asSharedFlow()

    // State flow for dashboard metrics
    private val _stats = MutableStateFlow(EventStats())
    val stats: StateFlow<EventStats> = _stats.asStateFlow()

    // Recent events log
    private val _recentEvents = MutableStateFlow<List<LiveEvent>>(emptyList())
    val recentEvents: StateFlow<List<LiveEvent>> = _recentEvents.asStateFlow()

    fun postEvent(event: LiveEvent) {
        // Emit to hot flow for active games
        _eventFlow.tryEmit(event)

        // Update statistics
        val current = _stats.value
        val updated = when (event.type) {
            LiveEventType.LIKE -> current.copy(totalLikes = current.totalLikes + 1)
            LiveEventType.COMMENT -> current.copy(totalComments = current.totalComments + 1)
            LiveEventType.GIFT -> current.copy(totalGifts = current.totalGifts + 1)
            LiveEventType.SPECIAL_GIFT -> current.copy(totalSpecialGifts = current.totalSpecialGifts + 1)
        }
        _stats.value = updated

        // Keep last 40 events
        val list = _recentEvents.value.toMutableList()
        list.add(0, event)
        if (list.size > 40) {
            list.removeAt(list.size - 1)
        }
        _recentEvents.value = list
    }

    fun setConnectionStatus(connected: Boolean) {
        _stats.value = _stats.value.copy(isConnected = connected)
    }

    fun clearHistory() {
        _recentEvents.value = emptyList()
        _stats.value = EventStats(isConnected = _stats.value.isConnected)
    }
}
