package com.darkz.live.games

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.darkz.live.events.LiveEvent
import kotlinx.coroutines.flow.StateFlow

data class GameMeta(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val isAvailable: Boolean = true,
    val iconName: String = "rocket"
)

interface GameEngine {
    val meta: GameMeta
    val scoreState: StateFlow<Long>
    val healthState: StateFlow<Float>
    val waveState: StateFlow<Int>
    val isGameOver: StateFlow<Boolean>
    val isPaused: StateFlow<Boolean>

    fun initGame(screenWidth: Float, screenHeight: Float)
    fun update(deltaTimeMs: Long)
    fun render(drawScope: DrawScope)
    fun onTouchMove(x: Float, y: Float)
    fun onFireButton(pressed: Boolean)
    fun handleLiveEvent(event: LiveEvent)
    fun togglePause()
    fun restartGame()
    fun cleanup()
}
