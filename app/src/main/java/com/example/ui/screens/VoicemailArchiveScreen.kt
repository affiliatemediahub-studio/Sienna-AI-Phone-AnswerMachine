package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
import com.example.ui.theme.BentoCardBorderFocused
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.CategoryRecruiter
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.SiennaPrimaryDark
import com.example.ui.theme.SiennaPrimaryLight
import com.example.ui.theme.TextHighlight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.DateRangePreset
import com.example.ui.viewmodel.SiennaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun VoicemailArchiveScreen(
    viewModel: SiennaViewModel,
    modifier: Modifier = Modifier
) {
    val searchResults by viewModel.archiveSearchResults.collectAsStateWithLifecycle()
    val allVoicemails by viewModel.allVoicemails.collectAsStateWithLifecycle()
    val archiveFilter by viewModel.archiveFilter.collectAsStateWithLifecycle()
    val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()
    val playingId by viewModel.playingVoicemailId.collectAsStateWithLifecycle()
    val progress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    val contacts by viewModel.allContacts.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var isFilterPanelExpanded by remember { mutableStateOf(false) }
    var showCustomDateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Header & Archive Vault Stats
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF2E1065), Color(0xFF090D16))
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
                                        .background(SiennaAccent, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "INTELLIGENCE VAULT • SEARCH & ARCHIVE",
                                    color = SiennaAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.8f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Audiotrack,
                                        contentDescription = null,
                                        tint = SiennaPrimaryLight,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${allVoicemails.size} Audio Records",
                                        color = TextHighlight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Searchable Voicemail Archive",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = "Query transcribed dialogues, filter by dates and caller numbers, or inspect original audio recordings.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }

        // 2. Primary Search Input with Filter Tray Toggle
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = archiveFilter.keyword,
                        onValueChange = { viewModel.setArchiveKeyword(it) },
                        placeholder = {
                            Text(
                                "Search keywords, transcripts, companies...",
                                color = TextTertiary,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (archiveFilter.keyword.isNotBlank()) SiennaAccent else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (archiveFilter.keyword.isNotBlank()) {
                                IconButton(onClick = { viewModel.setArchiveKeyword("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
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

                    // Filter Tray Toggle Button with Badge
                    Surface(
                        onClick = { isFilterPanelExpanded = !isFilterPanelExpanded },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isFilterPanelExpanded || activeFilterCount > 0) SiennaPrimaryDark else BentoSurface,
                        border = BorderStroke(
                            1.dp,
                            if (activeFilterCount > 0) SiennaAccent else BentoCardBorder
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (activeFilterCount > 0) {
                                BadgedBox(badge = {
                                    Badge(
                                        containerColor = SiennaAccent,
                                        contentColor = Color(0xFF090D16)
                                    ) {
                                        Text(activeFilterCount.toString(), fontWeight = FontWeight.Bold)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = "Filter Options",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filter Options",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (isFilterPanelExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = if (activeFilterCount > 0) Color.White else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Quick Date Range Chips Row (Always visible for fast date navigation)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Text(
                            text = "Date:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }

                    items(DateRangePreset.values()) { preset ->
                        val isSelected = archiveFilter.dateRangePreset == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (preset == DateRangePreset.CUSTOM) {
                                    showCustomDateDialog = true
                                } else {
                                    viewModel.setDateRangePreset(preset)
                                }
                            },
                            label = {
                                Text(
                                    preset.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (preset == DateRangePreset.CUSTOM) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            } else null,
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

        // 3. Collapsible Advanced Filter Drawer Panel
        item {
            AnimatedVisibility(
                visible = isFilterPanelExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BentoSurface,
                    border = BorderStroke(1.dp, BentoCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = null,
                                    tint = SiennaAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Archive Search Filters",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            if (activeFilterCount > 0) {
                                TextButton(
                                    onClick = { viewModel.resetArchiveFilters() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF87171))
                                ) {
                                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        HorizontalDivider(color = BentoCardBorder)

                        // 3A. Filter by Caller Name or Number
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "FILTER BY CALLER NAME OR PHONE NUMBER",
                                color = SiennaPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )

                            OutlinedTextField(
                                value = archiveFilter.callerQuery,
                                onValueChange = { viewModel.setArchiveCallerQuery(it) },
                                placeholder = { Text("e.g. Sarah, (415), Marcus...", color = TextTertiary, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoBackground,
                                    unfocusedContainerColor = BentoBackground,
                                    focusedBorderColor = SiennaPrimary,
                                    unfocusedBorderColor = BentoCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Quick Caller Chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(contacts) { contact ->
                                    val isSelected = archiveFilter.selectedCallerChip == contact.name
                                    Surface(
                                        onClick = {
                                            if (isSelected) viewModel.setSelectedCallerChip(null)
                                            else viewModel.setSelectedCallerChip(contact.name)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) SiennaPrimary else BentoBackground,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) SiennaPrimaryLight else BentoCardBorder
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = contact.name,
                                                color = if (isSelected) Color.White else TextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = BentoCardBorder)

                        // 3B. Category Filter
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "CATEGORY CLASSIFICATION",
                                color = SiennaPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )

                            val categoryOptions = listOf(
                                "ALL" to "All Categories",
                                "RECRUITER" to "💼 Recruiters",
                                "FRIEND_FAMILY" to "❤️ Friends & Family",
                                "SPAM" to "🚫 Spam Blocked",
                                "BUSINESS" to "🏢 Business",
                                "GENERAL" to "💬 General"
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(categoryOptions) { (catKey, catLabel) ->
                                    val isSelected = archiveFilter.categoryFilter == catKey
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setArchiveCategory(catKey) },
                                        label = { Text(catLabel, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SiennaPrimary,
                                            selectedLabelColor = Color.White,
                                            containerColor = BentoBackground,
                                            labelColor = TextSecondary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = BentoCardBorder,
                                            selectedBorderColor = SiennaPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = BentoCardBorder)

                        // 3C. Sentiment & Urgency Filters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Sentiment
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "AI SENTIMENT",
                                    color = SiennaPrimaryLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val sentiments = listOf("ALL", "POSITIVE", "NEUTRAL", "NEGATIVE")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(sentiments) { sent ->
                                        val isSelected = archiveFilter.sentimentFilter == sent
                                        val display = when (sent) {
                                            "ALL" -> "All"
                                            "POSITIVE" -> "✨ Pos"
                                            "NEUTRAL" -> "😐 Neu"
                                            "NEGATIVE" -> "⚠️ Neg"
                                            else -> sent
                                        }
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.setArchiveSentiment(sent) },
                                            label = { Text(display, fontSize = 10.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = SiennaPrimary,
                                                selectedLabelColor = Color.White,
                                                containerColor = BentoBackground,
                                                labelColor = TextSecondary
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = true,
                                                selected = isSelected,
                                                borderColor = BentoCardBorder,
                                                selectedBorderColor = SiennaPrimary
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }

                            // Starred Only
                            Column(
                                modifier = Modifier.weight(0.8f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "STARRED",
                                    color = SiennaPrimaryLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                FilterChip(
                                    selected = archiveFilter.starredOnly,
                                    onClick = { viewModel.toggleArchiveStarredOnly() },
                                    label = { Text("⭐ Starred Only", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFB45309),
                                        selectedLabelColor = Color.White,
                                        containerColor = BentoBackground,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = archiveFilter.starredOnly,
                                        borderColor = BentoCardBorder,
                                        selectedBorderColor = Color(0xFFFBBF24)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Archive Search Metrics Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Archive Results (${searchResults.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    if (activeFilterCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SiennaPrimary.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "$activeFilterCount filters active",
                                color = SiennaAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Showing of ${allVoicemails.size} archived",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // 5. Search Results Cards
        if (searchResults.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BentoSurface,
                    border = BorderStroke(1.dp, BentoCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Archived Voicemails Match Search",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try adjusting your keyword query, widening your date range, or resetting caller filters.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        if (activeFilterCount > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.resetArchiveFilters() },
                                colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Clear All Filters")
                            }
                        }
                    }
                }
            }
        } else {
            items(searchResults, key = { it.id }) { voicemail ->
                val isPlaying = playingId == voicemail.id
                val dateStr = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(voicemail.timestamp))

                ArchiveResultCard(
                    voicemail = voicemail,
                    searchKeyword = archiveFilter.keyword,
                    isPlaying = isPlaying,
                    progress = if (isPlaying) progress else 0f,
                    onTogglePlay = { viewModel.togglePlayVoicemail(voicemail) },
                    onToggleStar = { viewModel.toggleStarred(voicemail) },
                    onOpenDetail = { viewModel.selectVoicemail(voicemail) },
                    onCopyAudioLink = {
                        val linkToCopy = voicemail.audioFileUrl.ifBlank { voicemail.audioFilePath }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Sienna Voicemail Audio Link", linkToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Audio file link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    onShareAudioLink = {
                        val linkToShare = voicemail.audioFileUrl.ifBlank { voicemail.audioFilePath }
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Sienna Voicemail Audio - ${voicemail.callerName}")
                            putExtra(Intent.EXTRA_TEXT, "Voicemail from ${voicemail.callerName} (${voicemail.callerNumber})\nAudio Link: $linkToShare\n\nSummary: ${voicemail.summary}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Original Audio Link"))
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Custom Date Range Dialog Modal
    if (showCustomDateDialog) {
        CustomDateRangeModal(
            currentStart = archiveFilter.customStartDate,
            currentEnd = archiveFilter.customEndDate,
            onDismiss = { showCustomDateDialog = false },
            onApply = { startMillis, endMillis ->
                viewModel.setCustomDateRange(startMillis, endMillis)
                showCustomDateDialog = false
            }
        )
    }
}

@Composable
fun ArchiveResultCard(
    voicemail: VoicemailEntity,
    searchKeyword: String,
    isPlaying: Boolean,
    progress: Float,
    onTogglePlay: () -> Unit,
    onToggleStar: () -> Unit,
    onOpenDetail: () -> Unit,
    onCopyAudioLink: () -> Unit,
    onShareAudioLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTranscriptExpanded by remember { mutableStateOf(false) }
    val dateFormatted = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(voicemail.timestamp))

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetail),
        shape = RoundedCornerShape(18.dp),
        color = BentoSurface,
        border = BorderStroke(1.dp, if (isPlaying) SiennaPrimary else BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Caller Top Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = when (voicemail.category) {
                                    "RECRUITER" -> CategoryRecruiter.copy(alpha = 0.25f)
                                    "FRIEND_FAMILY" -> SentimentPositive.copy(alpha = 0.25f)
                                    else -> SiennaPrimary.copy(alpha = 0.25f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = voicemail.callerName.take(1).uppercase(),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = voicemail.callerName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            if (voicemail.recruiterCompany != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CategoryRecruiter.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = voicemail.recruiterCompany,
                                        color = CategoryRecruiter,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = voicemail.callerNumber,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "•", color = TextTertiary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = dateFormatted,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Actions: Star & Detail
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (voicemail.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (voicemail.isStarred) Color(0xFFFBBF24) else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenDetail,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "View Details",
                            tint = SiennaAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Badges Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = voicemail.category)
                SentimentBadge(sentiment = voicemail.sentiment)
                UrgencyBadge(urgency = voicemail.urgencyLevel)
            }

            // 3. Relevant Transcription Snippet (with keyword highlighting)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BentoBackground,
                border = BorderStroke(1.dp, BentoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TRANSCRIBED DIALOGUE",
                            color = SiennaAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        TextButton(
                            onClick = { isTranscriptExpanded = !isTranscriptExpanded },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(
                                text = if (isTranscriptExpanded) "Collapse" else "Full Transcript",
                                fontSize = 11.sp,
                                color = SiennaPrimaryLight,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = if (isTranscriptExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = SiennaPrimaryLight,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val displayText = if (isTranscriptExpanded) voicemail.transcript else {
                        // If searching, try to show context around matched keyword
                        if (searchKeyword.isNotBlank() && voicemail.transcript.contains(searchKeyword, ignoreCase = true)) {
                            getSurroundingSnippet(voicemail.transcript, searchKeyword, maxLength = 180)
                        } else {
                            voicemail.transcript.take(180) + if (voicemail.transcript.length > 180) "..." else ""
                        }
                    }

                    Text(
                        text = buildHighlightedString(displayText, searchKeyword),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // 4. Executive Summary Snippet
            if (voicemail.summary.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Summary: ",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = buildHighlightedString(voicemail.summary, searchKeyword),
                        color = TextHighlight,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // 5. Dedicated Link to Original Audio File & In-line Waveform Player
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, BentoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Audio URI / File Link Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Audio Link",
                                tint = SiennaAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val displayAudioUri = voicemail.audioFileUrl.ifBlank { voicemail.audioFilePath }
                            Text(
                                text = displayAudioUri,
                                color = SiennaAccent,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onCopyAudioLink,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Audio Link",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            IconButton(
                                onClick = onShareAudioLink,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Audio Link",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Audio Waveform and Play/Pause Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = onTogglePlay,
                            modifier = Modifier
                                .size(36.dp)
                                .background(SiennaPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause Audio" else "Play Audio",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isPlaying) "Streaming Original Audio..." else "Original Recording (${voicemail.durationSeconds}s)",
                                    color = if (isPlaying) SiennaPrimaryLight else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    color = SiennaAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            AudioWaveformVisualizer(
                                modifier = Modifier.fillMaxWidth(),
                                waveformCsv = voicemail.audioWaveform,
                                isPlaying = isPlaying,
                                progress = progress,
                                maxHeight = 24.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDateRangeModal(
    currentStart: Long?,
    currentEnd: Long?,
    onDismiss: () -> Unit,
    onApply: (startMillis: Long?, endMillis: Long?) -> Unit
) {
    val now = System.currentTimeMillis()
    val dayMillis = 24 * 3600 * 1000L

    var selectedDaysBack by remember { mutableStateOf(7) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = SiennaAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Custom Date Range", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Filter archived voicemails within specific historical intervals:",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                val quickIntervals = listOf(
                    3 to "Past 3 Days",
                    7 to "Past 7 Days",
                    14 to "Past 14 Days",
                    30 to "Past 30 Days",
                    60 to "Past 60 Days",
                    90 to "Past 90 Days"
                )

                quickIntervals.forEach { (days, label) ->
                    val isSelected = selectedDaysBack == days
                    Surface(
                        onClick = { selectedDaysBack = days },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SiennaPrimary else BentoBackground,
                        border = BorderStroke(1.dp, if (isSelected) SiennaPrimaryLight else BentoCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                            val fromDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(now - days * dayMillis))
                            val toDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(now))
                            Text(
                                text = "$fromDate - $toDate",
                                color = if (isSelected) TextHighlight else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val start = now - (selectedDaysBack * dayMillis)
                    val end = now
                    onApply(start, end)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary)
            ) {
                Text("Apply Date Filter")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Builds an AnnotatedString that highlights occurrences of [keyword] with a high-contrast background and accent color.
 */
fun buildHighlightedString(fullText: String, keyword: String): AnnotatedString {
    if (keyword.isBlank()) {
        return AnnotatedString(fullText)
    }

    val trimmed = keyword.trim()
    return buildAnnotatedString {
        var currentIndex = 0
        val lowerText = fullText.lowercase(Locale.getDefault())
        val lowerKeyword = trimmed.lowercase(Locale.getDefault())

        while (currentIndex < fullText.length) {
            val matchIndex = lowerText.indexOf(lowerKeyword, currentIndex)
            if (matchIndex == -1) {
                append(fullText.substring(currentIndex))
                break
            }

            // Append text before the match
            if (matchIndex > currentIndex) {
                append(fullText.substring(currentIndex, matchIndex))
            }

            // Append highlighted match
            val matchEnd = matchIndex + trimmed.length
            val matchedSubstring = fullText.substring(matchIndex, matchEnd)

            pushStyle(
                SpanStyle(
                    background = SiennaPrimary.copy(alpha = 0.45f),
                    color = Color(0xFF67E8F9), // Bright Cyan highlight
                    fontWeight = FontWeight.Bold
                )
            )
            append(matchedSubstring)
            pop()

            currentIndex = matchEnd
        }
    }
}

/**
 * Extracts a surrounding snippet of length around the first occurrence of the keyword.
 */
fun getSurroundingSnippet(fullText: String, keyword: String, maxLength: Int = 180): String {
    val index = fullText.indexOf(keyword, ignoreCase = true)
    if (index == -1) return fullText.take(maxLength) + if (fullText.length > maxLength) "..." else ""

    val start = (index - 40).coerceAtLeast(0)
    val end = (index + keyword.length + 100).coerceAtMost(fullText.length)

    val prefix = if (start > 0) "..." else ""
    val suffix = if (end < fullText.length) "..." else ""

    return prefix + fullText.substring(start, end).trim() + suffix
}
