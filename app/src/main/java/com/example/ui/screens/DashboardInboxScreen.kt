package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VoicemailEntity
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.BentoCard
import com.example.ui.components.CategoryChip
import com.example.ui.components.SentimentBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.CategoryRecruiter
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.SiennaPrimaryDark
import com.example.ui.theme.SiennaPrimaryLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.SiennaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardInboxScreen(
    viewModel: SiennaViewModel,
    onNavigateToSimulator: () -> Unit,
    onNavigateToTracker: () -> Unit,
    onNavigateToArchive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val voicemails by viewModel.filteredVoicemails.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val activeGreeting by viewModel.activeGreeting.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val playingId by viewModel.playingVoicemailId.collectAsStateWithLifecycle()
    val progress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Bento Top Hero Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Hero Card: Sienna Status
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0F172A))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(SentimentPositive, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SIENNA ACTIVE • SCREENING CALLS",
                                        color = SiennaAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A).copy(alpha = 0.8f)
                                ) {
                                    Text(
                                        text = "John Lanter: Away",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Personal AI Voicemail Answering",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )

                            Text(
                                text = "Screening recruiters, capturing friends & family messages, blocking spam.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToSimulator,
                                    colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simulate Live Call", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = onNavigateToTracker,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = SiennaAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tracker Feed", fontSize = 13.sp, color = TextPrimary)
                                }
                            }
                        }
                    }
                }

                // Bento 2-Col Quick Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tile 1: Active Greeting
                    BentoCard(
                        modifier = Modifier.weight(1f),
                        title = "Active Greeting",
                        icon = Icons.Default.GraphicEq,
                        iconTint = SiennaPrimaryLight,
                        backgroundColor = BentoSurface
                    ) {
                        Text(
                            text = activeGreeting?.title ?: "Sienna Standard Greeting",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${activeGreeting?.messageText?.take(48) ?: "Hello, you have reached John Lanter's number..."}...\"",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2,
                            lineHeight = 14.sp
                        )
                    }

                    // Tile 2: Call Tracker Live Sync
                    BentoCard(
                        modifier = Modifier.weight(1f),
                        title = "John's Call Tracker",
                        icon = Icons.Default.Chat,
                        iconTint = SentimentPositive,
                        backgroundColor = BentoSurface
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Auto-Synced",
                                color = SentimentPositive,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Immediate card alerts sent on recording finish",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search voicemails, companies...", color = TextTertiary, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoSurface,
                            unfocusedContainerColor = BentoSurface,
                            focusedBorderColor = SiennaPrimary,
                            unfocusedBorderColor = BentoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = onNavigateToArchive,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.4f)),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = SiennaAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Archive", color = SiennaAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // Filter Chips Row
                val filters = listOf(
                    "ALL" to "All Voicemails",
                    "RECRUITER" to "💼 Recruiters",
                    "FRIEND_FAMILY" to "❤️ Friends & Family",
                    "HIGH_URGENCY" to "🔥 High Priority",
                    "POSITIVE" to "✨ Positive Sentiment",
                    "SPAM" to "🚫 Spam Blocked"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { (key, label) ->
                        val isSelected = selectedFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFilter(key) },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SiennaPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = BentoSurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BentoCardBorder,
                                selectedBorderColor = SiennaPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Screened Voicemail Inbox (${voicemails.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                if (unreadCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SiennaPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "$unreadCount new",
                            color = SiennaPrimaryLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Voicemail Item Cards
        if (voicemails.isEmpty()) {
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Screened Voicemails Found",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Simulate a live call to see Sienna transcribe and analyze in real-time.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(voicemails, key = { it.id }) { voicemail ->
                val isPlaying = playingId == voicemail.id
                val dateStr = SimpleDateFormat("MMM d • h:mm a", Locale.getDefault()).format(Date(voicemail.timestamp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectVoicemail(voicemail) },
                    shape = RoundedCornerShape(18.dp),
                    color = BentoSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPlaying) SiennaAccent else if (!voicemail.isRead) SiennaPrimary.copy(alpha = 0.5f) else BentoCardBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header
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
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = voicemail.callerName,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (!voicemail.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                                color = TextPrimary
                                            )
                                        )
                                        if (!voicemail.isRead) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(SiennaAccent, CircleShape)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${voicemail.callerNumber} • $dateStr",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.toggleStarred(voicemail) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (voicemail.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star",
                                    tint = if (voicemail.isStarred) Color(0xFFFBBF24) else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category & Sentiment Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryChip(category = voicemail.category)
                            SentimentBadge(sentiment = voicemail.sentiment)
                            UrgencyBadge(urgency = voicemail.urgencyLevel)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Summary
                        Text(
                            text = voicemail.summary,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Recruiter quick pill
                        if (!voicemail.recruiterRole.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E1B4B),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Role: ${voicemail.recruiterRole} (${voicemail.recruiterCompany ?: "Opportunity"})",
                                        color = SiennaPrimaryLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio Waveform & Mini Player
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BentoBackground, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.togglePlayVoicemail(voicemail) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(SiennaPrimary, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            AudioWaveformVisualizer(
                                modifier = Modifier.weight(1f),
                                waveformCsv = voicemail.audioWaveform,
                                isPlaying = isPlaying,
                                progress = if (isPlaying) progress else 0f,
                                maxHeight = 24.dp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${voicemail.durationSeconds}s",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Audio vault file link
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
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
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
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = audioUri,
                                        color = SiennaAccent,
                                        fontSize = 10.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Link",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Voicemail Audio - ${voicemail.callerName}")
                                                putExtra(android.content.Intent.EXTRA_TEXT, "Audio link for ${voicemail.callerName}: $audioUri")
                                            }
                                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Audio Link"))
                                        },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share Link",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
