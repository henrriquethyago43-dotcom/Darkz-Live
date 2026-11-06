package com.darkz.live

import android.app.Application
import com.darkz.live.data.SettingsRepository
import com.darkz.live.events.LiveEventManager

class DarkzApplication : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var eventManager: LiveEventManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        settingsRepository = SettingsRepository(applicationContext)
        eventManager = LiveEventManager()
    }

    companion object {
        lateinit var instance: DarkzApplication
            private set
    }
}
