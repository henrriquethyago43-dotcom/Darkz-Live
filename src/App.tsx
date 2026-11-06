import React, { useState, useEffect } from 'react';
import { AndroidFrame } from './components/AndroidFrame';
import { GameCanvas } from './components/GameCanvas';
import { CodeExplorer } from './components/CodeExplorer';
import { ProjectExporter } from './components/ProjectExporter';
import { ScreenType, LiveEvent, EventStats, UserPreferences } from './types';
import {
  Gamepad2,
  Code2,
  Download,
  Video,
  Rocket,
  Zap,
  Settings,
  Wifi,
  Heart,
  MessageSquare,
  Gift,
  ArrowLeft,
  Volume2,
  Vibrate,
  Cpu,
  Trash2,
  CheckCircle,
  PlayCircle,
  StopCircle,
  ShieldAlert,
  Info
} from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState<'simulator' | 'code' | 'export'>('simulator');
  const [currentScreen, setCurrentScreen] = useState<ScreenType>('dashboard');

  // Live event system state (StateFlow & SharedFlow replica)
  const [stats, setStats] = useState<EventStats>({
    totalLikes: 142,
    totalComments: 38,
    totalGifts: 12,
    totalSpecialGifts: 2,
    isConnected: true,
    activeViewers: 1480
  });

  const [recentEvents, setRecentEvents] = useState<LiveEvent[]>([
    {
      id: '1',
      type: 'LIKE',
      senderName: 'DarkzSniper',
      timestamp: Date.now() - 12000
    },
    {
      id: '2',
      type: 'COMMENT',
      senderName: 'Valkyrie99',
      message: 'Manda bala no chefe!',
      timestamp: Date.now() - 28000
    },
    {
      id: '3',
      type: 'GIFT',
      senderName: 'NeonShadow',
      giftName: 'Míssil Darkz',
      giftValue: 100,
      timestamp: Date.now() - 45000
    }
  ]);

  const [lastLiveEvent, setLastLiveEvent] = useState<LiveEvent | null>(null);
  const [isAutoSimActive, setIsAutoSimActive] = useState<boolean>(false);

  // User Settings state
  const [preferences, setPreferences] = useState<UserPreferences>(() => {
    const saved = localStorage.getItem('darkz_live_prefs');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        // fallback
      }
    }
    return {
      soundVolume: 0.8,
      musicVolume: 0.6,
      vibrationEnabled: true,
      controlSensitivity: 1.0,
      highFpsMode: true,
      streamerTag: '@darkz_official',
      selectedGameId: 'darkz_battle',
      autoFire: true,
      livePlatform: 'SIMULATOR'
    };
  });

  // Save preferences locally
  useEffect(() => {
    localStorage.setItem('darkz_live_prefs', JSON.stringify(preferences));
  }, [preferences]);

  // Handle Event Triggers (LIKE, COMMENT, GIFT, SPECIAL_GIFT)
  const triggerLiveEvent = (type: 'LIKE' | 'COMMENT' | 'GIFT' | 'SPECIAL_GIFT') => {
    const names = [
      'DarkzSniper', 'Valkyrie99', 'NeonShadow', 'CyberGhost',
      'KryptonBR', 'AlphaLeader', 'RedNova', 'SpectreLive',
      'PixelLord', 'VoidWalker', 'TitanPilot'
    ];
    const comments = [
      'DARKZ LIVE insano!', 'Desvia dos lasers!', 'Invoca o titã agora!',
      'Manda bala!', 'Combo supremo!', 'Olha a nave-mãe!'
    ];
    const gifts = [
      { name: 'Foguete Neon', val: 50 },
      { name: 'Míssil Darkz', val: 100 },
      { name: 'Canhão Triplo', val: 250 },
      { name: 'Bomba EMP', val: 500 }
    ];
    const specialGifts = [
      { name: 'TITÃ CYBER GOLIATH', val: 1000 },
      { name: 'NAVE-MÃE APOCALIPSE', val: 2000 }
    ];

    const sender = names[Math.floor(Math.random() * names.length)];
    let newEvent: LiveEvent;

    if (type === 'LIKE') {
      newEvent = {
        id: Math.random().toString(),
        type: 'LIKE',
        senderName: sender,
        timestamp: Date.now()
      };
      setStats(prev => ({ ...prev, totalLikes: prev.totalLikes + 1 }));
    } else if (type === 'COMMENT') {
      newEvent = {
        id: Math.random().toString(),
        type: 'COMMENT',
        senderName: sender,
        message: comments[Math.floor(Math.random() * comments.length)],
        timestamp: Date.now()
      };
      setStats(prev => ({ ...prev, totalComments: prev.totalComments + 1 }));
    } else if (type === 'GIFT') {
      const g = gifts[Math.floor(Math.random() * gifts.length)];
      newEvent = {
        id: Math.random().toString(),
        type: 'GIFT',
        senderName: sender,
        giftName: g.name,
        giftValue: g.val,
        timestamp: Date.now()
      };
      setStats(prev => ({ ...prev, totalGifts: prev.totalGifts + 1 }));
    } else {
      const sg = specialGifts[Math.floor(Math.random() * specialGifts.length)];
      newEvent = {
        id: Math.random().toString(),
        type: 'SPECIAL_GIFT',
        senderName: sender,
        giftName: sg.name,
        giftValue: sg.val,
        timestamp: Date.now()
      };
      setStats(prev => ({ ...prev, totalSpecialGifts: prev.totalSpecialGifts + 1 }));
    }

    setRecentEvents(prev => [newEvent, ...prev.slice(0, 35)]);
    setLastLiveEvent(newEvent);
  };

  // Auto-Simulator Interval
  useEffect(() => {
    if (!isAutoSimActive) return;
    const interval = setInterval(() => {
      const rand = Math.random();
      if (rand < 0.5) triggerLiveEvent('LIKE');
      else if (rand < 0.8) triggerLiveEvent('COMMENT');
      else if (rand < 0.95) triggerLiveEvent('GIFT');
      else triggerLiveEvent('SPECIAL_GIFT');
    }, 2400);

    return () => clearInterval(interval);
  }, [isAutoSimActive]);

  return (
    <div className="min-h-screen bg-[#050507] text-white flex flex-col font-sans selection:bg-[#E50914] selection:text-white">
      {/* Top Application Bar */}
      <header className="border-b border-[#1A1A24] bg-[#0A0A0F]/90 backdrop-blur sticky top-0 z-50 px-4 py-3">
        <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-4">
          {/* Logo & Platform Info */}
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#E50914] to-[#800000] p-0.5 flex items-center justify-center shadow-[0_0_15px_rgba(229,9,20,0.5)]">
              <Rocket className="text-white" size={22} />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="font-mono font-black text-lg tracking-wider text-white">DARKZ</span>
                <span className="font-mono font-black text-lg tracking-wider text-[#FF1A2A]">LIVE</span>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-[#1C1C28] text-[#00F0FF] border border-[#00F0FF]/30 font-bold ml-1">
                  ANDROID NATIVO
                </span>
              </div>
              <p className="text-xs text-gray-400 font-mono">
                Kotlin • Jetpack Compose • Android Canvas • MinSdk 26
              </p>
            </div>
          </div>

          {/* Navigation Mode Tabs */}
          <div className="flex items-center gap-1 bg-[#12121A] p-1 rounded-xl border border-[#2A2A38]">
            <button
              onClick={() => setActiveTab('simulator')}
              className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-mono font-bold transition-all ${
                activeTab === 'simulator'
                  ? 'bg-[#E50914] text-white shadow-[0_0_10px_rgba(229,9,20,0.5)]'
                  : 'text-gray-400 hover:text-white hover:bg-[#1C1C28]'
              }`}
            >
              <Gamepad2 size={15} />
              <span>SIMULADOR ANDROID</span>
            </button>

            <button
              onClick={() => setActiveTab('code')}
              className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-mono font-bold transition-all ${
                activeTab === 'code'
                  ? 'bg-[#E50914] text-white shadow-[0_0_10px_rgba(229,9,20,0.5)]'
                  : 'text-gray-400 hover:text-white hover:bg-[#1C1C28]'
              }`}
            >
              <Code2 size={15} />
              <span>CÓDIGO-FONTE KOTLIN</span>
            </button>

            <button
              onClick={() => setActiveTab('export')}
              className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-mono font-bold transition-all ${
                activeTab === 'export'
                  ? 'bg-[#E50914] text-white shadow-[0_0_10px_rgba(229,9,20,0.5)]'
                  : 'text-gray-400 hover:text-white hover:bg-[#1C1C28]'
              }`}
            >
              <Download size={15} />
              <span>EXPORTAR (.ZIP)</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 p-4 md:p-6 flex flex-col justify-center">
        {activeTab === 'simulator' && (
          <div className="flex flex-col lg:flex-row items-center lg:items-start justify-center gap-8 max-w-6xl mx-auto w-full">
            {/* The Android Device Mockup */}
            <AndroidFrame>
              {/* Screen Router */}
              {currentScreen === 'dashboard' && (
                <div className="flex-1 flex flex-col bg-[#050507] text-white overflow-y-auto">
                  {/* Dashboard Header */}
                  <div className="p-4 border-b border-[#1A1A24] bg-[#0A0A0F]">
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="flex items-center gap-1 font-mono font-black text-xl tracking-wider">
                          <span>DARKZ</span>
                          <span className="text-[#FF1A2A]">LIVE</span>
                        </div>
                        <div className="text-[10px] font-mono text-gray-400">
                          PLATAFORMA STREAM // {preferences.streamerTag}
                        </div>
                      </div>

                      {/* Connection Indicator */}
                      <div className="flex items-center gap-1.5 px-2.5 py-1 bg-[#12121A] border border-[#00FF66]/40 rounded-full">
                        <div className="w-2 h-2 rounded-full bg-[#00FF66] animate-pulse" />
                        <span className="text-[10px] font-mono font-bold text-[#00FF66]">CONECTADO</span>
                      </div>
                    </div>
                  </div>

                  <div className="p-4 space-y-4 flex-1">
                    {/* System Status Card */}
                    <div className="bg-[#0F0F16] border border-[#2A2A38] rounded-xl p-3.5 shadow-lg">
                      <div className="flex items-center justify-between mb-3 text-[11px] font-mono font-bold">
                        <span className="text-[#FF1A2A] tracking-wider">STATUS DO SISTEMA</span>
                        <span className="text-gray-400 flex items-center gap-1">
                          <Wifi size={12} className="text-[#00FF66]" /> SIMULADOR ONLINE
                        </span>
                      </div>

                      {/* Metrics 4-Grid */}
                      <div className="grid grid-cols-4 gap-1 text-center">
                        <div className="p-1.5 bg-[#161622] rounded-lg">
                          <div className="text-sm font-mono font-black text-[#FF1A2A]">{stats.totalLikes}</div>
                          <div className="text-[9px] font-mono text-gray-400">LIKES</div>
                        </div>
                        <div className="p-1.5 bg-[#161622] rounded-lg">
                          <div className="text-sm font-mono font-black text-[#00F0FF]">{stats.totalComments}</div>
                          <div className="text-[9px] font-mono text-gray-400">CHATS</div>
                        </div>
                        <div className="p-1.5 bg-[#161622] rounded-lg">
                          <div className="text-sm font-mono font-black text-[#FFB800]">{stats.totalGifts}</div>
                          <div className="text-[9px] font-mono text-gray-400">GIFTS</div>
                        </div>
                        <div className="p-1.5 bg-[#161622] rounded-lg">
                          <div className="text-sm font-mono font-black text-[#E50914]">{stats.totalSpecialGifts}</div>
                          <div className="text-[9px] font-mono text-gray-400">CHEFES</div>
                        </div>
                      </div>
                    </div>

                    {/* Navigation Buttons Required in Spec */}
                    <div className="space-y-2.5">
                      {/* INICIAR LIVE */}
                      <button
                        onClick={() => setCurrentScreen('game')}
                        className="w-full py-3.5 px-4 bg-gradient-to-r from-[#E50914] to-[#990008] hover:brightness-110 active:scale-98 rounded-lg font-mono font-black text-xs tracking-wider border border-white/40 flex items-center justify-center gap-2 shadow-[0_0_15px_rgba(229,9,20,0.4)] transition-all"
                      >
                        <Video size={16} />
                        <span>INICIAR LIVE</span>
                      </button>

                      {/* ESCOLHER JOGO */}
                      <button
                        onClick={() => setCurrentScreen('game_select')}
                        className="w-full py-3 px-4 bg-[#14141E] hover:bg-[#1A1A28] border border-[#00F0FF]/40 rounded-lg font-mono font-bold text-xs tracking-wider text-[#00F0FF] flex items-center justify-center gap-2 transition-all"
                      >
                        <Rocket size={15} />
                        <span>ESCOLHER JOGO</span>
                      </button>

                      {/* CENTRAL DE EVENTOS */}
                      <button
                        onClick={() => setCurrentScreen('events')}
                        className="w-full py-3 px-4 bg-[#14141E] hover:bg-[#1A1A28] border border-[#FFB800]/40 rounded-lg font-mono font-bold text-xs tracking-wider text-[#FFB800] flex items-center justify-center gap-2 transition-all"
                      >
                        <Zap size={15} />
                        <span>CENTRAL DE EVENTOS</span>
                      </button>

                      {/* CONFIGURAÇÕES */}
                      <button
                        onClick={() => setCurrentScreen('settings')}
                        className="w-full py-3 px-4 bg-[#14141E] hover:bg-[#1A1A28] border border-[#2A2A38] rounded-lg font-mono font-bold text-xs tracking-wider text-gray-200 flex items-center justify-center gap-2 transition-all"
                      >
                        <Settings size={15} />
                        <span>CONFIGURAÇÕES</span>
                      </button>
                    </div>

                    {/* Última Atividade */}
                    <div className="pt-2">
                      <div className="flex items-center justify-between text-[11px] font-mono font-bold text-gray-300 mb-2">
                        <span>ÚLTIMA ATIVIDADE (LIVE FEED)</span>
                        <span className="text-[10px] text-gray-500">{recentEvents.length} EVENTOS</span>
                      </div>

                      <div className="space-y-1.5 max-h-[160px] overflow-y-auto pr-1">
                        {recentEvents.slice(0, 5).map((ev) => (
                          <div
                            key={ev.id}
                            className="p-2 bg-[#0F0F16] border border-[#1E1E2C] rounded-lg flex items-center gap-2 text-xs"
                          >
                            <span
                              className={`text-[9px] font-mono font-bold px-1.5 py-0.5 rounded ${
                                ev.type === 'LIKE'
                                  ? 'bg-[#FF1A2A]/20 text-[#FF1A2A] border border-[#FF1A2A]/40'
                                  : ev.type === 'COMMENT'
                                  ? 'bg-[#00F0FF]/20 text-[#00F0FF] border border-[#00F0FF]/40'
                                  : ev.type === 'GIFT'
                                  ? 'bg-[#FFB800]/20 text-[#FFB800] border border-[#FFB800]/40'
                                  : 'bg-[#E50914]/20 text-[#E50914] border border-[#E50914]'
                              }`}
                            >
                              {ev.type}
                            </span>
                            <div className="truncate flex-1">
                              <span className="font-bold text-white text-[11px]">{ev.senderName}: </span>
                              <span className="text-gray-400 text-[10px]">
                                {ev.type === 'LIKE' && '+Disparo Laser Spread'}
                                {ev.type === 'COMMENT' && `"${ev.message}"`}
                                {ev.type === 'GIFT' && `Enviou ${ev.giftName} (EMP Blast)`}
                                {ev.type === 'SPECIAL_GIFT' && `Invocou Chefe ${ev.giftName}!`}
                              </span>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* Game Screen with Canvas */}
              {currentScreen === 'game' && (
                <GameCanvas
                  onBack={() => setCurrentScreen('dashboard')}
                  onTriggerEvent={triggerLiveEvent}
                  lastLiveEvent={lastLiveEvent}
                  soundVolume={preferences.soundVolume}
                />
              )}

              {/* Game Select Screen */}
              {currentScreen === 'game_select' && (
                <div className="flex-1 flex flex-col bg-[#050507] text-white p-4 overflow-y-auto">
                  <div className="flex items-center gap-2 mb-4 pb-2 border-b border-[#1A1A24]">
                    <button
                      onClick={() => setCurrentScreen('dashboard')}
                      className="p-1 rounded bg-[#12121A] text-gray-400 hover:text-white"
                    >
                      <ArrowLeft size={16} />
                    </button>
                    <div>
                      <h2 className="text-sm font-mono font-bold text-white">ESCOLHER JOGO</h2>
                      <p className="text-[10px] font-mono text-[#00F0FF]">ARQUITETURA MODULAR</p>
                    </div>
                  </div>

                  <div className="space-y-3 flex-1">
                    {/* Active Game */}
                    <div
                      onClick={() => setCurrentScreen('game')}
                      className="p-3 bg-[#0F0F16] border-2 border-[#E50914] rounded-xl cursor-pointer hover:bg-[#161622] transition-colors"
                    >
                      <div className="flex items-center justify-between mb-1">
                        <span className="font-mono font-bold text-xs text-white">DARKZ BATTLE</span>
                        <span className="text-[9px] font-mono font-bold px-1.5 py-0.5 rounded bg-[#00FF66]/20 text-[#00FF66] border border-[#00FF66]/40">
                          DISPONÍVEL AGORA
                        </span>
                      </div>
                      <p className="text-[11px] text-gray-400">
                        Space shooter interativo com suporte a eventos de curtidas, mensagens e presentes da live.
                      </p>
                    </div>

                    {/* Future Game 1 */}
                    <div className="p-3 bg-[#0C0C12] border border-[#222230] rounded-xl opacity-70">
                      <div className="flex items-center justify-between mb-1">
                        <span className="font-mono font-bold text-xs text-gray-300">DARKZ CYBER RACE</span>
                        <span className="text-[9px] font-mono font-bold px-1.5 py-0.5 rounded bg-[#FFB800]/20 text-[#FFB800]">
                          FASE 2
                        </span>
                      </div>
                      <p className="text-[11px] text-gray-500">
                        Corrida arcade futurista. Os espectadores controlam armadilhas na pista e boosters de nitro.
                      </p>
                    </div>

                    {/* Future Game 2 */}
                    <div className="p-3 bg-[#0C0C12] border border-[#222230] rounded-xl opacity-70">
                      <div className="flex items-center justify-between mb-1">
                        <span className="font-mono font-bold text-xs text-gray-300">DARKZ TITAN DEFENSE</span>
                        <span className="text-[9px] font-mono font-bold px-1.5 py-0.5 rounded bg-[#00F0FF]/20 text-[#00F0FF]">
                          FASE 2
                        </span>
                      </div>
                      <p className="text-[11px] text-gray-500">
                        Defesa orbital de base espacial com envio de tropas e escudos pela transmissão ao vivo.
                      </p>
                    </div>
                  </div>

                  <button
                    onClick={() => setCurrentScreen('game')}
                    className="w-full mt-4 py-2.5 bg-[#E50914] hover:bg-[#B8000A] text-white font-mono font-bold text-xs rounded-lg"
                  >
                    JOGAR DARKZ BATTLE
                  </button>
                </div>
              )}

              {/* Event Hub Screen */}
              {currentScreen === 'events' && (
                <div className="flex-1 flex flex-col bg-[#050507] text-white p-4 overflow-y-auto">
                  <div className="flex items-center gap-2 mb-4 pb-2 border-b border-[#1A1A24]">
                    <button
                      onClick={() => setCurrentScreen('dashboard')}
                      className="p-1 rounded bg-[#12121A] text-gray-400 hover:text-white"
                    >
                      <ArrowLeft size={16} />
                    </button>
                    <div>
                      <h2 className="text-sm font-mono font-bold text-white">CENTRAL DE EVENTOS</h2>
                      <p className="text-[10px] font-mono text-[#FFB800]">SIMULADOR & INTEGRAÇÃO</p>
                    </div>
                  </div>

                  <div className="space-y-4">
                    {/* Simulator Action Triggers */}
                    <div className="bg-[#0F0F16] border border-[#2A2A38] rounded-xl p-3 space-y-2">
                      <div className="text-[11px] font-mono font-bold text-gray-300 mb-1">
                        DISPARAR EVENTOS MANUAIS
                      </div>

                      <button
                        onClick={() => triggerLiveEvent('LIKE')}
                        className="w-full p-2 bg-[#FF1A2A]/15 border border-[#FF1A2A]/40 rounded-lg flex items-center justify-between text-xs hover:bg-[#FF1A2A]/25 transition-colors"
                      >
                        <span className="font-mono font-bold text-[#FF1A2A]">1. LIKE</span>
                        <span className="text-[10px] text-gray-300">Disparo Especial Laser</span>
                      </button>

                      <button
                        onClick={() => triggerLiveEvent('COMMENT')}
                        className="w-full p-2 bg-[#00F0FF]/15 border border-[#00F0FF]/40 rounded-lg flex items-center justify-between text-xs hover:bg-[#00F0FF]/25 transition-colors"
                      >
                        <span className="font-mono font-bold text-[#00F0FF]">2. COMENTÁRIO</span>
                        <span className="text-[10px] text-gray-300">Invocar Inimigo Custom</span>
                      </button>

                      <button
                        onClick={() => triggerLiveEvent('GIFT')}
                        className="w-full p-2 bg-[#FFB800]/15 border border-[#FFB800]/40 rounded-lg flex items-center justify-between text-xs hover:bg-[#FFB800]/25 transition-colors"
                      >
                        <span className="font-mono font-bold text-[#FFB800]">3. PRESENTE (GIFT)</span>
                        <span className="text-[10px] text-gray-300">Bomba de Choque EMP</span>
                      </button>

                      <button
                        onClick={() => triggerLiveEvent('SPECIAL_GIFT')}
                        className="w-full p-2 bg-[#E50914]/20 border border-[#E50914] rounded-lg flex items-center justify-between text-xs hover:bg-[#E50914]/35 transition-colors"
                      >
                        <span className="font-mono font-bold text-[#E50914]">4. SUPER PRESENTE</span>
                        <span className="text-[10px] text-gray-300">Invocar Chefe Titã</span>
                      </button>
                    </div>

                    {/* Auto Simulator Toggle */}
                    <div className="bg-[#12121A] border border-[#2A2A38] rounded-xl p-3 flex items-center justify-between">
                      <div>
                        <div className="text-xs font-mono font-bold text-white">SIMULAÇÃO AUTOMÁTICA</div>
                        <div className="text-[10px] text-gray-400">Gera tráfego contínuo a cada ~2.4s</div>
                      </div>
                      <button
                        onClick={() => setIsAutoSimActive(!isAutoSimActive)}
                        className={`px-3 py-1.5 rounded-lg text-xs font-mono font-bold transition-all ${
                          isAutoSimActive
                            ? 'bg-[#00FF66] text-black shadow-[0_0_10px_#00FF66]'
                            : 'bg-[#222230] text-gray-400'
                        }`}
                      >
                        {isAutoSimActive ? 'ATIVO' : 'LIGAR'}
                      </button>
                    </div>

                    {/* Events History */}
                    <div>
                      <div className="flex items-center justify-between text-xs font-mono font-bold text-gray-300 mb-2">
                        <span>HISTÓRICO DE EVENTOS</span>
                        <button
                          onClick={() => setRecentEvents([])}
                          className="text-[10px] text-gray-500 hover:text-red-400"
                        >
                          Limpar
                        </button>
                      </div>
                      <div className="space-y-1.5 max-h-[140px] overflow-y-auto">
                        {recentEvents.map((ev) => (
                          <div
                            key={ev.id}
                            className="p-1.5 bg-[#0F0F16] border border-[#1A1A24] rounded text-[11px] flex justify-between"
                          >
                            <span className="text-gray-300 font-bold">{ev.senderName}</span>
                            <span className="text-gray-400 font-mono text-[10px]">{ev.type}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* Settings Screen */}
              {currentScreen === 'settings' && (
                <div className="flex-1 flex flex-col bg-[#050507] text-white p-4 overflow-y-auto">
                  <div className="flex items-center gap-2 mb-4 pb-2 border-b border-[#1A1A24]">
                    <button
                      onClick={() => setCurrentScreen('dashboard')}
                      className="p-1 rounded bg-[#12121A] text-gray-400 hover:text-white"
                    >
                      <ArrowLeft size={16} />
                    </button>
                    <div>
                      <h2 className="text-sm font-mono font-bold text-white">CONFIGURAÇÕES</h2>
                      <p className="text-[10px] font-mono text-[#00F0FF]">SISTEMA & PREFERÊNCIAS</p>
                    </div>
                  </div>

                  <div className="space-y-4 text-xs font-mono">
                    {/* Audio */}
                    <div className="bg-[#0F0F16] border border-[#2A2A38] rounded-xl p-3 space-y-3">
                      <div className="font-bold text-gray-300 flex items-center gap-1.5">
                        <Volume2 size={14} className="text-[#FF1A2A]" /> ÁUDIO & EFEITOS
                      </div>

                      <div>
                        <div className="flex justify-between text-[11px] text-gray-400 mb-1">
                          <span>Efeitos Sonoros</span>
                          <span className="text-[#00F0FF]">{Math.round(preferences.soundVolume * 100)}%</span>
                        </div>
                        <input
                          type="range"
                          min="0"
                          max="1"
                          step="0.05"
                          value={preferences.soundVolume}
                          onChange={(e) =>
                            setPreferences(p => ({ ...p, soundVolume: parseFloat(e.target.value) }))
                          }
                          className="w-full accent-[#E50914]"
                        />
                      </div>
                    </div>

                    {/* Gameplay & Tactile */}
                    <div className="bg-[#0F0F16] border border-[#2A2A38] rounded-xl p-3 space-y-3">
                      <div className="font-bold text-gray-300 flex items-center gap-1.5">
                        <Vibrate size={14} className="text-[#00F0FF]" /> JOGABILIDADE
                      </div>

                      <div className="flex items-center justify-between">
                        <div>
                          <div className="text-white text-[11px]">Feedback Tátil (Vibração)</div>
                          <div className="text-[10px] text-gray-500">Vibra ao receber dano/especial</div>
                        </div>
                        <input
                          type="checkbox"
                          checked={preferences.vibrationEnabled}
                          onChange={(e) =>
                            setPreferences(p => ({ ...p, vibrationEnabled: e.target.checked }))
                          }
                          className="w-4 h-4 accent-[#E50914]"
                        />
                      </div>

                      <div className="flex items-center justify-between">
                        <div>
                          <div className="text-white text-[11px]">Modo Alto Desempenho (60 FPS)</div>
                          <div className="text-[10px] text-gray-500">Taxa máxima de quadros</div>
                        </div>
                        <input
                          type="checkbox"
                          checked={preferences.highFpsMode}
                          onChange={(e) =>
                            setPreferences(p => ({ ...p, highFpsMode: e.target.checked }))
                          }
                          className="w-4 h-4 accent-[#00FF66]"
                        />
                      </div>
                    </div>

                    {/* Spec Info */}
                    <div className="bg-[#12121A] border border-[#222230] rounded-xl p-3 text-[10px] text-gray-400 space-y-1">
                      <div className="font-bold text-[#00F0FF]">DARKZ LIVE // SISTEMA NATIVO</div>
                      <div>• Pacote: com.darkz.live</div>
                      <div>• MinSdk: 26 | TargetSdk: 34</div>
                      <div>• 100% Offline e local (sem login)</div>
                      <div>• Sem anúncios ou telemetria</div>
                    </div>
                  </div>
                </div>
              )}
            </AndroidFrame>

            {/* Quick Testing Station (Outside the Phone Frame) */}
            <div className="w-full lg:w-80 space-y-4">
              <div className="bg-[#0A0A0F] border border-[#2A2A38] rounded-2xl p-5 shadow-xl">
                <div className="flex items-center gap-2 mb-3 text-xs font-mono font-bold text-[#FF1A2A]">
                  <Zap size={16} /> BANCADA DE TESTE DO STREAMER
                </div>
                <p className="text-xs text-gray-400 mb-4">
                  Dispare os eventos ao vivo diretamente aqui para ver a reação imediata do jogo no celular:
                </p>

                <div className="space-y-2">
                  <button
                    onClick={() => triggerLiveEvent('LIKE')}
                    className="w-full py-2.5 px-3 bg-[#FF1A2A]/10 hover:bg-[#FF1A2A]/20 border border-[#FF1A2A]/40 rounded-lg text-left text-xs font-mono flex items-center justify-between transition-colors"
                  >
                    <span className="font-bold text-[#FF1A2A]">👍 DISPARAR LIKE</span>
                    <span className="text-[10px] text-gray-300">Canhão Triplo</span>
                  </button>

                  <button
                    onClick={() => triggerLiveEvent('COMMENT')}
                    className="w-full py-2.5 px-3 bg-[#00F0FF]/10 hover:bg-[#00F0FF]/20 border border-[#00F0FF]/40 rounded-lg text-left text-xs font-mono flex items-center justify-between transition-colors"
                  >
                    <span className="font-bold text-[#00F0FF]">💬 DISPARAR CHAT</span>
                    <span className="text-[10px] text-gray-300">Inimigo Custom</span>
                  </button>

                  <button
                    onClick={() => triggerLiveEvent('GIFT')}
                    className="w-full py-2.5 px-3 bg-[#FFB800]/10 hover:bg-[#FFB800]/20 border border-[#FFB800]/40 rounded-lg text-left text-xs font-mono flex items-center justify-between transition-colors"
                  >
                    <span className="font-bold text-[#FFB800]">🎁 DISPARAR GIFT</span>
                    <span className="text-[10px] text-gray-300">Bomba de Choque EMP</span>
                  </button>

                  <button
                    onClick={() => triggerLiveEvent('SPECIAL_GIFT')}
                    className="w-full py-2.5 px-3 bg-[#E50914]/20 hover:bg-[#E50914]/30 border border-[#E50914] rounded-lg text-left text-xs font-mono flex items-center justify-between transition-colors"
                  >
                    <span className="font-bold text-[#E50914]">👑 SUPER GIFT</span>
                    <span className="text-[10px] text-gray-300">Invocar Chefe Titã</span>
                  </button>
                </div>

                <div className="mt-4 pt-4 border-t border-[#1C1C28] flex items-center justify-between text-xs font-mono">
                  <span className="text-gray-400">Tráfego Automático:</span>
                  <button
                    onClick={() => setIsAutoSimActive(!isAutoSimActive)}
                    className={`px-3 py-1 rounded text-[11px] font-bold ${
                      isAutoSimActive ? 'bg-[#00FF66] text-black' : 'bg-[#1C1C28] text-gray-300'
                    }`}
                  >
                    {isAutoSimActive ? 'RODANDO' : 'DESLIGADO'}
                  </button>
                </div>
              </div>

              {/* Native Architecture summary */}
              <div className="bg-[#0A0A0F] border border-[#2A2A38] rounded-2xl p-5 shadow-xl text-xs font-mono text-gray-400 space-y-2">
                <div className="font-bold text-white flex items-center gap-2">
                  <Cpu size={15} className="text-[#00F0FF]" /> ARQUITETURA ENTREGUE
                </div>
                <div>• Jetpack Compose + Compose BOM 2024.10</div>
                <div>• Coroutine SharedFlow para eventos</div>
                <div>• ViewModel + StateFlow para estados</div>
                <div>• Canvas com física e detecção de colisão</div>
                <div>• Código Kotlin modularizado (ui, games, events, live, data, settings)</div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'code' && <CodeExplorer />}

        {activeTab === 'export' && <ProjectExporter />}
      </main>
    </div>
  );
}
