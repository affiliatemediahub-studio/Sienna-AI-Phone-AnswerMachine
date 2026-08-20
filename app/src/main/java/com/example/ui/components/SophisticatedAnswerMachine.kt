package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.DetectedEntity
import com.example.data.model.ContactEntity
import com.example.data.model.GreetingEntity
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.CategoryBusiness
import com.example.ui.theme.CategoryFriendFamily
import com.example.ui.theme.CategoryRecruiter
import com.example.ui.theme.CategorySpam
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.SiennaPrimaryDark
import com.example.ui.theme.SiennaPrimaryLight
import com.example.ui.theme.SiennaSecondary
import com.example.ui.theme.TextHighlight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.UrgencyCritical
import com.example.ui.theme.UrgencyHigh
import com.example.ui.theme.UrgencyMedium
import kotlin.math.cos
import kotlin.math.sin

/**
 * Sophisticated AI Phone Answering Machine Bento Component
 * Features dual rotating reel visualizer, dynamic audio spectrum meters,
 * real-time call screening telemetry, and interactive whisper action deck.
 */
@Composable
fun SophisticatedAnswerMachine(
    modifier: Modifier = Modifier,
    isCallActive: Boolean,
    callerName: String,
    callerNumber: String,
    callerCategory: String,
    activeGreeting: GreetingEntity? = null,
    callDurationSeconds: Int = 0,
    isSiennaSpeaking: Boolean = false,
    isTranscribing: Boolean = false,
    streamingTranscript: String = "",
    detectedEntities: List<DetectedEntity> = emptyList(),
    activeWhisper: String? = null,
    onAnswerCall: () -> Unit = {},
    onDeclineCall: () -> Unit = {},
    onTakeOverCall: () -> Unit = {},
    onSendWhisper: (String) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tapeReel")
    val reelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCallActive) 2400 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reelSpin"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val minutes = callDurationSeconds / 60
    val seconds = callDurationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = SiennaPrimaryDark, spotColor = SiennaAccent),
        shape = RoundedCornerShape(24.dp),
        color = BentoSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCallActive) SiennaAccent.copy(alpha = pulseGlow) else BentoCardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BentoSurfaceVariant.copy(alpha = 0.7f),
                            BentoSurface,
                            Color(0xFF080C14)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. Header Bar: Model & Status Telemetry ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.linearGradient(listOf(SiennaPrimary, SiennaSecondary)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Sienna AI",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SIENNA NEURAL REEL",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SiennaAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "v3.1 PRO",
                                    color = SiennaAccent,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isCallActive) "Intelligent Call Screening Active" else "Standing By for Incoming Calls",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // LED Digital Segment Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF070B11),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isCallActive) Color(0xFFEF4444) else SentimentPositive,
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (isCallActive) "REC $formattedDuration" else "READY 00:00",
                            color = if (isCallActive) Color(0xFFFF6B6B) else SentimentPositive,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- 2. Dual Magnetic Reel Visualizer Deck ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0A0F1D),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2638))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Subtle background grid canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawAnswerMachineDeck(
                            isSpinning = isCallActive,
                            leftRotation = reelRotation,
                            rightRotation = -reelRotation,
                            accentColor = if (isCallActive) SiennaAccent else SiennaPrimaryLight,
                            pulse = pulseGlow
                        )
                    }

                    // Foreground Answering Machine Badge & Active Greeting Pill
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(
                                Color(0xFF0F172A).copy(alpha = 0.85f),
                                RoundedCornerShape(10.dp)
                            )
                            .border(1.dp, SiennaPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSiennaSpeaking) Icons.Default.RecordVoiceOver else Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = SiennaAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isSiennaSpeaking -> "SIENNA SPEAKING"
                                    isTranscribing -> "TRANSCRIBING CALLER"
                                    isCallActive -> "AI SCREENING RUNNING"
                                    else -> "TAPE LOADED & ARMED"
                                },
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (activeGreeting != null) {
                            Text(
                                text = "Greeting: ${activeGreeting.title}",
                                color = SiennaPrimaryLight,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Live Audio Equalizer Bar Overlay on Bottom of Deck
                    if (isCallActive) {
                        LiveEqualizerBars(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 24.dp, vertical = 2.dp),
                            isSpeaking = isSiennaSpeaking || isTranscribing
                        )
                    }
                }
            }

            // --- 3. Active Caller & Intelligence Routing Card ---
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF131C2E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF243048)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Caller Avatar / Shield
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    when (callerCategory) {
                                        "RECRUITER" -> CategoryRecruiter.copy(alpha = 0.2f)
                                        "FRIEND_FAMILY" -> CategoryFriendFamily.copy(alpha = 0.2f)
                                        "VIP" -> CategoryBusiness.copy(alpha = 0.2f)
                                        "SPAM" -> CategorySpam.copy(alpha = 0.2f)
                                        else -> SiennaPrimary.copy(alpha = 0.2f)
                                    },
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    when (callerCategory) {
                                        "RECRUITER" -> CategoryRecruiter
                                        "FRIEND_FAMILY" -> CategoryFriendFamily
                                        "VIP" -> CategoryBusiness
                                        "SPAM" -> CategorySpam
                                        else -> SiennaPrimary
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = callerName.take(1).uppercase(),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = callerName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (callerCategory == "SPAM") {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = SentimentNegative, modifier = Modifier.size(14.dp))
                                } else {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SentimentPositive, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(
                                text = "$callerNumber • $callerCategory",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Call Mode Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (callerCategory) {
                            "RECRUITER" -> CategoryRecruiter.copy(alpha = 0.15f)
                            "FRIEND_FAMILY" -> CategoryFriendFamily.copy(alpha = 0.15f)
                            "VIP" -> CategoryBusiness.copy(alpha = 0.15f)
                            "SPAM" -> CategorySpam.copy(alpha = 0.15f)
                            else -> SiennaPrimary.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = when (callerCategory) {
                                "RECRUITER" -> "💼 Recruiter Filter"
                                "FRIEND_FAMILY" -> "❤️ Personal Line"
                                "VIP" -> "🏢 Executive Priority"
                                "SPAM" -> "🚫 Auto-Defend"
                                else -> "📞 General Screening"
                            },
                            color = when (callerCategory) {
                                "RECRUITER" -> CategoryRecruiter
                                "FRIEND_FAMILY" -> CategoryFriendFamily
                                "VIP" -> CategoryBusiness
                                "SPAM" -> CategorySpam
                                else -> SiennaPrimaryLight
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // --- 4. Real-Time Gemini Transcription Stream HUD & Entity Telemetry ---
            if (isCallActive && (isTranscribing || streamingTranscript.isNotEmpty() || detectedEntities.isNotEmpty())) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(SentimentNegative, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GEMINI 3.5-FLASH LIVE STREAMING AUDIO-TO-TEXT",
                                    color = SiennaAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            if (isTranscribing) {
                                Text(
                                    text = "STREAMING...",
                                    color = SentimentPositive,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (streamingTranscript.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "“$streamingTranscript”",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        // Detected Entity Chips
                        if (detectedEntities.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(detectedEntities) { entity ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (entity.type) {
                                            "COMPENSATION" -> SentimentPositive.copy(alpha = 0.2f)
                                            "COMPANY", "ROLE" -> SiennaPrimary.copy(alpha = 0.25f)
                                            "PHONE", "DATETIME" -> SiennaAccent.copy(alpha = 0.2f)
                                            "URGENCY" -> SentimentNegative.copy(alpha = 0.2f)
                                            else -> BentoSurfaceVariant
                                        },
                                        border = androidx.compose.foundation.BorderStroke(
                                            0.5.dp,
                                            when (entity.type) {
                                                "COMPENSATION" -> SentimentPositive
                                                "COMPANY", "ROLE" -> SiennaPrimaryLight
                                                "PHONE", "DATETIME" -> SiennaAccent
                                                "URGENCY" -> SentimentNegative
                                                else -> BentoCardBorder
                                            }
                                        )
                                    ) {
                                        Text(
                                            text = "${entity.type}: ${entity.value}",
                                            color = when (entity.type) {
                                                "COMPENSATION" -> SentimentPositive
                                                "COMPANY", "ROLE" -> Color(0xFFC7D2FE)
                                                "PHONE", "DATETIME" -> SiennaAccent
                                                "URGENCY" -> SentimentNegative
                                                else -> TextSecondary
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 5. Interactive Live Action Deck: Takeover & AI Whispers ---
            if (isCallActive) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Executive AI Whispers",
                            color = TextHighlight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Direct Sienna live mid-call",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    // Quick AI Whisper Prompts
                    val whisperOptions = listOf(
                        "💰 Ask for salary & level",
                        "📅 Offer calendar link",
                        "💼 Say John is in meetings",
                        "📧 Ask to email resume",
                        "👋 Wrap up & say goodbye",
                        "🚫 Decline & terminate"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(whisperOptions) { option ->
                            val isSelected = activeWhisper == option
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) SiennaPrimary else Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) SiennaAccent else Color(0xFF334155)
                                ),
                                modifier = Modifier.clickable { onSendWhisper(option) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else SiennaAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = option,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Primary Call Control Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Take Over Button (Pick Up)
                        Button(
                            onClick = onTakeOverCall,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SentimentPositive
                            )
                        ) {
                            Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Take Over Call", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // End / Hang Up
                        Button(
                            onClick = onDeclineCall,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SentimentNegative
                            )
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("End & Save Log", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Idle Quick Start Screening Button
                Button(
                    onClick = onAnswerCall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SiennaPrimary
                    )
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Arm Answering Machine & Simulate Call", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Animated dynamic Equalizer Spectrum visualizer for live voice screening
 */
@Composable
private fun LiveEqualizerBars(
    modifier: Modifier = Modifier,
    isSpeaking: Boolean
) {
    val transition = rememberInfiniteTransition(label = "eqBars")
    val bar1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(280, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b3"
    )
    val bar4 by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(310, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b4"
    )
    val bar5 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(480, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b5"
    )

    Canvas(modifier = modifier) {
        val totalBars = 28
        val barWidth = 4.dp.toPx()
        val spacing = (size.width - (totalBars * barWidth)) / (totalBars - 1)
        val maxHeight = size.height

        val heights = if (isSpeaking) {
            listOf(bar1, bar2, bar3, bar4, bar5, bar2, bar4, bar1, bar3, bar5, bar4, bar2, bar1, bar3, bar5, bar2, bar4, bar1, bar3, bar5, bar4, bar2, bar1, bar3, bar5, bar2, bar4, bar1)
        } else {
            List(totalBars) { 0.15f }
        }

        for (i in 0 until totalBars) {
            val x = i * (barWidth + spacing)
            val h = heights[i % heights.size] * maxHeight
            val y = (maxHeight - h) / 2

            val color = when {
                i % 4 == 0 -> SiennaAccent
                i % 3 == 0 -> SiennaPrimaryLight
                else -> SiennaSecondary
            }

            drawRoundRect(
                color = color.copy(alpha = if (isSpeaking) 0.85f else 0.3f),
                topLeft = Offset(x, y),
                size = Size(barWidth, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}

/**
 * Custom Canvas drawing for the dual-reel cassette tape visualizer
 */
private fun DrawScope.drawAnswerMachineDeck(
    isSpinning: Boolean,
    leftRotation: Float,
    rightRotation: Float,
    accentColor: Color,
    pulse: Float
) {
    val width = size.width
    val height = size.height
    val centerY = height / 2

    val leftReelCenter = Offset(width * 0.22f, centerY)
    val rightReelCenter = Offset(width * 0.78f, centerY)
    val reelRadius = height * 0.34f

    // Draw connecting magnetic tape ribbon
    val tapePath = Path().apply {
        moveTo(leftReelCenter.x, leftReelCenter.y + reelRadius * 0.8f)
        lineTo(rightReelCenter.x, rightReelCenter.y + reelRadius * 0.8f)
    }
    drawPath(
        path = tapePath,
        color = Color(0xFF1E293B),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    // Left Reel Spool
    drawReelSpool(leftReelCenter, reelRadius, leftRotation, accentColor, isSpinning)

    // Right Reel Spool
    drawReelSpool(rightReelCenter, reelRadius, rightRotation, accentColor, isSpinning)

    // Center decorative tape guide window
    val centerWindowWidth = width * 0.32f
    val centerWindowHeight = height * 0.45f
    drawRoundRect(
        color = Color(0xFF0F172A).copy(alpha = 0.7f),
        topLeft = Offset((width - centerWindowWidth) / 2, (height - centerWindowHeight) / 2),
        size = Size(centerWindowWidth, centerWindowHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawReelSpool(
    center: Offset,
    radius: Float,
    rotationDegrees: Float,
    accentColor: Color,
    isSpinning: Boolean
) {
    // Outer wheel ring
    drawCircle(
        color = Color(0xFF1E293B),
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    // Inner spool hub
    drawCircle(
        color = Color(0xFF0F172A),
        radius = radius * 0.55f,
        center = center
    )
    drawCircle(
        color = accentColor.copy(alpha = if (isSpinning) 0.8f else 0.4f),
        radius = radius * 0.55f,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Spool spokes (3 spokes 120 degrees apart)
    val spokeCount = 3
    val rad = Math.toRadians(rotationDegrees.toDouble())
    for (i in 0 until spokeCount) {
        val angle = rad + (i * 2 * Math.PI / spokeCount)
        val endX = center.x + (radius * 0.48f * cos(angle)).toFloat()
        val endY = center.y + (radius * 0.48f * sin(angle)).toFloat()

        drawLine(
            color = accentColor.copy(alpha = 0.6f),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Spoke tip dot
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = 2.dp.toPx(),
            center = Offset(endX, endY)
        )
    }

    // Center hub pin
    drawCircle(
        color = Color(0xFF334155),
        radius = radius * 0.2f,
        center = center
    )
}
