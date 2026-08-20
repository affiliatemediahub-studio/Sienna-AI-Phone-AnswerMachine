package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoicemailEntity
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.CategoryRecruiter
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.SiennaPrimaryLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoicemailDetailBottomSheet(
    voicemail: VoicemailEntity,
    isPlaying: Boolean,
    progress: Float,
    onDismiss: () -> Unit,
    onTogglePlay: () -> Unit,
    onToggleStar: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateStr = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault()).format(Date(voicemail.timestamp))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BentoSurface,
        contentColor = TextPrimary,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = when (voicemail.category) {
                                    "RECRUITER" -> CategoryRecruiter.copy(alpha = 0.2f)
                                    "FRIEND_FAMILY" -> SentimentPositive.copy(alpha = 0.2f)
                                    else -> SiennaPrimary.copy(alpha = 0.2f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = voicemail.callerName.take(1).uppercase(),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = voicemail.callerName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "${voicemail.callerNumber} • $dateStr",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onToggleStar) {
                        Icon(
                            imageVector = if (voicemail.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (voicemail.isStarred) Color(0xFFFBBF24) else TextSecondary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color(0xFFF87171)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = voicemail.category)
                SentimentBadge(sentiment = voicemail.sentiment)
                UrgencyBadge(urgency = voicemail.urgencyLevel)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Player Bento Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BentoBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onTogglePlay,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(SiennaPrimary, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isPlaying) "Reading Voicemail..." else "Voicemail Audio Note",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Duration: ${voicemail.durationSeconds}s",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = SiennaAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AudioWaveformVisualizer(
                        modifier = Modifier.fillMaxWidth(),
                        waveformCsv = voicemail.audioWaveform,
                        isPlaying = isPlaying,
                        progress = progress,
                        maxHeight = 32.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Audio Vault File Link Row
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val audioUri = voicemail.audioFileUrl.ifBlank { voicemail.audioFilePath }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF090D16),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = SiennaAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = audioUri,
                                    color = SiennaAccent,
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Audio Link", audioUri)
                                        clipboard.setPrimaryClip(clip)
                                        android.widget.Toast.makeText(context, "Audio file link copied!", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Link",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Voicemail Audio Link - ${voicemail.callerName}")
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Original Audio Recording Link: $audioUri\nCaller: ${voicemail.callerName} (${voicemail.callerNumber})")
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Audio Link"))
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Link",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sienna AI Executive Summary
            BentoCard(
                title = "Sienna AI Executive Summary",
                icon = Icons.Default.Mood,
                iconTint = SiennaAccent,
                backgroundColor = BentoSurfaceVariant
            ) {
                Text(
                    text = voicemail.summary,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tone & Emotional State Analysis
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BentoBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Caller Emotional Tone:",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = voicemail.emotionalTone,
                                color = SiennaAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (voicemail.keyEmotionalPhrases.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Key Phrases: \"${voicemail.keyEmotionalPhrases}\"",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // Recruiter Details Box if available
            if (!voicemail.recruiterCompany.isNullOrBlank() || !voicemail.recruiterRole.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                BentoCard(
                    title = "Recruiter Opportunity Intel",
                    icon = Icons.Default.BusinessCenter,
                    iconTint = CategoryRecruiter,
                    backgroundColor = Color(0xFF1E1B4B)
                ) {
                    if (!voicemail.recruiterCompany.isNullOrBlank()) {
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(text = "Company: ", color = TextSecondary, fontSize = 13.sp)
                            Text(text = voicemail.recruiterCompany, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    if (!voicemail.recruiterRole.isNullOrBlank()) {
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(text = "Position: ", color = TextSecondary, fontSize = 13.sp)
                            Text(text = voicemail.recruiterRole, color = SiennaPrimaryLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    if (!voicemail.recruiterCallback.isNullOrBlank()) {
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(text = "Callback: ", color = TextSecondary, fontSize = 13.sp)
                            Text(text = voicemail.recruiterCallback, color = SiennaAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Action Items
            if (voicemail.actionItems.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                BentoCard(
                    title = "Action Items for John",
                    icon = Icons.Default.CheckCircle,
                    iconTint = SentimentPositive
                ) {
                    voicemail.actionItems.split(",").forEach { action ->
                        if (action.isNotBlank()) {
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SentimentPositive, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = action.trim(),
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Full Screening Transcript
            BentoCard(
                title = "Complete Screening Transcript",
                subtitle = "Transcribed live by Sienna AI Screener"
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BentoBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = voicemail.transcript,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { /* Callback trigger */ },
                    colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Back ${voicemail.callerName.split(" ").firstOrNull() ?: ""}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
