# DARKZ LIVE — Android Nativo (Fase 1)

Aplicativo Android nativo para transmissões ao vivo com gameplay arcade interativo em tempo real. Desenvolvido em **Kotlin**, **Jetpack Compose** e **Android Canvas**.

---

## 🎮 Identidade Visual e Arquitetura

- **Nome:** DARKZ LIVE
- **Pacote:** `com.darkz.live`
- **MinSdk:** 26 (Android 8.0 Oreo)
- **TargetSdk / CompileSdk:** 34 (Android 14)
- **Linguagem:** Kotlin 2.0.21
- **UI Toolkit:** Jetpack Compose (BOM 2024.10.00)
- **Design System:** Paleta Futurista Dark Arcade (Preto `#050507`, Vermelho `#E50914`, Branco `#FFFFFF` e Ciano Neon `#00F0FF`).

---

## 📂 Estrutura de Módulos e Pacotes

```text
app/src/main/java/com/darkz/live/
├── DarkzApplication.kt        # Inicialização do repositório e EventManager
├── DarkzLiveApp.kt            # Navegação centralizada Jetpack Compose (NavHost)
├── MainActivity.kt            # ComponentActivity única com injeção de ViewModel
├── data/
│   ├── SettingsRepository.kt  # Persistência local offline via SharedPreferences
│   └── UserPreferences.kt     # Modelo de dados de configurações do usuário
├── events/
│   ├── LiveEvent.kt           # Modelos de eventos (LIKE, COMMENT, GIFT, SPECIAL_GIFT)
│   ├── LiveEventManager.kt    # Barramento de eventos reativo com SharedFlow e StateFlow
│   └── EventSimulator.kt      # Simulador de live com gerador automático e manual
├── games/
│   ├── GameEngine.kt          # Interface modular para suportar novos jogos
│   └── darkzbattle/
│       ├── DarkzBattleEngine.kt # Engine do jogo com loop 60 FPS e Android Canvas
│       └── Entities.kt        # Nave, Inimigos, Chefes, Projéteis e Partículas
├── live/
│   ├── LivePlatformInterface.kt # Interface para integrações futuras (TikTok, YouTube, Twitch)
│   └── LiveStreamConnector.kt # Gerenciador de conexão com status de rede
├── settings/
│   └── SettingsViewModel.kt   # ViewModel para gerenciamento de preferências
└── ui/
    ├── components/
    │   └── ArcadeComponents.kt # Botões chanfrados, cabeçalho e cards estilizados
    ├── screens/
    │   ├── DashboardScreen.kt  # Menu principal com status, métricas e atalhos
    │   ├── GameScreen.kt       # Tela do jogo com Canvas, HUD e barra de simulação
    │   ├── EventHubScreen.kt   # Central de eventos e testes manuais/automáticos
    │   ├── GameSelectScreen.kt # Catálogo modular de jogos disponíveis e futuros
    │   └── SettingsScreen.kt   # Configurações de áudio, sensibilidade e sistema
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## ⚡ Como Compilar e Gerar o APK no Android Studio

### Método 1: Via Android Studio (Recomendado)
1. Abra o **Android Studio** (versão Jellyfish, Koala, Ladybug ou superior).
2. Selecione **File > Open** e selecione a pasta raiz deste projeto.
3. Aguarde o Android Studio sincronizar o Gradle automaticamente (`Sync Project with Gradle Files`).
4. Conecte seu dispositivo Android com depuração USB ativada ou inicie um Emulador com API 26+.
5. Clique no botão **Run** (`Shift + F10`) ou selecione **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. O arquivo APK será gerado em:
   `app/build/outputs/apk/debug/app-debug.apk`

### Método 2: Via Linha de Comando (Gradle Wrapper)
```bash
# No Linux / macOS:
./gradlew assembleDebug

# No Windows:
gradlew.bat assembleDebug
```

---

## 🚀 Mecânicas Interativas do DARKZ BATTLE

| Evento | Ação no Jogo | Descrição |
|---|---|---|
| **LIKE** | **Disparo Triplo Laser** | Concede 6 segundos de canhão spread laser e regenera escudo |
| **COMMENT** | **Invocação de Invasor** | Spawna inimigo personalizado com o nome do espectador |
| **GIFT** | **Bomba EMP de Área** | Limpa todos os tiros inimigos da tela e causa dano em área |
| **SPECIAL_GIFT** | **Invocação do Chefe Titã** | Invoca imediatamente o titã espacial **DARKZ GOLIATH** |
