package com.darkz.live.events

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
            LiveEventType.COMMENT -> "$senderName comentou: \"$message\""
            LiveEventType.GIFT -> "$senderName enviou $giftName ($giftValue) -> BOMBA DE ÁREA!"
            LiveEventType.SPECIAL_GIFT -> "ALERTA: $senderName enviou $giftName -> INVOCANDO CHEFE!"
        }
    }
}
