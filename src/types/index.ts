export type ScreenType = 'dashboard' | 'game' | 'game_select' | 'events' | 'settings';

export type LiveEventType = 'LIKE' | 'COMMENT' | 'GIFT' | 'SPECIAL_GIFT';

export interface LiveEvent {
  id: string;
  type: LiveEventType;
  senderName: string;
  senderAvatar?: string;
  message?: string;
  giftName?: string;
  giftValue?: number;
  timestamp: number;
}

export interface EventStats {
  totalLikes: number;
  totalComments: number;
  totalGifts: number;
  totalSpecialGifts: number;
  isConnected: boolean;
  activeViewers: number;
}

export interface UserPreferences {
  soundVolume: number;
  musicVolume: number;
  vibrationEnabled: boolean;
  controlSensitivity: number;
  highFpsMode: boolean;
  streamerTag: string;
  selectedGameId: string;
  autoFire: boolean;
  livePlatform: string;
}
