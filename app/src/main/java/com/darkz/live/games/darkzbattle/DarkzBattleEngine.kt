package com.darkz.live.games.darkzbattle

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.darkz.live.events.LiveEvent
import com.darkz.live.events.LiveEventType
import com.darkz.live.games.GameEngine
import com.darkz.live.games.GameMeta
import com.darkz.live.ui.theme.DarkzAmber
import com.darkz.live.ui.theme.DarkzCyanNeon
import com.darkz.live.ui.theme.DarkzGreenLive
import com.darkz.live.ui.theme.DarkzRedNeon
import com.darkz.live.ui.theme.DarkzRedPrimary
import com.darkz.live.ui.theme.DarkzWhite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.hypot
import kotlin.random.Random

class DarkzBattleEngine : GameEngine {

    override val meta: GameMeta = GameMeta(
        id = "darkz_battle",
        title = "DARKZ BATTLE",
        subtitle = "Space Shooter Interativo",
        description = "Batalha espacial frenética onde as curtidas, comentários e presentes da live afetam diretamente o jogo!",
        isAvailable = true,
        iconName = "rocket"
    )

    private val _scoreState = MutableStateFlow(0L)
    override val scoreState: StateFlow<Long> = _scoreState.asStateFlow()

    private val _healthState = MutableStateFlow(100f)
    override val healthState: StateFlow<Float> = _healthState.asStateFlow()

    private val _waveState = MutableStateFlow(1)
    override val waveState: StateFlow<Int> = _waveState.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    override val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    override val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    // World & dimensions
    private var width: Float = 1080f
    private var height: Float = 1920f

    // Entities
    private var player = Spacecraft()
    private val enemies = mutableListOf<Enemy>()
    private var activeBoss: Boss? = null
    private val projectiles = mutableListOf<Projectile>()
    private val particles = mutableListOf<Particle>()
    private val notices = mutableListOf<FloatingNotice>()
    private val stars = mutableListOf<Offset>()

    // Mechanics
    private var fireCooldown = 0L
    private var spawnCooldown = 0L
    private var waveKillCount = 0
    private var comboMultiplier = 1
    private var comboTimer = 0L
    private var screenShake = 0f

    override fun initGame(screenWidth: Float, screenHeight: Float) {
        width = screenWidth.coerceAtLeast(360f)
        height = screenHeight.coerceAtLeast(640f)

        player = Spacecraft(
            x = width / 2f,
            y = height * 0.82f,
            health = 100f,
            shield = 50f
        )

        enemies.clear()
        activeBoss = null
        projectiles.clear()
        particles.clear()
        notices.clear()

        // Generate starfield
        stars.clear()
        for (i in 0 until 60) {
            stars.add(Offset(Random.nextFloat() * width, Random.nextFloat() * height))
        }

        _scoreState.value = 0L
        _healthState.value = 100f
        _waveState.value = 1
        _isGameOver.value = false
        _isPaused.value = false

        waveKillCount = 0
        comboMultiplier = 1
    }

