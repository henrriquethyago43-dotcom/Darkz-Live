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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkz.live.events.EventSimulator
import com.darkz.live.events.LiveEventManager
import com.darkz.live.ui.components.DarkzArcadeButton
import com.darkz.live.ui.components.DarkzEventItem
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
fun EventHubScreen(
    eventManager: LiveEventManager,
    onBackToDashboard: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val simulator = remember { EventSimulator(eventManager) }
    var isAutoSimRunning by remember { mutableStateOf(false) }

    val recentEvents by eventManager.recentEvents.collectAsState()
    val stats by eventManager.stats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkzVoid)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToDashboard,
                modifier = Modifier
                    .size(38.dp)
                    .background(DarkzSurface, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, DarkzBorder, shape = RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = DarkzWhite
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "CENTRAL DE EVENTOS",
                    color = DarkzWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SIMULADOR E INTEGRAÇÃO DE LIVES",
                    color = DarkzRedNeon,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulator Control Panel
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkzSurface, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, DarkzBorder, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "DISPARADORES DE TESTE",
                            color = DarkzWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // LIKE trigger
                        SimButtonRow(
                            label = "1. LIKE",
                            actionDesc = "Ativa Canhão Triplo Spread",
                            badgeColor = DarkzRedNeon,
                            onClick = { simulator.triggerLike() }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // COMMENT trigger
                        SimButtonRow(
                            label = "2. COMENTÁRIO",
                            actionDesc = "Invoca Invasor Patrocinado",
                            badgeColor = DarkzCyanNeon,
                            onClick = { simulator.triggerComment() }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // GIFT trigger
                        SimButtonRow(
                            label = "3. PRESENTE (GIFT)",
                            actionDesc = "Bomba EMP de Área",
                            badgeColor = DarkzAmber,
                            onClick = { simulator.triggerGift() }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // SPECIAL_GIFT trigger
                        SimButtonRow(
                            label = "4. SUPER PRESENTE",
                            actionDesc = "Invoca Chefe Titã Imediato",
                            badgeColor = DarkzRedPrimary,
                            onClick = { simulator.triggerSpecialGift() }
                        )
                    }
                }
            }

            // Auto-Simulator Mode Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkzSurfaceElevated, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, if (isAutoSimRunning) DarkzGreenLive else DarkzBorder, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SIMULAÇÃO AUTOMÁTICA",
                                color = DarkzWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isAutoSimRunning) "Gerando tráfego contínuo de live..." else "Gera eventos aleatórios a cada ~2.5s",
                                color = if (isAutoSimRunning) DarkzGreenLive else DarkzGrayText,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = isAutoSimRunning,
                            onCheckedChange = { checked ->
                                isAutoSimRunning = checked
                                if (checked) {
                                    simulator.startAutoSimulation(scope)
                                } else {
                                    simulator.stopAutoSimulation()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkzWhite,
                                checkedTrackColor = DarkzGreenLive,
                                uncheckedThumbColor = DarkzGrayText,
                                uncheckedTrackColor = DarkzSurface
                            )
                        )
                    }
                }
            }

            // Event Feed Header & Clear Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FEED AO VIVO EM TEMPO REAL",
                        color = DarkzWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    IconButton(
                        onClick = { eventManager.clearHistory() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Limpar",
                            tint = DarkzGrayText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Events List
            if (recentEvents.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum evento registrado. Clique em um dos botões acima para testar.",
                        color = DarkzGrayText,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
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
private fun SimButtonRow(
    label: String,
    actionDesc: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkzSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = badgeColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = actionDesc,
                color = DarkzWhite.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}
