package com.darkz.live

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.darkz.live.data.SettingsRepository
import com.darkz.live.events.LiveEventManager
import com.darkz.live.settings.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val settingsRepository by lazy {
        (application as DarkzApplication).settingsRepository
    }

    private val eventManager by lazy {
        (application as DarkzApplication).eventManager
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(settingsRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DarkzLiveApp(
                eventManager = eventManager,
                settingsViewModel = settingsViewModel
            )
        }
    }
}
