package com.darkz.live.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkz.live.ui.components.DarkzArcadeButton
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
fun GameSelectScreen(
    onSelectGame: (String) -> Unit,
    onBackToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkzVoid)
    ) {
        // Header
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
                    text = "CATÁLOGO DE JOGOS",
                    color = DarkzWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ARQUITETURA MODULAR MULTIJOGOS",
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
            // Active Game: DARKZ BATTLE
            item {
                GameCard(
                    title = "DARKZ BATTLE",
                    badge = "DISPONÍVEL AGORA",
                    badgeColor = DarkzGreenLive,
                    description = "Combate espacial arcade com suporte completo a eventos de live. Curtidas concedem lasers duplos e presentes invocam ondas de invasores e o titã Goliath.",
                    icon = Icons.Default.RocketLaunch,
                    isAvailable = true,
                    isSelected = true,
                    onClick = { onSelectGame("darkz_battle") }
                )
            }

            // Upcoming Game 1: DARKZ SPEED RUNNER
            item {
                GameCard(
                    title = "DARKZ CYBER RACE",
                    badge = "FASE 2 (EM BREVE)",
                    badgeColor = DarkzAmber,
                    description = "Corrida cyberpunk de alta velocidade. O chat controla armadilhas na pista, turbos de nitro e obstáculos dinâmicos.",
                    icon = Icons.Default.Speed,
                    isAvailable = false,
                    isSelected = false,
                    onClick = {}
                )
            }

            // Upcoming Game 2: DARKZ TITAN DEFENSE
            item {
                GameCard(
                    title = "DARKZ TITAN DEFENSE",
                    badge = "FASE 2 (EM BREVE)",
                    badgeColor = DarkzCyanNeon,
                    description = "Defesa de torre orbital holográfica. Os espectadores enviam tropas e melhorias de escudo em tempo real.",
                    icon = Icons.Default.Security,
                    isAvailable = false,
                    isSelected = false,
                    onClick = {}
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                DarkzArcadeButton(
                    text = "JOGAR DARKZ BATTLE",
                    onClick = { onSelectGame("darkz_battle") }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GameCard(
    title: String,
    badge: String,
    badgeColor: Color,
    description: String,
    icon: ImageVector,
    isAvailable: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkzSurface, shape = RoundedCornerShape(12.dp))
            .border(
                1.5.dp,
                if (isSelected) DarkzRedNeon else DarkzBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isAvailable, onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isAvailable) DarkzRedPrimary.copy(alpha = 0.2f) else DarkzSurfaceElevated,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isAvailable) DarkzRedPrimary else DarkzBorder,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isAvailable) DarkzRedNeon else DarkzGrayText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            color = DarkzWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selecionado",
                        tint = DarkzRedNeon,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (!isAvailable) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = DarkzGrayText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                color = DarkzGrayText,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
