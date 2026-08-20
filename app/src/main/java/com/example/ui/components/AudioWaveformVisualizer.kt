package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimaryLight

@Composable
fun AudioWaveformVisualizer(
    modifier: Modifier = Modifier,
    waveformCsv: String = "20,40,65,85,95,70,50,80,60,45,90,75,35,55,80,65,40,60,75,50,30,20",
    isPlaying: Boolean = false,
    progress: Float = 0f,
    activeColor: Color = SiennaPrimaryLight,
    inactiveColor: Color = Color(0xFF374151),
    maxHeight: Dp = 36.dp,
    barWidth: Dp = 3.dp,
    barSpacing: Dp = 2.dp
) {
    val amplitudes = waveformCsv.split(",").mapNotNull { it.trim().toFloatOrNull() }
        .ifEmpty { listOf(20f, 40f, 60f, 80f, 50f, 70f, 90f, 40f, 60f, 30f) }

    val infiniteTransition = rememberInfiniteTransition(label = "waveAnim")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(barSpacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        amplitudes.forEachIndexed { index, amp ->
            val barProgress = index.toFloat() / amplitudes.size
            val isPassed = barProgress <= progress
            val scale = if (isPlaying) pulseAnim else 1.0f
            val dynamicHeight = (maxHeight.value * (amp / 100f) * scale).coerceIn(4f, maxHeight.value).dp

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(dynamicHeight)
                    .background(
                        color = if (isPlaying && isPassed) SiennaAccent else if (isPassed) activeColor else inactiveColor,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
