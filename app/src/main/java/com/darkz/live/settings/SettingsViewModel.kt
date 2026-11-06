package com.darkz.live.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkz.live.data.SettingsRepository
import com.darkz.live.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<UserPreferences> = repository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.userPreferences.value
        )

    fun setSoundVolume(volume: Float) {
        repository.updatePreferences { it.copy(soundVolume = volume.coerceIn(0f, 1f)) }
    }

    fun setMusicVolume(volume: Float) {
        repository.updatePreferences { it.copy(musicVolume = volume.coerceIn(0f, 1f)) }
    }

    fun toggleVibration(enabled: Boolean) {
        repository.updatePreferences { it.copy(vibrationEnabled = enabled) }
    }

    fun toggleHighFps(enabled: Boolean) {
        repository.updatePreferences { it.copy(highFpsMode = enabled) }
    }

    fun toggleAutoFire(enabled: Boolean) {
        repository.updatePreferences { it.copy(autoFire = enabled) }
    }

    fun setControlSensitivity(value: Float) {
        repository.updatePreferences { it.copy(controlSensitivity = value) }
    }

    fun setStreamerTag(tag: String) {
        repository.updatePreferences { it.copy(streamerTag = tag) }
    }

    fun setLivePlatform(platform: String) {
        repository.updatePreferences { it.copy(livePlatform = platform) }
    }
}