    override fun update(deltaTimeMs: Long) {
        if (_isPaused.value || _isGameOver.value) return

        val dt = (deltaTimeMs.coerceIn(8L, 33L)).toFloat() / 1000f

        // Decay screen shake
        if (screenShake > 0) {
            screenShake = (screenShake - dt * 25f).coerceAtLeast(0f)
        }

        // Update player timers
        if (player.specialLaserTimer > 0) {
            player.specialLaserTimer -= deltaTimeMs
        }
        if (player.invincibleTimer > 0) {
            player.invincibleTimer -= deltaTimeMs
        }

        // Auto firing
        fireCooldown -= deltaTimeMs
        if (fireCooldown <= 0L) {
            shootPlayerWeapon()
            fireCooldown = if (player.specialLaserTimer > 0) 120L else 200L
        }

        // Update starfield
        for (i in stars.indices) {
            val s = stars[i]
            var ny = s.y + (dt * 150f)
            var nx = s.x
            if (ny > height) {
                ny = 0f
                nx = Random.nextFloat() * width
            }
            stars[i] = Offset(nx, ny)
        }

        // Spawn normal waves if no boss
        if (activeBoss == null) {
            spawnCooldown -= deltaTimeMs
            if (spawnCooldown <= 0L && enemies.size < (4 + _waveState.value * 2)) {
                spawnEnemy()
                spawnCooldown = (1200L - (_waveState.value * 80L)).coerceAtLeast(400L)
            }
        }

        // Update Enemies
        val enemyIterator = enemies.iterator()
        while (enemyIterator.hasNext()) {
            val enemy = enemyIterator.next()
            enemy.x += enemy.vx * dt
            enemy.y += enemy.vy * dt

            // Bounce on lateral borders
            if (enemy.x < 30f || enemy.x > width - 30f) {
                enemy.vx = -enemy.vx
            }

            // Enemy firing
            enemy.shootTimer -= deltaTimeMs
            if (enemy.shootTimer <= 0L && enemy.y < height * 0.7f) {
                shootEnemyWeapon(enemy)
                enemy.shootTimer = Random.nextLong(1500L, 3000L)
            }

            // Check if passed screen bottom
            if (enemy.y > height + 50f) {
                enemyIterator.remove()
                // Player takes small penalty for breach
                damagePlayer(5f)
            }
        }

        // Update Boss
        activeBoss?.let { boss ->
            // Move boss smoothly
            boss.x += (boss.targetX - boss.x) * (dt * 2f)
            if (kotlin.math.abs(boss.targetX - boss.x) < 20f) {
                boss.targetX = Random.nextFloat() * (width - boss.width) + (boss.width / 2f)
            }

            boss.attackTimer -= deltaTimeMs
            if (boss.attackTimer <= 0L) {
                executeBossAttack(boss)
                boss.attackTimer = 1800L
            }
        }

        // Update Projectiles
        val projIterator = projectiles.iterator()
        while (projIterator.hasNext()) {
            val p = projIterator.next()
            p.x += p.vx * dt
            p.y += p.vy * dt

            // Remove out of bounds
            if (p.x < -20f || p.x > width + 20f || p.y < -30f || p.y > height + 30f) {
                projIterator.remove()
                continue
            }

            // Player projectile collisions
            if (p.isPlayer) {
                // Check boss collision
                activeBoss?.let { boss ->
                    if (p.x >= boss.x - boss.width / 2 && p.x <= boss.x + boss.width / 2 &&
                        p.y >= boss.y - boss.height / 2 && p.y <= boss.y + boss.height / 2
                    ) {
                        boss.health -= p.damage
                        createSparks(p.x, p.y, DarkzRedNeon, 4)
                        projIterator.remove()

                        if (boss.health <= 0f) {
                            defeatBoss(boss)
                        }
                        return@let
                    }
                }

                // Check enemies collision
                val hitEnemy = enemies.find { e ->
                    hypot(p.x - e.x, p.y - e.y) < (e.width / 2 + p.radius)
                }

                if (hitEnemy != null) {
                    hitEnemy.health -= p.damage
                    createSparks(p.x, p.y, hitEnemy.type.let { DarkzCyanNeon }, 3)
                    projIterator.remove()

                    if (hitEnemy.health <= 0f) {
                        defeatEnemy(hitEnemy)
                    }
                    continue
                }
            } else {
                // Enemy projectile hits player
                if (hypot(p.x - player.x, p.y - player.y) < (player.width / 2 + p.radius)) {
                    damagePlayer(p.damage)
                    createSparks(p.x, p.y, DarkzRedPrimary, 6)
                    projIterator.remove()
                    continue
                }
            }
        }

        // Check player collision with enemies
        for (enemy in enemies) {
            if (hypot(player.x - enemy.x, player.y - enemy.y) < (player.width / 2 + enemy.width / 2)) {
                damagePlayer(25f)
                enemy.health = 0f
                defeatEnemy(enemy)
                break
            }
        }

        // Update Particles
        val partIterator = particles.iterator()
        while (partIterator.hasNext()) {
            val part = partIterator.next()
            part.x += part.vx * dt
            part.y += part.vy * dt
            part.lifeMs -= deltaTimeMs
            part.alpha = (part.lifeMs.toFloat() / part.maxLifeMs.toFloat()).coerceIn(0f, 1f)
            if (part.lifeMs <= 0L) {
                partIterator.remove()
            }
        }

        // Update Floating Notices
        val noticeIterator = notices.iterator()
        while (noticeIterator.hasNext()) {
            val n = noticeIterator.next()
            n.y -= dt * 45f
            n.lifeMs -= deltaTimeMs
            n.alpha = (n.lifeMs.toFloat() / 1800f).coerceIn(0f, 1f)
            if (n.lifeMs <= 0L) {
                noticeIterator.remove()
            }
        }
    }

