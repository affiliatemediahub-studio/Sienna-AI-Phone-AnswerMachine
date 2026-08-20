package com.example.ui.screens

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ContactEntity
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.BentoCard
import com.example.ui.components.CategoryChip
import com.example.ui.components.SophisticatedAnswerMachine
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
import com.example.ui.theme.TextHighlight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.CallState
import com.example.ui.viewmodel.SiennaViewModel

@Composable
fun LiveCallScreen(
    viewModel: SiennaViewModel,
    onViewTracker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val callState by viewModel.callState.collectAsStateWithLifecycle()
    val activeCaller by viewModel.activeCaller.collectAsStateWithLifecycle()
    val callerNumber by viewModel.customCallerNumber.collectAsStateWithLifecycle()
    val conversation by viewModel.liveConversation.collectAsStateWithLifecycle()
    val callDuration by viewModel.liveCallDuration.collectAsStateWithLifecycle()
    val contacts by viewModel.allContacts.collectAsStateWithLifecycle()
    val activeGreeting by viewModel.activeGreeting.collectAsStateWithLifecycle()
    val liveStreamingTranscription by viewModel.liveStreamingTranscription.collectAsStateWithLifecycle()
    val liveDetectedEntities by viewModel.liveDetectedEntities.collectAsStateWithLifecycle()
    val isTranscribingStream by viewModel.isTranscribingStream.collectAsStateWithLifecycle()

    var customCallerInput by remember { mutableStateOf("") }
    var activeWhisper by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(conversation.size) {
        if (conversation.isNotEmpty()) {
            listState.animateScrollToItem(conversation.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        when (callState) {
            CallState.IDLE -> {
                IdleAnswerMachineStudio(
                    contacts = contacts,
                    activeGreeting = activeGreeting,
                    onStartCall = { contact, number ->
                        viewModel.startIncomingCall(contact, number)
                    }
                )
            }

            CallState.INCOMING_RINGING -> {
                SophisticatedRingingConsole(
                    callerName = activeCaller?.name ?: "Unknown Caller",
                    callerNumber = callerNumber,
                    callerCategory = activeCaller?.category ?: "UNKNOWN",
                    activeGreeting = activeGreeting,
                    onAnswer = { viewModel.answerCall() },
                    onDecline = { viewModel.resetCallState() }
                )
            }

            CallState.CONNECTED, CallState.SIENNA_SPEAKING, CallState.CALLER_SPEAKING -> {
                ActiveScreeningDeck(
                    callerName = activeCaller?.name ?: "Unknown Caller",
                    callerNumber = callerNumber,
                    callerCategory = activeCaller?.category ?: "UNKNOWN",
                    activeGreeting = activeGreeting,
                    callDuration = callDuration,
                    callState = callState,
                    conversation = conversation,
                    listState = listState,
                    customCallerInput = customCallerInput,
                    activeWhisper = activeWhisper,
                    streamingTranscript = liveStreamingTranscription,
                    detectedEntities = liveDetectedEntities,
                    isTranscribing = isTranscribingStream,
                    onCustomInputChange = { customCallerInput = it },
                    onSendCallerSpeech = {
                        viewModel.callerSpeak(it)
                        customCallerInput = ""
                    },
                    onSendWhisper = { whisper ->
                        activeWhisper = whisper
                        viewModel.callerSpeak("[John's Live Whisper to Sienna: $whisper]")
                    },
                    onTakeOver = {
                        viewModel.callerSpeak("[John took over the call live]")
                    },
                    onEndCall = { viewModel.endAndScreenCall() }
                )
            }

            CallState.ANALYZING -> {
                SophisticatedAnalyzingConsole()
            }

            CallState.CALL_ENDED -> {
                SophisticatedCallDispatchedSummary(
                    callerName = activeCaller?.name ?: "Caller",
                    onViewTracker = onViewTracker,
                    onStartNew = { viewModel.resetCallState() }
                )
            }
        }
    }
}

/**
 * Idle state displaying the Sophisticated Answering Machine and Scenario Simulator
 */
@Composable
private fun IdleAnswerMachineStudio(
    contacts: List<ContactEntity>,
    activeGreeting: com.example.data.model.GreetingEntity?,
    onStartCall: (ContactEntity?, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Sophisticated Answering Machine Deck (Armed & Ready)
        item {
            SophisticatedAnswerMachine(
                isCallActive = false,
                callerName = "John Lanter's Line",
                callerNumber = "+1 (555) 019-4820",
                callerCategory = "AI ASSISTANT",
                activeGreeting = activeGreeting,
                callDurationSeconds = 0,
                onAnswerCall = {
                    val defaultContact = contacts.firstOrNull { it.category == "RECRUITER" }
                    onStartCall(defaultContact, defaultContact?.phoneNumber ?: "+1 (415) 890-2341")
                }
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Simulate Incoming Call to John",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BentoSurfaceVariant
                ) {
                    Text(
                        text = "4 Scenarios Ready",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Scenario 1: Recruiter
        item {
            val recruiter = contacts.firstOrNull { it.category == "RECRUITER" }
            ScenarioPresetBentoCard(
                icon = Icons.Default.Business,
                iconTint = CategoryRecruiter,
                badge = "Recruiter Screening",
                badgeColor = CategoryRecruiter,
                title = "Sarah Jenkins • Talent Lead",
                company = "Apex AI Systems (Staff Platform Engineer role)",
                number = recruiter?.phoneNumber ?: "+1 (415) 890-2341",
                scenarioDescription = "Sienna gathers compensation ($240k+), team size, role details, and schedules follow-up.",
                onCall = { onStartCall(recruiter, recruiter?.phoneNumber ?: "+1 (415) 890-2341") }
            )
        }

        // Scenario 2: Family
        item {
            val family = contacts.firstOrNull { it.category == "FRIEND_FAMILY" }
            ScenarioPresetBentoCard(
                icon = Icons.Default.Person,
                iconTint = CategoryFriendFamily,
                badge = "Personal & Family",
                badgeColor = CategoryFriendFamily,
                title = "Eleanor Lanter • Mom",
                company = "Personal Family Line",
                number = family?.phoneNumber ?: "+1 (512) 441-9082",
                scenarioDescription = "Sienna plays a warm personal greeting, records her message about Sunday dinner, and marks high priority.",
                onCall = { onStartCall(family, family?.phoneNumber ?: "+1 (512) 441-9082") }
            )
        }

        // Scenario 3: Telemarketer / Spam
        item {
            val spam = contacts.firstOrNull { it.category == "SPAM" }
            ScenarioPresetBentoCard(
                icon = Icons.Default.Security,
                iconTint = CategorySpam,
                badge = "Spam Defense Shield",
                badgeColor = CategorySpam,
                title = "National Vehicle Protection",
                company = "Robocall Warranty Solicitation",
                number = spam?.phoneNumber ?: "+1 (800) 993-4120",
                scenarioDescription = "Sienna prompts for caller verification and terminates the robocall without disturbing John.",
                onCall = { onStartCall(spam, spam?.phoneNumber ?: "+1 (800) 993-4120") }
            )
        }

        // Scenario 4: Unknown Executive Contact
        item {
            ScenarioPresetBentoCard(
                icon = Icons.Default.Star,
                iconTint = SiennaAccent,
                badge = "Executive Inquiry",
                badgeColor = SiennaAccent,
                title = "Marcus Vance • Managing Director",
                company = "Vance Horizon Capital",
                number = "+1 (650) 902-1144",
                scenarioDescription = "Sienna prompts for identity, confirms meeting intent, and instantly pings John's Call Tracker.",
                onCall = { onStartCall(null, "+1 (650) 902-1144") }
            )
        }
    }
}

@Composable
private fun ScenarioPresetBentoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    badge: String,
    badgeColor: Color,
    title: String,
    company: String,
    number: String,
    scenarioDescription: String,
    onCall: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BentoSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
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
                            .size(36.dp)
                            .background(iconTint.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$company • $number",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BentoBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = scenarioDescription,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate Call to John's Sienna Line", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Incoming Ringing Screen with Sophisticated Call Screening UI
 */
@Composable
private fun SophisticatedRingingConsole(
    callerName: String,
    callerNumber: String,
    callerCategory: String,
    activeGreeting: com.example.data.model.GreetingEntity?,
    onAnswer: () -> Unit,
    onDecline: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ringPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SiennaPrimary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(SiennaAccent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INCOMING CALL • SIENNA SCREENING READY",
                        color = SiennaAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = callerName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            )

            Text(
                text = callerNumber,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(10.dp))
            CategoryChip(category = callerCategory)
        }

        // Center Pulsing Answering Machine Node
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .scale(pulseScale)
                    .background(SiennaPrimary.copy(alpha = 0.15f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale * 0.9f)
                    .background(SiennaAccent.copy(alpha = 0.2f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        Brush.radialGradient(listOf(SiennaPrimaryLight, SiennaPrimaryDark)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BentoSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = SiennaAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (activeGreeting != null) "Loaded Greeting: ${activeGreeting.title}" else "Loaded: Default Sienna Screening Script",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Decline
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .size(60.dp)
                            .background(SentimentNegative, CircleShape)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Decline", color = TextSecondary, fontSize = 11.sp)
                }

                // Answer with Sienna
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onAnswer,
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.linearGradient(listOf(SentimentPositive, Color(0xFF059669))),
                                CircleShape
                            )
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = "Answer with Sienna", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Screen with Sienna", color = SentimentPositive, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Active screening screen featuring Sophisticated Answering Machine deck,
 * real-time transcript streaming, entity highlight chips, and whisper controls.
 */
@Composable
private fun ActiveScreeningDeck(
    callerName: String,
    callerNumber: String,
    callerCategory: String,
    activeGreeting: com.example.data.model.GreetingEntity?,
    callDuration: Int,
    callState: CallState,
    conversation: List<com.example.ai.LiveTurn>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    customCallerInput: String,
    activeWhisper: String?,
    streamingTranscript: String = "",
    detectedEntities: List<com.example.ai.DetectedEntity> = emptyList(),
    isTranscribing: Boolean = false,
    onCustomInputChange: (String) -> Unit,
    onSendCallerSpeech: (String) -> Unit,
    onSendWhisper: (String) -> Unit,
    onTakeOver: () -> Unit,
    onEndCall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Sophisticated Answering Machine Tape Deck
        SophisticatedAnswerMachine(
            isCallActive = true,
            callerName = callerName,
            callerNumber = callerNumber,
            callerCategory = callerCategory,
            activeGreeting = activeGreeting,
            callDurationSeconds = callDuration,
            isSiennaSpeaking = callState == CallState.SIENNA_SPEAKING,
            isTranscribing = isTranscribing || callState == CallState.CALLER_SPEAKING,
            streamingTranscript = streamingTranscript,
            detectedEntities = detectedEntities,
            activeWhisper = activeWhisper,
            onTakeOverCall = onTakeOver,
            onDeclineCall = onEndCall,
            onSendWhisper = onSendWhisper
        )

        // 2. Real-Time Conversation Stream
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = BentoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (isTranscribing) SentimentNegative else SiennaAccent, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTranscribing) "STREAMING AUDIO TRANSCRIPT..." else "LIVE SPEECH TRANSCRIPT",
                            color = if (isTranscribing) SentimentNegative else SiennaAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "${conversation.size} turns",
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(conversation) { turn ->
                        val isSienna = turn.speaker == "Sienna"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isSienna) Arrangement.Start else Arrangement.End
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isSienna) 2.dp else 14.dp,
                                    bottomEnd = if (isSienna) 14.dp else 2.dp
                                ),
                                color = if (isSienna) Color(0xFF261D5A) else BentoSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSienna) SiennaPrimary.copy(alpha = 0.6f) else BentoCardBorder
                                ),
                                modifier = Modifier.fillMaxWidth(0.88f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isSienna) "🤖 Sienna AI (John's Assistant)" else "👤 $callerName",
                                            color = if (isSienna) SiennaAccent else TextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "LIVE",
                                            color = if (isSienna) SiennaPrimaryLight else TextTertiary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = turn.text,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Live active streaming speech bubble when caller is speaking
                    if (isTranscribing && streamingTranscript.isNotBlank()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 14.dp, bottomEnd = 2.dp),
                                    color = BentoSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.6f)),
                                    modifier = Modifier.fillMaxWidth(0.88f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "👤 $callerName (Transcribing live...)",
                                                color = SiennaAccent,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(10.dp),
                                                color = SiennaAccent,
                                                strokeWidth = 1.5.dp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$streamingTranscript ▍",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Caller Quick Dialogue Simulation Tiles
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Simulate Caller Dialogue:",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )

            val callerPresets = listOf(
                "Hi! Sarah from Apex AI. Staff Role ($240k-$280k). Can John talk this Friday?",
                "Hey honey, Eleanor here! Reminding you about Sunday family dinner at 6 PM.",
                "Vehicle warranty division calling regarding your coverage expiration.",
                "Hey John, Dave here regarding our tennis match this weekend!"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(callerPresets) { preset ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BentoSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
                        modifier = Modifier
                            .width(240.dp)
                            .clickable { onSendCallerSpeech(preset) }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = preset,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text("Tap to speak ➔", fontSize = 9.sp, color = SiennaAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 4. Custom Speech Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customCallerInput,
                onValueChange = onCustomInputChange,
                placeholder = { Text("Custom caller speech...", color = TextTertiary, fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BentoSurface,
                    unfocusedContainerColor = BentoSurface,
                    focusedBorderColor = SiennaPrimary,
                    unfocusedBorderColor = BentoCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                trailingIcon = {
                    if (customCallerInput.isNotBlank()) {
                        IconButton(onClick = { onSendCallerSpeech(customCallerInput) }) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = SiennaAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onEndCall,
                colors = ButtonDefaults.buttonColors(containerColor = SentimentNegative),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("End Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Analyzing screen during AI processing
 */
@Composable
private fun SophisticatedAnalyzingConsole() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = SiennaAccent,
            modifier = Modifier.size(54.dp),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sienna Neural Engine Processing...",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Analyzing caller intent, extracting key entities, structuring voicemail transcription, and notifying 'John's Call Tracker'.",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

/**
 * Summary screen when call finishes
 */
@Composable
private fun SophisticatedCallDispatchedSummary(
    callerName: String,
    onViewTracker: () -> Unit,
    onStartNew: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(SentimentPositive.copy(alpha = 0.15f), CircleShape)
                .border(1.dp, SentimentPositive, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SentimentPositive,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Call Screened & Dispatched!",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Voicemail and live screening transcript for $callerName have been processed and pushed directly to 'John's Call Tracker'.",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onViewTracker,
                colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Call Tracker Feed", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onStartNew,
                colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text("Simulate Another", fontSize = 12.sp, color = TextPrimary)
            }
        }
    }
}
