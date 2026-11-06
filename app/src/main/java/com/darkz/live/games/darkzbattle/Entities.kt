package com.darkz.live.games.darkzbattle

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
) {
    fun getBounds(): Pair<Offset, Offset> {
        return Pair(
            Offset(x - width / 2, y - height / 2),
            Offset(x + width / 2, y + height / 2)
        )
    }
}

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
)

data class FloatingNotice(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    var x: Float,
    var y: Float,
    val color: Color,
    var alpha: Float = 1f,
    var lifeMs: Long = 1800L
)
