package com.darkz.live.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserPreferences(
    val soundVolume: Float = 0.8f,
    val musicVolume: Float = 0.6f,
    val vibrationEnabled: Boolean = true,
    val controlSensitivity: Float = 1.0f,
    val highFpsMode: Boolean = true,
    val streamerTag: String = "@darkz_official",
    val selectedGameId: String = "darkz_battle",
    val autoFire: Boolean = true,
    val livePlatform: String = "SIMULATOR"
)

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("darkz_live_prefs", Context.MODE_PRIVATE)

    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        return UserPreferences(
            soundVolume = prefs.getFloat(KEY_SOUND_VOL, 0.8f),
            musicVolume = prefs.getFloat(KEY_MUSIC_VOL, 0.6f),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION, true),
            controlSensitivity = prefs.getFloat(KEY_SENSITIVITY, 1.0f),
            highFpsMode = prefs.getBoolean(KEY_HIGH_FPS, true),
            streamerTag = prefs.getString(KEY_STREAMER_TAG, "@darkz_official") ?: "@darkz_official",
            selectedGameId = prefs.getString(KEY_GAME_ID, "darkz_battle") ?: "darkz_battle",
            autoFire = prefs.getBoolean(KEY_AUTO_FIRE, true),
            livePlatform = prefs.getString(KEY_LIVE_PLATFORM, "SIMULATOR") ?: "SIMULATOR"
        )
    }

    fun updatePreferences(update: (UserPreferences) -> UserPreferences) {
        val newPrefs = update(_userPreferences.value)
        _userPreferences.value = newPrefs
        prefs.edit().apply {
            putFloat(KEY_SOUND_VOL, newPrefs.soundVolume)
            putFloat(KEY_MUSIC_VOL, newPrefs.musicVolume)
            putBoolean(KEY_VIBRATION, newPrefs.vibrationEnabled)
            putFloat(KEY_SENSITIVITY, newPrefs.controlSensitivity)
            putBoolean(KEY_HIGH_FPS, newPrefs.highFpsMode)
            putString(KEY_STREAMER_TAG, newPrefs.streamerTag)
            putString(KEY_GAME_ID, newPrefs.selectedGameId)
            putBoolean(KEY_AUTO_FIRE, newPrefs.autoFire)
            putString(KEY_LIVE_PLATFORM, newPrefs.livePlatform)
            apply()
        }
    }

    companion object {
        private const val KEY_SOUND_VOL = "key_sound_vol"
        private const val KEY_MUSIC_VOL = "key_music_vol"
        private const val KEY_VIBRATION = "key_vibration"
        private const val KEY_SENSITIVITY = "key_sensitivity"
        private const val KEY_HIGH_FPS = "key_high_fps"
        private const val KEY_STREAMER_TAG = "key_streamer_tag"
        private const val KEY_GAME_ID = "key_game_id"
        private const val KEY_AUTO_FIRE = "key_auto_fire"
        private const val KEY_LIVE_PLATFORM = "key_live_platform"
    }
}
