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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkz.live.settings.SettingsViewModel
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
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackToDashboard: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkzVoid)
    ) {
        // Top Bar
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
                    text = "CONFIGURAÇÕES",
                    color = DarkzWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SISTEMA LOCAL & PREFERÊNCIAS",
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
            // Audio Settings Card
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
                            text = "ÁUDIO & EFEITOS",
                            color = DarkzWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Sound Volume
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Efeitos Sonoros (SFX)", color = DarkzWhite, fontSize = 13.sp)
                            Text("${(state.soundVolume * 100).toInt()}%", color = DarkzRedNeon, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = state.soundVolume,
                            onValueChange = { viewModel.setSoundVolume(it) },
                            colors = SliderDefaults.colors(
                                thumbColor = DarkzRedNeon,
                                activeTrackColor = DarkzRedPrimary,
                                inactiveTrackColor = DarkzSurfaceElevated
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Music Volume
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Música Arcade Synth", color = DarkzWhite, fontSize = 13.sp)
                            Text("${(state.musicVolume * 100).toInt()}%", color = DarkzCyanNeon, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = state.musicVolume,
                            onValueChange = { viewModel.setMusicVolume(it) },
                            colors = SliderDefaults.colors(
                                thumbColor = DarkzCyanNeon,
                                activeTrackColor = DarkzCyanNeon,
                                inactiveTrackColor = DarkzSurfaceElevated
                            )
                        )
                    }
                }
            }

            // Gameplay & Tactile Settings
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkzSurface, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, DarkzBorder, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "JOGABILIDADE & TÁTIL",
                            color = DarkzWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        // Vibration switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Feedback Tátil (Vibração)", color = DarkzWhite, fontSize = 13.sp)
                                Text("Vibra ao ser atingido ou disparar especiais", color = DarkzGrayText, fontSize = 11.sp)
                            }
                            Switch(
                                checked = state.vibrationEnabled,
                                onCheckedChange = { viewModel.toggleVibration(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = DarkzRedPrimary)
                            )
                        }

                        // High FPS switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Modo Alto Desempenho", color = DarkzWhite, fontSize = 13.sp)
                                Text("Execução a 60/120 FPS ultra suave", color = DarkzGrayText, fontSize = 11.sp)
                            }
                            Switch(
                                checked = state.highFpsMode,
                                onCheckedChange = { viewModel.toggleHighFps(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = DarkzGreenLive)
                            )
                        }

                        // Auto Fire switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Disparo Contínuo", color = DarkzWhite, fontSize = 13.sp)
                                Text("A nave atira automaticamente", color = DarkzGrayText, fontSize = 11.sp)
                            }
                            Switch(
                                checked = state.autoFire,
                                onCheckedChange = { viewModel.toggleAutoFire(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = DarkzCyanNeon)
                            )
                        }
                    }
                }
            }

            // Offline & Architecture Spec Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkzSurfaceElevated, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, DarkzBorder, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = DarkzCyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ESPECIFICAÇÕES DO SISTEMA",
                                color = DarkzCyanNeon,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Aplicativo 100% Android Nativo em Kotlin & Jetpack Compose\n• MinSdk: 26 (Android 8.0) | TargetSdk: 34 (Android 14)\n• Funcionamento totalmente offline e local\n• Sem anúncios, sem rastreadores, sem necessidade de login\n• Arquitetura orientada a StateFlow e SharedFlow",
                            color = DarkzWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
