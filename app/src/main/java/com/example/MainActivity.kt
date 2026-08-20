package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.TrackerNotificationBanner
import com.example.ui.components.VoicemailDetailBottomSheet
import com.example.ui.screens.CallTrackerFeedScreen
import com.example.ui.screens.ContactDirectoryScreen
import com.example.ui.screens.DashboardInboxScreen
import com.example.ui.screens.GreetingStudioScreen
import com.example.ui.screens.LiveCallScreen
import com.example.ui.screens.VoicemailArchiveScreen
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.SiennaPrimaryLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SiennaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SiennaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: SiennaViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val activeAlert by viewModel.activeAlert.collectAsStateWithLifecycle()
    val selectedVoicemail by viewModel.selectedVoicemail.collectAsStateWithLifecycle()
    val playingId by viewModel.playingVoicemailId.collectAsStateWithLifecycle()
    val progress by viewModel.playbackProgress.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem("Inbox", Icons.Filled.Inbox, Icons.Outlined.Inbox, hasBadge = unreadCount > 0),
        NavItem("Archive", Icons.Filled.Search, Icons.Outlined.Search),
        NavItem("Live Call", Icons.Filled.PhoneInTalk, Icons.Outlined.PhoneInTalk),
        NavItem("Tracker", Icons.Filled.Chat, Icons.Outlined.Chat),
        NavItem("Greetings", Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq),
        NavItem("Contacts", Icons.Filled.Contacts, Icons.Outlined.Contacts)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BentoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Brush.linearGradient(listOf(SiennaPrimary, SiennaAccent)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Sienna AI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SentimentPositive, CircleShape)
                                )
                            }
                            Text(
                                text = "Assistant for John Lanter",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E1B4B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = SiennaAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tracker: Synced",
                                color = SiennaAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BentoSurface,
                tonalElevation = 0.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            if (item.hasBadge) {
                                BadgedBox(badge = { Badge { Text(unreadCount.toString()) } }) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = SiennaPrimaryLight,
                            indicatorColor = SiennaPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Tab Content
            when (selectedTab) {
                0 -> DashboardInboxScreen(
                    viewModel = viewModel,
                    onNavigateToSimulator = { selectedTab = 2 },
                    onNavigateToTracker = { selectedTab = 3 },
                    onNavigateToArchive = { selectedTab = 1 }
                )
                1 -> VoicemailArchiveScreen(
                    viewModel = viewModel
                )
                2 -> LiveCallScreen(
                    viewModel = viewModel,
                    onViewTracker = { selectedTab = 3 }
                )
                3 -> CallTrackerFeedScreen(
                    viewModel = viewModel
                )
                4 -> GreetingStudioScreen(
                    viewModel = viewModel,
                    onNavigateToSimulator = { selectedTab = 2 }
                )
                5 -> ContactDirectoryScreen(
                    viewModel = viewModel,
                    onSimulateCall = { contact ->
                        viewModel.startIncomingCall(contact)
                        selectedTab = 2
                    }
                )
            }

            // Top Floating Notification Banner for instant 'John's Call Tracker' alerts
            TrackerNotificationBanner(
                alert = activeAlert,
                onDismiss = { viewModel.dismissAlert() },
                onClick = {
                    viewModel.dismissAlert()
                    selectedTab = 2
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Voicemail Detail Sheet
            if (selectedVoicemail != null) {
                VoicemailDetailBottomSheet(
                    voicemail = selectedVoicemail!!,
                    isPlaying = playingId == selectedVoicemail!!.id,
                    progress = if (playingId == selectedVoicemail!!.id) progress else 0f,
                    onDismiss = { viewModel.selectVoicemail(null) },
                    onTogglePlay = { viewModel.togglePlayVoicemail(selectedVoicemail!!) },
                    onToggleStar = { viewModel.toggleStarred(selectedVoicemail!!) },
                    onDelete = {
                        viewModel.deleteVoicemail(selectedVoicemail!!.id)
                        viewModel.selectVoicemail(null)
                    }
                )
            }
        }
    }
}

data class NavItem(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val hasBadge: Boolean = false
)
