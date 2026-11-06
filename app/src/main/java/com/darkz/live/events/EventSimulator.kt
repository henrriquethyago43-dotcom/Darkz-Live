package com.darkz.live.events

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

    fun triggerLike(customUser: String? = null) {
        val user = customUser ?: names.random()
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.LIKE,
                senderName = user
            )
        )
    }

    fun triggerComment(customUser: String? = null, customMsg: String? = null) {
        val user = customUser ?: names.random()
        val msg = customMsg ?: comments.random()
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.COMMENT,
                senderName = user,
                message = msg
            )
        )
    }

    fun triggerGift(customGiftName: String? = null, customValue: Int? = null) {
        val user = names.random()
        val gift = if (customGiftName != null && customValue != null) {
            Pair(customGiftName, customValue)
        } else {
            gifts.random()
        }
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.GIFT,
                senderName = user,
                giftName = gift.first,
                giftValue = gift.second
            )
        )
    }

    fun triggerSpecialGift(customGiftName: String? = null) {
        val user = names.random()
        val gift = if (customGiftName != null) {
            Pair(customGiftName, 1000)
        } else {
            specialGifts.random()
        }
        eventManager.postEvent(
            LiveEvent(
                type = LiveEventType.SPECIAL_GIFT,
                senderName = user,
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
}
