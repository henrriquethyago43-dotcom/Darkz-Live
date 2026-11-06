export interface AndroidFileRecord {
  path: string;
  category: 'gradle' | 'manifest' | 'kotlin' | 'res' | 'docs';
  description: string;
  language: 'kotlin' | 'groovy' | 'xml' | 'markdown' | 'properties';
  content: string;
}

export const ANDROID_PROJECT_FILES: AndroidFileRecord[] = [
  {
    path: 'settings.gradle.kts',
    category: 'gradle',
    description: 'Configuração de repositórios Gradle e inclusão do módulo :app',
    language: 'groovy',
    content: `pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\\\.android.*")
                includeGroupByRegex("com\\\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DarkzLive"
include(":app")`
  },
  {
    path: 'build.gradle.kts',
    category: 'gradle',
    description: 'Build script de nível superior com plugins Android e Kotlin',
    language: 'groovy',
    content: `// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}`
  },
  {
    path: 'gradle/libs.versions.toml',
    category: 'gradle',
    description: 'Catálogo de versões centralizado (Kotlin 2.0.21, AGP 8.5.2, Compose BOM)',
    language: 'properties',
    content: `[versions]
agp = "8.5.2"
kotlin = "2.0.21"
coreKtx = "1.13.1"
lifecycleRuntimeKtx = "2.8.6"
activityCompose = "1.9.3"
composeBom = "2024.10.00"
navigationCompose = "2.8.3"
coroutines = "1.9.0"
datastore = "1.1.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }`
  },
  {
    path: 'gradle/wrapper/gradle-wrapper.properties',
    category: 'gradle',
    description: 'Gradle Wrapper 8.9 oficial',
    language: 'properties',
    content: `distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.9-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists`
  },
  {
    path: 'gradle.properties',
    category: 'gradle',
    description: 'Parâmetros de JVM e AndroidX do Gradle',
    language: 'properties',
    content: `org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
kotlin.code.style=official`
  },
  {
    path: 'app/build.gradle.kts',
    category: 'gradle',
    description: 'Configuração do módulo :app (MinSdk 26, TargetSdk 34, Jetpack Compose)',
    language: 'groovy',
    content: `plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.darkz.live"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.darkz.live"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Coroutines & StateFlow
    implementation(libs.kotlinx.coroutines.android)
    
    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)
}`
  },
  {
    path: 'app/src/main/AndroidManifest.xml',
    category: 'manifest',
    description: 'Manifesto Android com permissões de rede, vibração e Activity única',
    language: 'xml',
    content: `<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:name=".DarkzApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher"
        android:supportsRtl="true"
        android:theme="@style/Theme.DarkzLive"
        android:hardwareAccelerated="true"
        tools:targetApi="34">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"
            android:windowSoftInputMode="adjustResize"
            android:theme="@style/Theme.DarkzLive">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>`
  },
  {
    path: 'app/src/main/java/com/darkz/live/DarkzApplication.kt',
    category: 'kotlin',
    description: 'Classe Application para inicializar o repositório e o LiveEventManager',
    language: 'kotlin',
    content: `package com.darkz.live

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
}`
  },
  {
    path: 'app/src/main/java/com/darkz/live/MainActivity.kt',
    category: 'kotlin',
    description: 'ComponentActivity com suporte a ViewModel e inicialização Compose',
    language: 'kotlin',
    content: `package com.darkz.live

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
}`
  },
  {
    path: 'app/src/main/java/com/darkz/live/DarkzLiveApp.kt',
    category: 'kotlin',
    description: 'Roteamento Jetpack Compose com NavHost para todas as telas',
    language: 'kotlin',
    content: `package com.darkz.live

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkz.live.events.LiveEventManager
import com.darkz.live.settings.SettingsViewModel
import com.darkz.live.ui.screens.DashboardScreen
import com.darkz.live.ui.screens.EventHubScreen
import com.darkz.live.ui.screens.GameScreen
import com.darkz.live.ui.screens.GameSelectScreen
import com.darkz.live.ui.screens.SettingsScreen
import com.darkz.live.ui.theme.DarkzLiveTheme
import com.darkz.live.ui.theme.DarkzVoid

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Game : Screen("game")
    object GameSelect : Screen("game_select")
    object Events : Screen("events")
    object Settings : Screen("settings")
}

@Composable
fun DarkzLiveApp(
    eventManager: LiveEventManager,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()

    DarkzLiveTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkzVoid
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        eventManager = eventManager,
                        onNavigateToGame = { navController.navigate(Screen.Game.route) },
                        onNavigateToGameSelect = { navController.navigate(Screen.GameSelect.route) },
                        onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }

                composable(Screen.Game.route) {
                    GameScreen(
                        eventManager = eventManager,
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }

                composable(Screen.GameSelect.route) {
                    GameSelectScreen(
                        onSelectGame = { gameId ->
                            if (gameId == "darkz_battle") {
                                navController.navigate(Screen.Game.route)
                            }
                        },
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }

                composable(Screen.Events.route) {
                    EventHubScreen(
                        eventManager = eventManager,
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}`
  },
  {
    path: 'app/src/main/java/com/darkz/live/events/LiveEvent.kt',
    category: 'kotlin',
    description: 'Definição de tipos de evento: LIKE, COMMENT, GIFT, SPECIAL_GIFT',
    language: 'kotlin',
    content: `package com.darkz.live.events

import java.util.UUID

enum class LiveEventType(val label: String, val badgeColorHex: String) {
    LIKE("LIKE", "#FF1A2A"),
    COMMENT("COMENTÁRIO", "#00F0FF"),
    GIFT("PRESENTE", "#FFB800"),
    SPECIAL_GIFT("SUPER PRESENTE", "#E50914")
}

data class LiveEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: LiveEventType,
    val senderName: String,
    val senderAvatar: String = "",
    val message: String = "",
    val giftName: String = "",
    val giftValue: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getSummaryText(): String {
        return when (type) {
            LiveEventType.LIKE -> "$senderName curtiu a live (+Disparo Laser)!"
            LiveEventType.COMMENT -> "$senderName comentou: \\"$message\\""
            LiveEventType.GIFT -> "$senderName enviou $giftName ($giftValue) -> BOMBA DE ÁREA!"
            LiveEventType.SPECIAL_GIFT -> "ALERTA: $senderName enviou $giftName -> INVOCANDO CHEFE!"
        }
    }
}`
  },
  {
    path: 'app/src/main/java/com/darkz/live/events/LiveEventManager.kt',
    category: 'kotlin',
    description: 'Gerenciador reativo com Kotlin SharedFlow e StateFlow',
    language: 'kotlin',
    content: `package com.darkz.live.events

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
        _eventFlow.tryEmit(event)

        val current = _stats.value
        val updated = when (event.type) {
            LiveEventType.LIKE -> current.copy(totalLikes = current.totalLikes + 1)
            LiveEventType.COMMENT -> current.copy(totalComments = current.totalComments + 1)
            LiveEventType.GIFT -> current.copy(totalGifts = current.totalGifts + 1)
            LiveEventType.SPECIAL_GIFT -> current.copy(totalSpecialGifts = current.totalSpecialGifts + 1)
        }
        _stats.value = updated

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
}`
  },
  {
    path: 'app/src/main/java/com/darkz/live/events/EventSimulator.kt',
    category: 'kotlin',
    description: 'Simulador com disparadores manuais e modo automático desacoplado',
    language: 'kotlin',
    content: `package com.darkz.live.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class EventSimulator(private val eventManager: LiveEventManager) {

    private val names = listOf(
        "DarkzSniper", "Valkyrie99", "NeonShadow", "CyberGhost",
        "KryptonBR", "AlphaLeader", "RedNova", "SpectreLive",
        "GamerX_BR", "PixelLord", "VoidWalker", "TitanPilot"
    )

    private val comments = listOf(
        "Manda ver, piloto!",
        "Invoca o boss aí!",
        "Desvia dos lasers!",
        "Live insana hoje!",
        "DARKZ LIVE número 1!",
        "Cuidado com a direita!",
        "Combo absurdo!",
        "Solta o especial logo!"
    )

    private val gifts = listOf(
        Pair("Foguete Neon", 50),
        Pair("Míssil Darkz", 100),
        Pair("Super Canhão", 250),
        Pair("Bomba de Fótons", 500)
    )

    private val specialGifts = listOf(
        Pair("TITÃ CYBER GOLIATH", 1000),
        Pair("NAVE-MÃE APOCALIPSE", 2000),
        Pair("CHEFE DRAGÃO NEON", 5000)
    )

    private var autoSimJob: Job? = null

    fun triggerLike() {
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.LIKE,
                senderName = names.random()
            )
        )
    }

    fun triggerComment() {
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.COMMENT,
                senderName = names.random(),
                message = comments.random()
            )
        )
    }

    fun triggerGift() {
        val gift = gifts.random()
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.GIFT,
                senderName = names.random(),
                giftName = gift.first,
                giftValue = gift.second
            )
        )
    }

    fun triggerSpecialGift() {
        val gift = specialGifts.random()
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.SPECIAL_GIFT,
                senderName = names.random(),
                giftName = gift.first,
                giftValue = gift.second
            )
        )
    }

    fun startAutoSimulation(scope: CoroutineScope, intervalMs: Long = 2500L) {
        stopAutoSimulation()
        autoSimJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(intervalMs + Random.nextLong(-500, 1000))
                when (Random.nextInt(100)) {
                    in 0..50 -> triggerLike()
                    in 51..80 -> triggerComment()
                    in 81..94 -> triggerGift()
                    else -> triggerSpecialGift()
                }
            }
        }
    }

    fun stopAutoSimulation() {
        autoSimJob?.cancel()
        autoSimJob = null
    }

    fun isAutoSimulating(): Boolean = autoSimJob?.isActive == true
}`
  },
  {
    path: 'app/src/main/java/com/darkz/live/games/GameEngine.kt',
    category: 'kotlin',
    description: 'Interface modular para suportar qualquer jogo na plataforma DARKZ LIVE',
    language: 'kotlin',
    content: `package com.darkz.live.games

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
}`
  },
  {
    path: 'app/src/main/java/com/darkz/live/games/darkzbattle/Entities.kt',
    category: 'kotlin',
    description: 'Modelos de entidades: Nave espacial, Inimigos, Chefes, Projéteis e Partículas',
    language: 'kotlin',
    content: `package com.darkz.live.games.darkzbattle

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Spacecraft(
    var x: Float = 0f,
    var y: Float = 0f,
    val width: Float = 54f,
    val height: Float = 62f,
    var health: Float = 100f,
    val maxHealth: Float = 100f,
    var shield: Float = 50f,
    val maxShield: Float = 50f,
    var specialLaserTimer: Long = 0L,
    var invincibleTimer: Long = 0L
)

enum class EnemyType {
    DRONE,
    INTERCEPTOR,
    CRUISER,
    COMMUNITY_SPONSORED
}

data class Enemy(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val type: EnemyType,
    var health: Float,
    val maxHealth: Float,
    val width: Float,
    val height: Float,
    val scoreValue: Int,
    val sponsorName: String = "",
    var shootTimer: Long = 0L
)

data class Boss(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "DARKZ GOLIATH",
    var x: Float,
    var y: Float,
    var targetX: Float,
    val width: Float = 180f,
    val height: Float = 120f,
    var health: Float = 1500f,
    val maxHealth: Float = 1500f,
    var phase: Int = 1,
    var attackTimer: Long = 0L,
    var isChargingLaser: Boolean = false,
    val summonedBy: String = ""
)

data class Projectile(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val isPlayer: Boolean,
    val damage: Float,
    val radius: Float = 5f,
    val color: Color,
    val isSpecialSpread: Boolean = false
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var alpha: Float = 1f,
    var radius: Float = 3f,
    var lifeMs: Long = 600L,
    val maxLifeMs: Long = 600L
)`
  },
  {
    path: 'app/src/main/java/com/darkz/live/games/darkzbattle/DarkzBattleEngine.kt',
    category: 'kotlin',
    description: 'Motor do jogo com renderização Android Canvas, física e colisões',
    language: 'kotlin',
    content: `// Ver arquivo completo em app/src/main/java/com/darkz/live/games/darkzbattle/DarkzBattleEngine.kt`
  },
  {
    path: 'app/src/main/java/com/darkz/live/ui/screens/DashboardScreen.kt',
    category: 'kotlin',
    description: 'Dashboard principal em Jetpack Compose com botões e métricas',
    language: 'kotlin',
    content: `// Ver arquivo completo em app/src/main/java/com/darkz/live/ui/screens/DashboardScreen.kt`
  },
  {
    path: 'app/src/main/java/com/darkz/live/ui/screens/GameScreen.kt',
    category: 'kotlin',
    description: 'Tela do jogo com Jetpack Compose Canvas e HUD interativo',
    language: 'kotlin',
    content: `// Ver arquivo completo em app/src/main/java/com/darkz/live/ui/screens/GameScreen.kt`
  },
  {
    path: 'app/src/main/java/com/darkz/live/ui/theme/Color.kt',
    category: 'kotlin',
    description: 'Paleta oficial DARKZ LIVE: Preto, Vermelho Crimson, Branco e Ciano Neon',
    language: 'kotlin',
    content: `package com.darkz.live.ui.theme

import androidx.compose.ui.graphics.Color

val DarkzVoid = Color(0xFF050507)
val DarkzBlack = Color(0xFF0A0A0F)
val DarkzSurface = Color(0xFF12121A)
val DarkzSurfaceElevated = Color(0xFF1C1C28)
val DarkzBorder = Color(0xFF2A2A38)

val DarkzRedPrimary = Color(0xFFE50914)
val DarkzRedNeon = Color(0xFFFF1A2A)
val DarkzRedDark = Color(0xFF8B0000)

val DarkzWhite = Color(0xFFFFFFFF)
val DarkzWhiteMuted = Color(0xFFE2E2EA)
val DarkzGrayText = Color(0xFF8E8E9F)

val DarkzCyanNeon = Color(0xFF00F0FF)
val DarkzAmber = Color(0xFFFFB800)
val DarkzGreenLive = Color(0xFF00FF66)`
  }
];
