package com.darkz.live.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkz.live.events.EventSimulator
import com.darkz.live.events.LiveEventManager
import com.darkz.live.games.darkzbattle.DarkzBattleEngine
import com.darkz.live.ui.theme.DarkzAmber
import com.darkz.live.ui.theme.DarkzBorder
import com.darkz.live.ui.theme.DarkzCyanNeon
import com.darkz.live.ui.theme.DarkzGreenLive
import com.darkz.live.ui.theme.DarkzRedNeon
import com.darkz.live.ui.theme.DarkzRedPrimary
import com.darkz.live.ui.theme.DarkzSurface
import com.darkz.live.ui.theme.DarkzVoid
import com.darkz.live.ui.theme.DarkzWhite
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(
    eventManager: LiveEventManager,
    onBackToDashboard: () -> Unit
) {
    val engine = remember { DarkzBattleEngine() }
    val simulator = remember { EventSimulator(eventManager) }

    val score by engine.scoreState.collectAsState()
    val health by engine.healthState.collectAsState()
    val wave by engine.waveState.collectAsState()
    val isGameOver by engine.isGameOver.collectAsState()
    val isPaused by engine.isPaused.collectAsState()

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var frameTick by remember { mutableStateOf(0) }

    // Connect event manager flow to game engine
    LaunchedEffect(Unit) {
        eventManager.eventFlow.collect { event ->
            engine.handleLiveEvent(event)
        }
    }

    // Main 60 FPS Game Loop with smooth canvas invalidation
    LaunchedEffect(Unit) {
        var lastTime = System.currentTimeMillis()
        while (isActive) {
            val now = System.currentTimeMillis()
            val delta = now - lastTime
            lastTime = now
            engine.update(delta)
            frameTick++
            kotlinx.coroutines.delay(16L) // ~60 FPS
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            engine.cleanup()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkzVoid)
    ) {
        // Android Canvas for the game
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    val newSize = Size(size.width.toFloat(), size.height.toFloat())
                    if (canvasSize != newSize && newSize.width > 0f && newSize.height > 0f) {
                        canvasSize = newSize
                        engine.initGame(newSize.width, newSize.height)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        engine.onTouchMove(change.position.x, change.position.y)
                        change.consume()
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            engine.onTouchMove(offset.x, offset.y)
                        }
                    )
                }
        ) {
            val _tick = frameTick // Read frameTick to trigger redraw on every frame
            engine.render(this)
        }

        // Top HUD Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBackToDashboard,
                    modifier = Modifier
                        .size(38.dp)
                        .background(DarkzSurface.copy(alpha = 0.85f), shape = CircleShape)
                        .border(1.dp, DarkzBorder, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = DarkzWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Wave & Score Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ONDA $wave",
                        color = DarkzRedNeon,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "SCORE: $score",
                        color = DarkzWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Pause Button
                IconButton(
                    onClick = { engine.togglePause() },
                    modifier = Modifier
                        .size(38.dp)
                        .background(DarkzSurface.copy(alpha = 0.85f), shape = CircleShape)
                        .border(1.dp, DarkzBorder, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pausar",
                        tint = DarkzWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Player Health Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HP",
                    color = DarkzRedNeon,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.width(26.dp)
                )
                LinearProgressIndicator(
                    progress = { (health / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = if (health > 35f) DarkzGreenLive else DarkzRedNeon,
                    trackColor = Color(0xFF2A2A38)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${health.toInt()}%",
                    color = DarkzWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // Bottom In-Game Event Simulator Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkzSurface.copy(alpha = 0.92f), shape = RoundedCornerShape(12.dp))
                    .border(1.dp, DarkzBorder, shape = RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickEventBtn(
                    label = "LIKE",
                    icon = Icons.Default.Favorite,
                    color = DarkzRedNeon,
                    onClick = { simulator.triggerLike() }
                )
                QuickEventBtn(
                    label = "CHAT",
                    icon = Icons.Default.Chat,
                    color = DarkzCyanNeon,
                    onClick = { simulator.triggerComment() }
                )
                QuickEventBtn(
                    label = "GIFT",
                    icon = Icons.Default.CardGiftcard,
                    color = DarkzAmber,
                    onClick = { simulator.triggerGift() }
                )
                QuickEventBtn(
                    label = "CHEFE",
                    icon = Icons.Default.Bolt,
                    color = DarkzRedPrimary,
                    onClick = { simulator.triggerSpecialGift() }
                )
            }
        }

        // Pause Dialog
        if (isPaused) {
            AlertDialog(
                onDismissRequest = { engine.togglePause() },
                containerColor = DarkzSurface,
                title = {
                    Text(
                        text = "JOGO PAUSADO",
                        color = DarkzWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "A live e a transmissão continuam ativas.",
                        color = DarkzWhite.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { engine.togglePause() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkzRedPrimary)
                    ) {
                        Text("CONTINUAR", color = DarkzWhite)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = onBackToDashboard,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("SAIR DO JOGO", color = DarkzRedNeon)
                    }
                }
            )
        }

        // Game Over Dialog
        if (isGameOver) {
            AlertDialog(
                onDismissRequest = {},
                containerColor = DarkzSurface,
                title = {
                    Text(
                        text = "GAME OVER",
                        color = DarkzRedNeon,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Sua nave foi destruída!",
                            color = DarkzWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pontuação final: $score",
                            color = DarkzCyanNeon,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Onda alcançada: $wave",
                            color = DarkzWhite.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { engine.restartGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkzRedPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("REINICIAR", color = DarkzWhite)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = onBackToDashboard,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("MENU PRINCIPAL", color = DarkzWhite.copy(alpha = 0.7f))
                    }
                }
            )
        }
    }
}

@Composable
private fun QuickEventBtn(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .background(color.copy(alpha = 0.16f), shape = RoundedCornerShape(10.dp))
                .border(1.dp, color, shape = RoundedCornerShape(10.dp))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = DarkzWhite,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
