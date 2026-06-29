package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.TransactionEntity
import com.example.ui.theme.*
import java.util.Locale

// Reusable Vibrant Palette Light background with subtle warmth
@Composable
fun AeinzBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .drawBehind {
                // Add a very subtle, beautiful orange glow at the top-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0FFF97316), Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.15f),
                        radius = size.width * 0.8f
                    )
                )
                // Add another subtle gold glow at the bottom-left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x06FFEAB308), Color.Transparent),
                        center = Offset(size.width * 0.1f, size.height * 0.85f),
                        radius = size.width * 0.8f
                    )
                )
            },
        content = content
    )
}

// Sleek Slate-100 bordered card with white surface
@Composable
fun AeinzCard(
    modifier: Modifier = Modifier,
    borderGlow: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    } else {
        modifier
    }

    val borderBrush = if (borderGlow) {
        Brush.horizontalGradient(listOf(PrimaryOrange, LightOrange))
    } else {
        Brush.horizontalGradient(listOf(Slate100, Slate100))
    }

    Column(
        modifier = cardModifier
            .shadow(
                elevation = if (borderGlow) 4.dp else 1.5.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        content = content
    )
}

// Glowing persuasive filled button
@Composable
fun AeinzButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    val buttonBrush = if (enabled) {
        Brush.horizontalGradient(listOf(PrimaryOrange, DarkOrange))
    } else {
        Brush.horizontalGradient(listOf(Slate300, Slate400))
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(54.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBrush)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

// Outlined sleek button with orange border
@Composable
fun AeinzOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(54.dp)
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = if (enabled) PrimaryOrange else Slate300,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) PrimaryOrange else Slate500,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

// Beautiful glowing circular progress chart
@Composable
fun AeinzCircularProgress(
    progress: Float, // 0.0 to 1.0
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp
) {
    val sweepAngle = 360f * progress.coerceIn(0f, 1f)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = sweepAngle,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 80f)
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            // Track
            drawCircle(
                color = Slate100,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            // Progress Glow Underlay
            drawArc(
                brush = Brush.sweepGradient(listOf(DarkOrange, PrimaryOrange, LightOrange, DarkOrange)),
                startAngle = -90f,
                sweepAngle = animatedProgress.value,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx() + 4f, cap = StrokeCap.Round),
                alpha = 0.15f
            )
            // Main Progress Arc
            drawArc(
                brush = Brush.sweepGradient(listOf(DarkOrange, PrimaryOrange, LightOrange, DarkOrange)),
                startAngle = -90f,
                sweepAngle = animatedProgress.value,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Slate900,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Slate500,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Custom Horizontal Progress Bar for savings goals
@Composable
fun AeinzProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp
) {
    val boundedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(boundedProgress) {
        animatedProgress.animateTo(
            targetValue = boundedProgress,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Slate100, shape = RoundedCornerShape(height / 2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress.value)
                .background(
                    brush = Brush.horizontalGradient(listOf(PrimaryOrange, LightOrange)),
                    shape = RoundedCornerShape(height / 2)
                )
        )
    }
}

// Custom Financial Bar Chart
@Composable
fun AeinzBarChart(
    data: List<Float>, // 3 values: deposits, withdrawals, card spend
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val maxVal = data.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, value ->
            val ratio = value / maxVal
            val animatedRatio = remember { Animatable(0f) }
            
            LaunchedEffect(ratio) {
                animatedRatio.animateTo(ratio, animationSpec = spring(stiffness = 50f))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = String.format(Locale.ROOT, "%,.0f", value),
                    color = Slate900,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                val barColor = when (index) {
                    0 -> EmeraldGreen // Deposits
                    1 -> CrimsonRed   // Withdrawals
                    else -> PrimaryOrange // Card Spend
                }
                
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .fillMaxHeight(animatedRatio.value * 0.7f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(barColor.copy(alpha = 0.8f), barColor)
                            ),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = labels[index],
                    color = Slate500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Real-time floating Notification Card at the top of the screen
@Composable
fun RealtimeNotificationBanner(
    notification: TransactionEntity,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, PrimaryOrange)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(PrimaryOrange.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Alert",
                    tint = PrimaryOrange,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.notificationTitle,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.notificationMessage,
                    color = Slate300,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Slate300,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
