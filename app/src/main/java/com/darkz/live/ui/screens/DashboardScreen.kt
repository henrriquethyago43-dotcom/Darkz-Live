package com.darkz.live.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkz.live.events.LiveEventManager
import com.darkz.live.ui.components.DarkzArcadeButton
import com.darkz.live.ui.components.DarkzEventItem
import com.darkz.live.ui.components.DarkzHeader
import com.darkz.live.ui.theme.DarkzAmber
import com.darkz.live.ui.theme.DarkzBorder
import com.darkz.live.ui.theme.DarkzCyanNeon
import com.darkz.live.ui.theme.DarkzGrayText
import com.darkz.live.ui.theme.DarkzGreenLive
import com.darkz.live.ui.theme.DarkzRedNeon
import com.darkz.live.ui.theme.DarkzRedPrimary
import com.darkz.live.ui.theme.DarkzSurface
import com.darkz.live.ui.theme.DarkzSurfaceElevated
import com.darkz.live.ui.theme.DarkzVoid
import com.darkz.live.ui.theme.DarkzWhite

@Composable
fun DashboardScreen(
    eventManager: LiveEventManager,
    onNavigateToGame: () -> Unit,
    onNavigateToGameSelect: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val stats by eventManager.stats.collectAsState()
    val recentEvents by eventManager.recentEvents.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkzVoid)
    ) {
        // App Header & Branding
        DarkzHeader(isConnected = stats.isConnected)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System Status HUD Panel
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkzSurface, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, DarkzBorder, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STATUS DO SISTEMA",
                                color = DarkzRedNeon,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (stats.isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                                    contentDescription = null,
                                    tint = if (stats.isConnected) DarkzGreenLive else DarkzRedNeon,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (stats.isConnected) "SIMULADOR PRONTO" else "DESCONECTADO",
                                    color = if (stats.isConnected) DarkzGreenLive else DarkzRedNeon,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live metrics counter row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricItem(label = "LIKES", value = "${stats.totalLikes}", color = DarkzRedNeon)
                            MetricItem(label = "CHATS", value = "${stats.totalComments}", color = DarkzCyanNeon)
                            MetricItem(label = "GIFTS", value = "${stats.totalGifts}", color = DarkzAmber)
                            MetricItem(label = "CHEFES", value = "${stats.totalSpecialGifts}", color = DarkzRedPrimary)
                        }
                    }
                }
            }

            // Primary Navigation Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DarkzArcadeButton(
                        text = "INICIAR LIVE",
                        icon = Icons.Default.Videocam,
                        isPrimary = true,
                        onClick = onNavigateToGame
                    )

                    DarkzArcadeButton(
                        text = "ESCOLHER JOGO",
                        icon = Icons.Default.RocketLaunch,
                        isPrimary = false,
                        accentColor = DarkzCyanNeon,
                        onClick = onNavigateToGameSelect
                    )

                    DarkzArcadeButton(
                        text = "CENTRAL DE EVENTOS",
                        icon = Icons.Default.Bolt,
                        isPrimary = false,
                        accentColor = DarkzAmber,
                        onClick = onNavigateToEvents
                    )

                    DarkzArcadeButton(
                        text = "CONFIGURAÇÕES",
                        icon = Icons.Default.Settings,
                        isPrimary = false,
                        accentColor = DarkzWhite,
                        onClick = onNavigateToSettings
                    )
                }
            }

            // Last Activity Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ÚLTIMA ATIVIDADE (LIVE FEED)",
                        color = DarkzWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${recentEvents.size} EVENTOS",
                        color = DarkzGrayText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            if (recentEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkzSurfaceElevated, shape = RoundedCornerShape(8.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NENHUM EVENTO REGISTRADO AINDA.\nUTILIZE A CENTRAL DE EVENTOS PARA SIMULAR LIKES E GIFTS.",
                            color = DarkzGrayText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(recentEvents) { event ->
                    DarkzEventItem(event = event)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
        Text(
            text = label,
            color = DarkzGrayText,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
    }
}
