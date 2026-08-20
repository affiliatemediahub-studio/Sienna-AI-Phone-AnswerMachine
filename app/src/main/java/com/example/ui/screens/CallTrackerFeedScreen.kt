package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VoicemailEntity
import com.example.ui.components.BentoCard
import com.example.ui.components.CategoryChip
import com.example.ui.components.SentimentBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.CategoryRecruiter
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.SiennaPrimaryLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SiennaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallTrackerFeedScreen(
    viewModel: SiennaViewModel,
    modifier: Modifier = Modifier
) {
    val dispatches by viewModel.trackerDispatches.collectAsStateWithLifecycle()
    val webhookUrl by viewModel.callTrackerWebhookUrl.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Space Header Banner
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BentoSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF0F766E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "John's Call Tracker",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Google Chat Space • Real-time Voicemail Sync",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SiennaAccent)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SentimentPositive.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SentimentPositive, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Connected",
                                    color = SentimentPositive,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sienna automatically posts comprehensive summary cards with caller sentiment, recruiter intel, and action items to this space immediately after recording.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Live Dispatched Summaries (${dispatches.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        if (dispatches.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BentoSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Dispatches Yet", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Incoming voicemails will generate immediate cards here.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(dispatches, key = { it.id }) { item ->
                TrackerDispatchCard(
                    voicemail = item,
                    onSelect = { viewModel.selectVoicemail(item) }
                )
            }
        }
    }
}

@Composable
private fun TrackerDispatchCard(
    voicemail: VoicemailEntity,
    onSelect: () -> Unit
) {
    val dateStr = SimpleDateFormat("h:mm a • MMM d", Locale.getDefault()).format(Date(voicemail.trackerSentTimestamp))
    var copied by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BentoSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header (Google Chat card format)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(SiennaPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Sienna Bot",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Dispatched $dateStr",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                CategoryChip(category = voicemail.category)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Card Container (Glassmorphic style)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BentoBackground,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (voicemail.sentiment) {
                        "POSITIVE" -> SentimentPositive.copy(alpha = 0.3f)
                        "NEGATIVE" -> SentimentNegative.copy(alpha = 0.3f)
                        else -> SiennaAccent.copy(alpha = 0.3f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Caller & Sentiment line
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = voicemail.callerName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        SentimentBadge(sentiment = voicemail.sentiment)
                    }

                    Text(
                        text = voicemail.callerNumber,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Summary
                    Text(
                        text = voicemail.summary,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Emotional state breakdown
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BentoSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tone: ",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${voicemail.emotionalTone} (${(voicemail.sentimentScore * 100).toInt()}% confidence)",
                                color = SiennaAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Recruiter Intel if present
                    if (!voicemail.recruiterRole.isNullOrBlank() || !voicemail.recruiterCompany.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E1B4B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "💼 Role Details Captured:",
                                    color = CategoryRecruiter,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (!voicemail.recruiterRole.isNullOrBlank()) {
                                    Text(
                                        text = "• Position: ${voicemail.recruiterRole}",
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                                if (!voicemail.recruiterCompany.isNullOrBlank()) {
                                    Text(
                                        text = "• Company: ${voicemail.recruiterCompany}",
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                                if (!voicemail.recruiterCallback.isNullOrBlank()) {
                                    Text(
                                        text = "• Callback: ${voicemail.recruiterCallback}",
                                        color = SiennaAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Action Items
                    if (voicemail.actionItems.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            voicemail.actionItems.split(",").forEach { action ->
                                if (action.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SentimentPositive,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = action.trim(),
                                            color = TextPrimary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Open Full Transcript", fontSize = 12.sp)
                }

                Button(
                    onClick = { copied = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Done else Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = if (copied) SentimentPositive else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "Copied" else "Copy Info",
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