    override fun render(drawScope: DrawScope) {
        with(drawScope) {
            // Background deep space
            drawRect(
                color = Color(0xFF050507),
                size = size
            )

            // Starfield
            for (s in stars) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = 1.5f,
                    center = s
                )
            }

            // Screen shake translation
            // Render Projectiles
            for (p in projectiles) {
                if (p.isPlayer) {
                    drawCircle(
                        color = p.color,
                        radius = p.radius,
                        center = Offset(p.x, p.y)
                    )
                    // Glow trail
                    drawCircle(
                        color = p.color.copy(alpha = 0.4f),
                        radius = p.radius * 2.2f,
                        center = Offset(p.x, p.y)
                    )
                } else {
                    drawCircle(
                        color = p.color,
                        radius = p.radius,
                        center = Offset(p.x, p.y)
                    )
                }
            }

            // Render Enemies
            for (enemy in enemies) {
                renderEnemy(this, enemy)
            }

            // Render Boss
            activeBoss?.let { boss ->
                renderBoss(this, boss)
            }

            // Render Spacecraft (Player)
            renderPlayer(this, player)

            // Render Particles
            for (part in particles) {
                drawCircle(
                    color = part.color.copy(alpha = part.alpha),
                    radius = part.radius,
                    center = Offset(part.x, part.y)
                )
            }
        }
    }

    private fun renderPlayer(scope: DrawScope, p: Spacecraft) {
        with(scope) {
            val center = Offset(p.x, p.y)
            val halfW = p.width / 2f
            val halfH = p.height / 2f

            // Engine Flame Thrusters
            val flamePath = Path().apply {
                moveTo(p.x - 12f, p.y + halfH)
                lineTo(p.x + 12f, p.y + halfH)
                lineTo(p.x, p.y + halfH + Random.nextFloat() * 20f + 12f)
                close()
            }
            drawPath(flamePath, color = DarkzCyanNeon.copy(alpha = 0.85f))

            // Ship Body - Sleek Delta Wing Fighter
            val shipPath = Path().apply {
                moveTo(p.x, p.y - halfH) // Nose
                lineTo(p.x + halfW, p.y + halfH) // Right wing tip
                lineTo(p.x + 10f, p.y + halfH * 0.6f) // Right thruster notch
                lineTo(p.x, p.y + halfH * 0.4f) // Center notch
                lineTo(p.x - 10f, p.y + halfH * 0.6f) // Left thruster notch
                lineTo(p.x - halfW, p.y + halfH) // Left wing tip
                close()
            }

            // Fill & Outline
            drawPath(shipPath, color = DarkzRedPrimary)
            drawPath(shipPath, color = DarkzWhite, style = Stroke(width = 2.5f))

            // Cockpit Canopy
            drawCircle(
                color = DarkzCyanNeon,
                radius = 7f,
                center = Offset(p.x, p.y - 4f)
            )

            // Shield Aura
            if (p.shield > 0) {
                drawCircle(
                    color = DarkzCyanNeon.copy(alpha = (p.shield / p.maxShield) * 0.35f),
                    radius = p.width * 0.85f,
                    center = center,
                    style = Stroke(width = 2f)
                )
            }

            // Special Laser Indicator
            if (p.specialLaserTimer > 0) {
                drawCircle(
                    color = DarkzRedNeon.copy(alpha = 0.4f),
                    radius = p.width * 1.1f,
                    center = center,
                    style = Stroke(width = 3f)
                )
            }
        }
    }

    private fun renderEnemy(scope: DrawScope, e: Enemy) {
        with(scope) {
            val halfW = e.width / 2f
            val halfH = e.height / 2f

            val enemyColor = when (e.type) {
                EnemyType.DRONE -> DarkzAmber
                EnemyType.INTERCEPTOR -> DarkzRedNeon
                EnemyType.CRUISER -> DarkzRedPrimary
                EnemyType.COMMUNITY_SPONSORED -> DarkzCyanNeon
            }

            val path = Path().apply {
                moveTo(e.x, e.y + halfH) // Front pointing down
                lineTo(e.x + halfW, e.y - halfH)
                lineTo(e.x, e.y - halfH * 0.4f)
                lineTo(e.x - halfW, e.y - halfH)
                close()
            }

            drawPath(path, color = enemyColor)
            drawPath(path, color = DarkzWhite, style = Stroke(width = 1.5f))

            // Health bar over enemy
            val barW = e.width
            val barH = 4f
            val healthRatio = (e.health / e.maxHealth).coerceIn(0f, 1f)

            drawRect(
                color = Color.DarkGray,
                topLeft = Offset(e.x - barW / 2, e.y - halfH - 10f),
                size = Size(barW, barH)
            )
            drawRect(
                color = if (healthRatio > 0.4f) DarkzGreenLive else DarkzRedNeon,
                topLeft = Offset(e.x - barW / 2, e.y - halfH - 10f),
                size = Size(barW * healthRatio, barH)
            )
        }
    }

    private fun renderBoss(scope: DrawScope, boss: Boss) {
        with(scope) {
            val halfW = boss.width / 2f
            val halfH = boss.height / 2f

            // Menacing Boss Structure
            val hull = Path().apply {
                moveTo(boss.x, boss.y + halfH)
                lineTo(boss.x + halfW * 0.5f, boss.y + halfH * 0.7f)
                lineTo(boss.x + halfW, boss.y)
                lineTo(boss.x + halfW * 0.8f, boss.y - halfH)
                lineTo(boss.x, boss.y - halfH * 0.6f)
                lineTo(boss.x - halfW * 0.8f, boss.y - halfH)
                lineTo(boss.x - halfW, boss.y)
                lineTo(boss.x - halfW * 0.5f, boss.y + halfH * 0.7f)
                close()
            }

            drawPath(hull, color = Color(0xFF1F0004))
            drawPath(hull, color = DarkzRedNeon, style = Stroke(width = 3f))

            // Reactor Core
            drawCircle(
                color = if (boss.isChargingLaser) DarkzWhite else DarkzRedNeon,
                radius = 24f,
                center = Offset(boss.x, boss.y)
            )

            // Boss Health Bar on top
            val barW = boss.width * 1.4f
            val barH = 10f
            val hpRatio = (boss.health / boss.maxHealth).coerceIn(0f, 1f)

            drawRect(
                color = Color(0xFF222230),
                topLeft = Offset(boss.x - barW / 2, boss.y - halfH - 25f),
                size = Size(barW, barH)
            )
            drawRect(
                color = DarkzRedNeon,
                topLeft = Offset(boss.x - barW / 2, boss.y - halfH - 25f),
                size = Size(barW * hpRatio, barH)
            )
        }
    }

    override fun onTouchMove(x: Float, y: Float) {
        if (_isPaused.value || _isGameOver.value) return
        player.x = x.coerceIn(40f, width - 40f)
        player.y = y.coerceIn(height * 0.35f, height - 60f)
    }

    override fun onFireButton(pressed: Boolean) {
        if (pressed) {
            shootPlayerWeapon()
        }
    }

    private fun shootPlayerWeapon() {
        val isSpread = player.specialLaserTimer > 0
        if (isSpread) {
            // Triple cannon spread
            projectiles.add(
                Projectile(
                    x = player.x,
                    y = player.y - player.height / 2,
                    vx = 0f,
                    vy = -1200f,
                    isPlayer = true,
                    damage = 40f,
                    radius = 6f,
                    color = DarkzCyanNeon,
                    isSpecialSpread = true
                )
            )
            projectiles.add(
                Projectile(
                    x = player.x - 18f,
                    y = player.y - player.height / 2,
                    vx = -250f,
                    vy = -1150f,
                    isPlayer = true,
                    damage = 35f,
                    radius = 5f,
                    color = DarkzRedNeon,
                    isSpecialSpread = true
                )
            )
            projectiles.add(
                Projectile(
                    x = player.x + 18f,
                    y = player.y - player.height / 2,
                    vx = 250f,
                    vy = -1150f,
                    isPlayer = true,
                    damage = 35f,
                    radius = 5f,
                    color = DarkzRedNeon,
                    isSpecialSpread = true
                )
            )
        } else {
            // Standard dual plasma blaster
            projectiles.add(
                Projectile(
                    x = player.x - 14f,
                    y = player.y - player.height / 2,
                    vx = 0f,
                    vy = -950f,
                    isPlayer = true,
                    damage = 30f,
                    radius = 4.5f,
                    color = DarkzCyanNeon
                )
            )
            projectiles.add(
                Projectile(
                    x = player.x + 14f,
                    y = player.y - player.height / 2,
                    vx = 0f,
                    vy = -950f,
                    isPlayer = true,
                    damage = 30f,
                    radius = 4.5f,
                    color = DarkzCyanNeon
                )
            )
        }
    }

    private fun shootEnemyWeapon(e: Enemy) {
        projectiles.add(
            Projectile(
                x = e.x,
                y = e.y + e.height / 2,
                vx = 0f,
                vy = 450f,
                isPlayer = false,
                damage = 12f,
                radius = 5f,
                color = DarkzAmber
            )
        )
    }

    private fun executeBossAttack(boss: Boss) {
        // Multi-angle barrage
        for (angle in listOf(-300f, -150f, 0f, 150f, 300f)) {
            projectiles.add(
                Projectile(
                    x = boss.x,
                    y = boss.y + boss.height / 2,
                    vx = angle,
                    vy = 520f,
                    isPlayer = false,
                    damage = 18f,
                    radius = 6f,
                    color = DarkzRedNeon
                )
            )
        }
    }

    private fun spawnEnemy(forcedType: EnemyType? = null, sponsor: String = "") {
        val type = forcedType ?: when (Random.nextInt(100)) {
            in 0..50 -> EnemyType.DRONE
            in 51..85 -> EnemyType.INTERCEPTOR
            else -> EnemyType.CRUISER
        }

        val w = when (type) {
            EnemyType.DRONE -> 38f
            EnemyType.INTERCEPTOR -> 46f
            EnemyType.CRUISER -> 60f
            EnemyType.COMMUNITY_SPONSORED -> 54f
        }
        val h = w

        val hp = when (type) {
            EnemyType.DRONE -> 40f
            EnemyType.INTERCEPTOR -> 70f
            EnemyType.CRUISER -> 120f
            EnemyType.COMMUNITY_SPONSORED -> 150f
        }

        val enemy = Enemy(
            x = Random.nextFloat() * (width - w * 2) + w,
            y = -40f,
            vx = Random.nextFloat() * 120f - 60f,
            vy = (Random.nextFloat() * 100f + 90f + _waveState.value * 12f),
            type = type,
            health = hp,
            maxHealth = hp,
            width = w,
            height = h,
            scoreValue = (hp * 2).toInt(),
            sponsorName = sponsor,
            shootTimer = Random.nextLong(1000L, 2500L)
        )
        enemies.add(enemy)
    }

    private fun spawnBoss(summonedBy: String = "") {
        activeBoss = Boss(
            x = width / 2f,
            y = 160f,
            targetX = width / 2f,
            health = 2000f + (_waveState.value * 500f),
            maxHealth = 2000f + (_waveState.value * 500f),
            summonedBy = summonedBy
        )
        screenShake = 15f
        notices.add(
            FloatingNotice(
                text = "ALERTA: TITÃ DARKZ INVOCADO!",
                x = width / 2f,
                y = 280f,
                color = DarkzRedNeon
            )
        )
    }

    private fun damagePlayer(amount: Float) {
        if (player.invincibleTimer > 0) return

        if (player.shield > 0) {
            val remainShield = player.shield - amount
            if (remainShield < 0) {
                player.shield = 0f
                player.health += remainShield
            } else {
                player.shield = remainShield
            }
        } else {
            player.health -= amount
        }

        player.invincibleTimer = 300L
        screenShake = 10f
        _healthState.value = player.health.coerceAtLeast(0f)

        if (player.health <= 0f) {
            _isGameOver.value = true
        }
    }

    private fun defeatEnemy(e: Enemy) {
        enemies.remove(e)
        createSparks(e.x, e.y, DarkzAmber, 12)
        _scoreState.value += e.scoreValue * comboMultiplier

        waveKillCount++
        if (waveKillCount >= 15 && activeBoss == null) {
            waveKillCount = 0
            _waveState.value += 1
            if (_waveState.value % 3 == 0) {
                spawnBoss()
            }
        }
    }

    private fun defeatBoss(boss: Boss) {
        activeBoss = null
        createSparks(boss.x, boss.y, DarkzRedNeon, 40)
        _scoreState.value += 5000L
        screenShake = 20f
        _waveState.value += 1

        notices.add(
            FloatingNotice(
                text = "TITÃ DESTRUÍDO! +5000 PTS",
                x = width / 2f,
                y = 250f,
                color = DarkzGreenLive
            )
        )
    }

    private fun createSparks(x: Float, y: Float, color: Color, count: Int) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 6.28f
            val spd = Random.nextFloat() * 220f + 60f
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * spd,
                    vy = kotlin.math.sin(angle) * spd,
                    color = color,
                    radius = Random.nextFloat() * 3f + 2f,
                    lifeMs = Random.nextLong(300L, 700L),
                    maxLifeMs = 700L
                )
            )
        }
    }

    override fun handleLiveEvent(event: LiveEvent) {
        when (event.type) {
            LiveEventType.LIKE -> {
                // LIKE: Ativa Canhão Especial Spread e regenera 10 de escudo
                player.specialLaserTimer = 6000L
                player.shield = (player.shield + 10f).coerceAtMost(player.maxShield)
                _scoreState.value += 100L
                notices.add(
                    FloatingNotice(
                        text = "LIKE de ${event.senderName}: CANHÃO TRIPLO ATIVADO!",
                        x = width / 2f,
                        y = height * 0.7f,
                        color = DarkzCyanNeon
                    )
                )
            }
            LiveEventType.COMMENT -> {
                // COMMENT: Invoca Inimigo Patrocinado com o nome do espectador
                spawnEnemy(
                    forcedType = EnemyType.COMMUNITY_SPONSORED,
                    sponsor = event.senderName
                )
                notices.add(
                    FloatingNotice(
                        text = "Invasor invocado por ${event.senderName}!",
                        x = width / 2f,
                        y = height * 0.4f,
                        color = DarkzAmber
                    )
                )
            }
            LiveEventType.GIFT -> {
                // GIFT: Detona onda de choque EMP limpando todos os projéteis inimigos e causando dano em massa
                projectiles.removeAll { !it.isPlayer }
                for (e in enemies) {
                    e.health -= 80f
                    createSparks(e.x, e.y, DarkzRedNeon, 6)
                }
                enemies.removeAll { it.health <= 0f }
                activeBoss?.let { it.health -= 200f }
                screenShake = 16f
                notices.add(
                    FloatingNotice(
                        text = "BOMBA EMP enviada por ${event.senderName}!",
                        x = width / 2f,
                        y = height * 0.5f,
                        color = DarkzGreenLive
                    )
                )
            }
            LiveEventType.SPECIAL_GIFT -> {
                // SPECIAL_GIFT: Invoca imediatamente o Chefe Titã!
                spawnBoss(summonedBy = event.senderName)
            }
        }
    }

    override fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    override fun restartGame() {
        initGame(width, height)
    }

    override fun cleanup() {
        enemies.clear()
        projectiles.clear()
        particles.clear()
    }
}
