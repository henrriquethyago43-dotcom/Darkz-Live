package com.darkz.live.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkz.live.events.LiveEvent
import com.darkz.live.events.LiveEventType
import com.darkz.live.ui.theme.DarkzBorder
import com.darkz.live.ui.theme.DarkzCyanNeon
import com.darkz.live.ui.theme.DarkzGrayDark
import com.darkz.live.ui.theme.DarkzGrayText
import com.darkz.live.ui.theme.DarkzGreenLive
import com.darkz.live.ui.theme.DarkzRedNeon
import com.darkz.live.ui.theme.DarkzRedPrimary
import com.darkz.live.ui.theme.DarkzSurface
import com.darkz.live.ui.theme.DarkzSurfaceElevated
import com.darkz.live.ui.theme.DarkzVoid
import com.darkz.live.ui.theme.DarkzWhite

@Composable
fun DarkzArcadeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    accentColor: Color = DarkzRedPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1.0f, label = "button_scale")

    val shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .height(56.dp)
            .background(
                brush = if (isPrimary) {
                    Brush.horizontalGradient(
                        colors = listOf(DarkzRedPrimary, Color(0xFF900008))
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(DarkzSurfaceElevated, DarkzSurface)
                    )
                },
                shape = shape
            )
            .border(
                BorderStroke(
                    width = if (isPrimary) 1.5.dp else 1.dp,
                    color = if (isPrimary) DarkzWhite.copy(alpha = 0.8f) else accentColor.copy(alpha = 0.6f)
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DarkzWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = DarkzWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun DarkzHeader(
    streamerTag: String = "@darkz_official",
    isConnected: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkzVoid)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "DARKZ",
                    color = DarkzWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LIVE",
                    color = DarkzRedNeon,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = "STREAM INTERACT PLATFORM // $streamerTag",
                color = DarkzGrayText,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }

        // Status Badge
        Box(
            modifier = Modifier
                .background(DarkzSurfaceElevated, shape = RoundedCornerShape(20.dp))
                .border(
                    BorderStroke(1.dp, if (isConnected) DarkzGreenLive.copy(alpha = 0.5f) else DarkzRedNeon),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = if (isConnected) DarkzGreenLive else DarkzRedNeon,
                    modifier = Modifier.size(8.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isConnected) "ONLINE" else "OFFLINE",
                    color = DarkzWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DarkzEventItem(event: LiveEvent) {
    val badgeColor = when (event.type) {
        LiveEventType.LIKE -> DarkzRedNeon
        LiveEventType.COMMENT -> DarkzCyanNeon
        LiveEventType.GIFT -> Color(0xFFFFB800)
        LiveEventType.SPECIAL_GIFT -> DarkzRedPrimary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkzSurface, shape = RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, DarkzBorder), shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(badgeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                .border(BorderStroke(1.dp, badgeColor), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = event.type.name,
                color = badgeColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.senderName,
                color = DarkzWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = event.getSummaryText(),
                color = DarkzGrayText,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}
