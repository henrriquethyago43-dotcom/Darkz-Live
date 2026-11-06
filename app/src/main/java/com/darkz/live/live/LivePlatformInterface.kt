package com.darkz.live.live

import com.darkz.live.events.LiveEvent
import kotlinx.coroutines.flow.Flow

enum class SupportedPlatform(val displayName: String, val defaultPort: Int) {
    SIMULATOR("Simulador Interno", 0),
    TIKTOK("TikTok Live", 8080),
    YOUTUBE("YouTube Live", 8081),
    TWITCH("Twitch", 6667),
    CUSTOM_WEBSOCKET("WebSocket Customizado", 9000)
}

data class LiveConnectionConfig(
    val platform: SupportedPlatform = SupportedPlatform.SIMULATOR,
    val streamerUsername: String = "@darkz_official",
    val serverHost: String = "127.0.0.1",
    val serverPort: Int = 8080,
    val autoReconnect: Boolean = true
)

interface LivePlatformInterface {
    val platform: SupportedPlatform
    val isConnected: Boolean
    suspend fun connect(config: LiveConnectionConfig): Result<Unit>
    suspend fun disconnect()
    fun observeEvents(): Flow<LiveEvent>
}
