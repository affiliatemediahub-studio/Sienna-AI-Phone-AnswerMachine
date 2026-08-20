package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.RecordedAudioResult
import com.example.audio.RecordingState
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
import com.example.ui.theme.SentimentNeutral
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.SiennaPrimaryDark
import com.example.ui.theme.SiennaPrimaryLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.SiennaViewModel

enum class GreetingCreationMode(val label: String, val icon: ImageVector) {
    RECORD("Record Voice", Icons.Default.Mic),
    UPLOAD("Upload Audio", Icons.Default.UploadFile),
    TEXT_TTS("AI Speech Script", Icons.Default.SmartToy)
}

@Composable
fun GreetingManager(
    viewModel: SiennaViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSimulator: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val greetings by viewModel.allGreetings.collectAsStateWithLifecycle()
    val contacts by viewModel.allContacts.collectAsStateWithLifecycle()

    var isCreatorExpanded by remember { mutableStateOf(true) }
    var selectedCreationMode by remember { mutableStateOf(GreetingCreationMode.RECORD) }
    var libraryFilter by remember { mutableStateOf("ALL") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Hero / Control Header ---
        BentoCard(
            title = "Custom Voice Greeting Studio",
            subtitle = "Record personal voice notes or upload audio files for specific callers",
            icon = Icons.Default.GraphicEq,
            iconTint = SiennaAccent,
            badge = {
                Button(
                    onClick = { isCreatorExpanded = !isCreatorExpanded },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCreatorExpanded) BentoSurfaceVariant else SiennaPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isCreatorExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCreatorExpanded) "Hide Studio" else "New Greeting",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Assign distinct greetings to Recruiters, Family, VIP Clients, or specific contacts. Stored securely in your local Room database.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                // Quick stats pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BentoBackground, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val customVoiceCount = greetings.count { it.isCustomAudio }
                    val activeCount = greetings.count { it.isActive }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "🎙️ $customVoiceCount Custom Voice Recordings",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "⚡ $activeCount Active Rules",
                            color = SentimentPositive,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (onNavigateToSimulator != null) {
                        Text(
                            text = "Test Call →",
                            color = SiennaAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToSimulator() }
                        )
                    }
                }
            }
        }

        // --- 2. Interactive Greeting Creator & Recorder Component ---
        AnimatedVisibility(
            visible = isCreatorExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            GreetingCreatorCard(
                viewModel = viewModel,
                contacts = contacts,
                selectedMode = selectedCreationMode,
                onModeSelected = { selectedCreationMode = it },
                onGreetingSaved = {
                    Toast.makeText(context, "Voice greeting saved to Room database!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // --- 3. Saved Greetings Library Title & Filter Chips ---
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Saved Greetings Library (${greetings.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Text(
                    text = "Room DB Synced",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            // Filter chips for library
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All (${greetings.size})",
                    "VOICE" to "🎙️ Voice Recordings",
                    "UPLOADED" to "📁 Uploaded Audio",
                    "TTS" to "🤖 Sienna AI TTS",
                    "RECRUITER" to "💼 Recruiters",
                    "FRIEND_FAMILY" to "❤️ Friends/Family",
                    "CONTACT" to "👤 Specific Contacts"
                )

                items(filters) { (key, label) ->
                    val isSelected = libraryFilter == key
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) SiennaPrimary else BentoSurface,
                        border = BorderStroke(1.dp, if (isSelected) SiennaAccent else BentoCardBorder),
                        modifier = Modifier.clickable { libraryFilter = key }
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // --- 4. Saved Greetings Bento Cards List ---
        val filteredList = greetings.filter { item ->
            when (libraryFilter) {
                "ALL" -> true
                "VOICE" -> item.isCustomAudio && item.audioSourceType == "RECORDED"
                "UPLOADED" -> item.isCustomAudio && item.audioSourceType == "UPLOADED"
                "TTS" -> !item.isCustomAudio || item.audioSourceType == "TTS"
                "RECRUITER" -> item.targetCallerType == "RECRUITER"
                "FRIEND_FAMILY" -> item.targetCallerType == "FRIEND_FAMILY"
                "CONTACT" -> item.assignedContactId != null || item.targetCallerType == "SPECIFIC_CONTACT"
                else -> true
            }
        }

        if (filteredList.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BentoSurface,
                border = BorderStroke(1.dp, BentoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MicNone,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No greetings found in this category",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Use the studio above to record or upload a new greeting.",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            filteredList.forEach { greeting ->
                GreetingLibraryCard(
                    greeting = greeting,
                    viewModel = viewModel,
                    onToggleActive = {
                        if (greeting.assignedContactId != null) {
                            viewModel.setActiveGreetingForContact(greeting.id, greeting.assignedContactId)
                        } else if (greeting.targetCallerType != "ALL") {
                            viewModel.setActiveGreetingForCallerType(greeting.id, greeting.targetCallerType)
                        } else {
                            viewModel.setActiveGreeting(greeting.id)
                        }
                    },
                    onDelete = {
                        viewModel.deleteGreeting(greeting)
                        Toast.makeText(context, "Greeting removed from database", Toast.LENGTH_SHORT).show()
                    },
                    onDuplicate = {
                        viewModel.duplicateGreeting(greeting)
                        Toast.makeText(context, "Greeting duplicated", Toast.LENGTH_SHORT).show()
                    },
                    onNavigateToSimulator = onNavigateToSimulator
                )
            }
        }
    }
}

@Composable
private fun GreetingCreatorCard(
    viewModel: SiennaViewModel,
    contacts: List<ContactEntity>,
    selectedMode: GreetingCreationMode,
    onModeSelected: (GreetingCreationMode) -> Unit,
    onGreetingSaved: () -> Unit
) {
    val context = LocalContext.current
    val audioEngine = viewModel.greetingAudioEngine

    // Creator State
    var greetingTitle by remember { mutableStateOf("") }
    var greetingScript by remember { mutableStateOf("") }
    var selectedTargetCaller by remember { mutableStateOf("ALL") } // ALL, RECRUITER, FRIEND_FAMILY, VIP, SPAM, SPECIFIC_CONTACT
    var selectedContact by remember { mutableStateOf<ContactEntity?>(null) }
    var showContactPickerDropdown by remember { mutableStateOf(false) }
    var selectedRoutingRule by remember { mutableStateOf("DEFAULT") }
    var setAsActiveByDefault by remember { mutableStateOf(true) }

    // Recorded Audio State
    val recordingState by audioEngine.recordingState.collectAsStateWithLifecycle()
    val recordingDuration by audioEngine.recordingDurationSeconds.collectAsStateWithLifecycle()
    val liveAmplitude by audioEngine.liveAmplitude.collectAsStateWithLifecycle()
    var recordedAudioResult by remember { mutableStateOf<RecordedAudioResult?>(null) }

    // Uploaded Audio State
    var uploadedAudioResult by remember { mutableStateOf<RecordedAudioResult?>(null) }

    // Permission launcher for Recording
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            audioEngine.startRecording()
        } else {
            Toast.makeText(context, "Microphone permission is required to record custom voice greetings", Toast.LENGTH_LONG).show()
        }
    }

    // Audio file picker launcher
    val audioFilePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var fileName = "custom_voice.m4a"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex) ?: fileName
                }
            }

            val result = audioEngine.importAudioFileFromUri(uri, fileName)
            if (result != null) {
                uploadedAudioResult = result
                if (greetingTitle.isBlank()) {
                    greetingTitle = "Uploaded: ${fileName.take(20)}"
                }
                Toast.makeText(context, "Audio file imported successfully (${result.durationSeconds}s)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to import audio file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BentoSurface,
        border = BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoBackground, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GreetingCreationMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SiennaPrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onModeSelected(mode) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = mode.label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Mode Content Area
            when (selectedMode) {
                GreetingCreationMode.RECORD -> {
                    VoiceRecordingStudio(
                        audioEngine = audioEngine,
                        recordingState = recordingState,
                        recordingDuration = recordingDuration,
                        liveAmplitude = liveAmplitude,
                        recordedResult = recordedAudioResult,
                        onStartRecording = {
                            if (hasMicPermission) {
                                recordedAudioResult = null
                                audioEngine.startRecording()
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onStopRecording = {
                            val result = audioEngine.stopRecording()
                            recordedAudioResult = result
                            if (greetingTitle.isBlank()) {
                                greetingTitle = "Voice Recording #${(100..999).random()}"
                            }
                            if (greetingScript.isBlank()) {
                                greetingScript = "Custom recorded voice greeting for incoming callers."
                            }
                        },
                        onDiscard = {
                            audioEngine.cancelRecording()
                            recordedAudioResult = null
                        }
                    )
                }

                GreetingCreationMode.UPLOAD -> {
                    AudioUploadStudio(
                        audioEngine = audioEngine,
                        uploadedResult = uploadedAudioResult,
                        onPickFile = { audioFilePickerLauncher.launch("audio/*") },
                        onRemove = { uploadedAudioResult = null }
                    )
                }

                GreetingCreationMode.TEXT_TTS -> {
                    TextTtsStudio(
                        greetingScript = greetingScript,
                        onScriptChange = { greetingScript = it },
                        onApplyTemplate = { title, script ->
                            greetingTitle = title
                            greetingScript = script
                        },
                        onPreviewVoice = {
                            if (greetingScript.isNotBlank()) {
                                viewModel.previewGreetingVoice(greetingScript)
                            } else {
                                viewModel.previewGreetingVoice("Hello, this is Sienna his assistant.")
                            }
                        }
                    )
                }
            }

            HorizontalDivider(color = BentoCardBorder.copy(alpha = 0.6f))

            // Greeting Details & Metadata
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Greeting Details & Configuration",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = greetingTitle,
                    onValueChange = { greetingTitle = it },
                    label = { Text("Greeting Title (e.g. 'Personal Note for Sarah', 'VIP Recruiter')") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoBackground,
                        unfocusedContainerColor = BentoBackground,
                        focusedBorderColor = SiennaAccent,
                        unfocusedBorderColor = BentoCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedMode != GreetingCreationMode.TEXT_TTS) {
                    OutlinedTextField(
                        value = greetingScript,
                        onValueChange = { greetingScript = it },
                        label = { Text("Spoken Message Transcript / Summary Notes") },
                        minLines = 2,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoBackground,
                            unfocusedContainerColor = BentoBackground,
                            focusedBorderColor = SiennaAccent,
                            unfocusedBorderColor = BentoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Target Caller Selection Section
                Text(
                    text = "Who should hear this greeting?",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    val callerCategories = listOf(
                        "ALL" to "👥 All Callers",
                        "RECRUITER" to "💼 Recruiters",
                        "FRIEND_FAMILY" to "❤️ Friends & Family",
                        "VIP" to "🏢 VIP Clients",
                        "SPAM" to "🚫 Spam Defense",
                        "SPECIFIC_CONTACT" to "👤 Specific Contact..."
                    )

                    items(callerCategories) { (type, label) ->
                        val isSelected = selectedTargetCaller == type
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SiennaPrimaryLight.copy(alpha = 0.25f) else BentoBackground,
                            border = BorderStroke(1.dp, if (isSelected) SiennaAccent else BentoCardBorder),
                            modifier = Modifier.clickable {
                                selectedTargetCaller = type
                                if (type == "SPECIFIC_CONTACT") {
                                    showContactPickerDropdown = true
                                }
                            }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) SiennaAccent else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // If Specific Contact chosen, show contact picker dropdown
                if (selectedTargetCaller == "SPECIFIC_CONTACT" || selectedContact != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BentoBackground,
                        border = BorderStroke(1.dp, SiennaAccent.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                selectedContact?.avatarColor?.let { Color(it) } ?: SiennaPrimary,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = selectedContact?.name ?: "Select a Contact from Directory",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = selectedContact?.phoneNumber ?: "${contacts.size} contacts available in Room DB",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { showContactPickerDropdown = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = if (selectedContact == null) "Choose Contact" else "Change",
                                        fontSize = 10.sp,
                                        color = SiennaAccent
                                    )
                                }
                            }

                            // Contact Dropdown Selection
                            DropdownMenu(
                                expanded = showContactPickerDropdown,
                                onDismissRequest = { showContactPickerDropdown = false },
                                modifier = Modifier.background(BentoSurface)
                            ) {
                                contacts.forEach { contact ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .background(Color(contact.avatarColor), CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(contact.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    Text("${contact.relationship} • ${contact.phoneNumber}", color = TextSecondary, fontSize = 10.sp)
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedContact = contact
                                            selectedTargetCaller = "SPECIFIC_CONTACT"
                                            showContactPickerDropdown = false
                                            if (greetingTitle.isBlank()) {
                                                greetingTitle = "Personal Greeting for ${contact.name}"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Set as Active Greeting for this Caller",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Sienna will play this whenever this caller rings",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    Switch(
                        checked = setAsActiveByDefault,
                        onCheckedChange = { setAsActiveByDefault = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SentimentPositive,
                            uncheckedTrackColor = BentoBackground
                        )
                    )
                }
            }

            // Save Action Button
            Button(
                onClick = {
                    val finalTitle = if (greetingTitle.isNotBlank()) greetingTitle else when (selectedMode) {
                        GreetingCreationMode.RECORD -> "Voice Greeting (${System.currentTimeMillis() % 1000})"
                        GreetingCreationMode.UPLOAD -> "Audio Greeting (${uploadedAudioResult?.fileName ?: "File"})"
                        GreetingCreationMode.TEXT_TTS -> "Sienna AI Script"
                    }

                    val finalMessage = if (greetingScript.isNotBlank()) greetingScript else when (selectedMode) {
                        GreetingCreationMode.RECORD -> "Custom recorded voice message."
                        GreetingCreationMode.UPLOAD -> "Uploaded custom voice audio clip."
                        GreetingCreationMode.TEXT_TTS -> "Hello, you have reached John Lanter's phone. This is Sienna."
                    }

                    val audioPath = when (selectedMode) {
                        GreetingCreationMode.RECORD -> recordedAudioResult?.filePath
                        GreetingCreationMode.UPLOAD -> uploadedAudioResult?.filePath
                        GreetingCreationMode.TEXT_TTS -> null
                    }

                    val audioName = when (selectedMode) {
                        GreetingCreationMode.RECORD -> recordedAudioResult?.fileName
                        GreetingCreationMode.UPLOAD -> uploadedAudioResult?.fileName
                        GreetingCreationMode.TEXT_TTS -> null
                    }

                    val duration = when (selectedMode) {
                        GreetingCreationMode.RECORD -> recordedAudioResult?.durationSeconds ?: 6
                        GreetingCreationMode.UPLOAD -> uploadedAudioResult?.durationSeconds ?: 8
                        GreetingCreationMode.TEXT_TTS -> 5
                    }

                    val waveform = when (selectedMode) {
                        GreetingCreationMode.RECORD -> recordedAudioResult?.waveformCsv ?: "20,40,65,80,60,40,75,90,50,30"
                        GreetingCreationMode.UPLOAD -> uploadedAudioResult?.waveformCsv ?: "25,50,75,90,70,55,80,65,40,20"
                        GreetingCreationMode.TEXT_TTS -> "15,30,50,70,85,60,45,70,80,50,30,20"
                    }

                    val isCustom = selectedMode != GreetingCreationMode.TEXT_TTS
                    val sourceType = when (selectedMode) {
                        GreetingCreationMode.RECORD -> "RECORDED"
                        GreetingCreationMode.UPLOAD -> "UPLOADED"
                        GreetingCreationMode.TEXT_TTS -> "TTS"
                    }

                    val greeting = GreetingEntity(
                        title = finalTitle,
                        messageText = finalMessage,
                        voiceType = if (isCustom) "Custom Voice Note" else "Sienna Natural",
                        isActive = setAsActiveByDefault,
                        routingRule = selectedRoutingRule,
                        audioFilePath = audioPath,
                        audioFileName = audioName,
                        isCustomAudio = isCustom,
                        audioSourceType = sourceType,
                        audioDurationSeconds = duration,
                        targetCallerType = if (selectedContact != null) "SPECIFIC_CONTACT" else selectedTargetCaller,
                        assignedContactId = selectedContact?.id,
                        assignedContactName = selectedContact?.name,
                        assignedPhoneNumber = selectedContact?.phoneNumber,
                        waveformCsv = waveform,
                        createdAt = System.currentTimeMillis()
                    )

                    viewModel.saveGreeting(greeting)
                    onGreetingSaved()

                    // Reset fields
                    greetingTitle = ""
                    greetingScript = ""
                    recordedAudioResult = null
                    uploadedAudioResult = null
                    selectedContact = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Save Greeting to Room Database",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun VoiceRecordingStudio(
    audioEngine: com.example.audio.GreetingAudioEngine,
    recordingState: RecordingState,
    recordingDuration: Int,
    liveAmplitude: Float,
    recordedResult: RecordedAudioResult?,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onDiscard: () -> Unit
) {
    val isPlaying by audioEngine.isPlaying.collectAsStateWithLifecycle()
    val playingPath by audioEngine.playingAudioPath.collectAsStateWithLifecycle()
    val playbackProgress by audioEngine.playbackProgress.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BentoBackground,
        border = BorderStroke(
            1.dp,
            if (recordingState == RecordingState.RECORDING) Color(0xFFEF4444) else BentoCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                // State A: Ready to Record
                recordingState == RecordingState.IDLE && recordedResult == null -> {
                    Text(
                        text = "Voice Recording Studio",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap the microphone to start recording your personal voice greeting.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    IconButton(
                        onClick = onStartRecording,
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.radialGradient(listOf(SiennaPrimary, SiennaPrimaryDark)),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Start Recording",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text("Tap to Record", color = SiennaAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // State B: Actively Recording
                recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .scale(if (recordingState == RecordingState.RECORDING) pulseScale else 1f)
                                    .background(Color(0xFFEF4444), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (recordingState == RecordingState.RECORDING) "RECORDING LIVE" else "RECORDING PAUSED",
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val mins = recordingDuration / 60
                        val secs = recordingDuration % 60
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Live Waveform Visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(BentoSurfaceVariant, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val barCount = 20
                        (0 until barCount).forEach { i ->
                            val heightRatio = (liveAmplitude * (0.4f + (i % 5) * 0.15f)).coerceIn(0.1f, 1f)
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height((36 * heightRatio).dp)
                                    .background(
                                        if (recordingState == RecordingState.RECORDING) SiennaAccent else TextTertiary,
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    // Recording Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDiscard,
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Discard", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = onStopRecording,
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFEF4444), CircleShape)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(26.dp))
                        }

                        OutlinedButton(
                            onClick = {
                                if (recordingState == RecordingState.RECORDING) audioEngine.pauseRecording()
                                else audioEngine.resumeRecording()
                            },
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (recordingState == RecordingState.RECORDING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Pause/Resume",
                                tint = SiennaAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // State C: Recorded and Ready for Preview
                recordedResult != null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(SentimentPositive.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = SentimentPositive, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Voice Greeting Recorded", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Duration: ${recordedResult.durationSeconds}s • Ready to Save", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = onStartRecording,
                            colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Re-record", fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    // Audio Waveform & Player preview
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BentoSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isThisPlaying = isPlaying && playingPath == recordedResult.filePath

                            IconButton(
                                onClick = { audioEngine.togglePlayPause(recordedResult.filePath) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SiennaAccent, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Preview",
                                    tint = BentoBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                WaveformDisplay(
                                    waveformCsv = recordedResult.waveformCsv,
                                    progress = if (isThisPlaying) playbackProgress else 0f,
                                    activeColor = SiennaAccent,
                                    inactiveColor = TextTertiary.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Preview Audio", color = TextSecondary, fontSize = 10.sp)
                                    Text("00:${String.format("%02d", recordedResult.durationSeconds)}", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioUploadStudio(
    audioEngine: com.example.audio.GreetingAudioEngine,
    uploadedResult: RecordedAudioResult?,
    onPickFile: () -> Unit,
    onRemove: () -> Unit
) {
    val isPlaying by audioEngine.isPlaying.collectAsStateWithLifecycle()
    val playingPath by audioEngine.playingAudioPath.collectAsStateWithLifecycle()
    val playbackProgress by audioEngine.playbackProgress.collectAsStateWithLifecycle()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BentoBackground,
        border = BorderStroke(1.dp, BentoCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uploadedResult == null) {
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = SiennaAccent,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Upload Existing Audio File",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Supported formats: .m4a, .mp3, .wav, .aac, .ogg",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Button(
                    onClick = onPickFile,
                    colors = ButtonDefaults.buttonColors(containerColor = SiennaPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Audio File", fontSize = 12.sp)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(SiennaAccent.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Audiotrack, contentDescription = null, tint = SiennaAccent, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(uploadedResult.fileName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Length: ${uploadedResult.durationSeconds}s • Ready to Save", color = TextSecondary, fontSize = 10.sp)
                        }
                    }

                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                // Audio Playback Tester
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BentoSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isThisPlaying = isPlaying && playingPath == uploadedResult.filePath

                        IconButton(
                            onClick = { audioEngine.togglePlayPause(uploadedResult.filePath) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(SiennaAccent, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Preview",
                                tint = BentoBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            WaveformDisplay(
                                waveformCsv = uploadedResult.waveformCsv,
                                progress = if (isThisPlaying) playbackProgress else 0f,
                                activeColor = SiennaAccent,
                                inactiveColor = TextTertiary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Uploaded Audio File", color = TextSecondary, fontSize = 10.sp)
                                Text("00:${String.format("%02d", uploadedResult.durationSeconds)}", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextTtsStudio(
    greetingScript: String,
    onScriptChange: (String) -> Unit,
    onApplyTemplate: (String, String) -> Unit,
    onPreviewVoice: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "AI Voice Script Studio",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        // Script Templates
        Text(
            text = "Quick Script Presets:",
            color = TextSecondary,
            fontSize = 11.sp
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val templates = listOf(
                "💼 Recruiter Filter" to (
                    "Recruiter Priority Screening" to
                    "Hi there, you've reached John Lanter's line. I'm Sienna, his assistant. John is currently exploring new engineering leadership opportunities. Please let me know your company, role, and the best callback number!"
                ),
                "❤️ Family & Friends" to (
                    "Warm Friends & Family" to
                    "Hey! You've reached John. This is Sienna, his assistant. John is currently away from his phone right now, but leave a quick message and I'll notify his Call Tracker immediately."
                ),
                "🏢 Executive Work" to (
                    "Executive Business Routing" to
                    "Hello, you have reached John Lanter's office line. I am Sienna his assistant. Please state the project matter and urgency of your inquiry."
                ),
                "🚫 Spam Deflector" to (
                    "Strict Spam & Robo Filter" to
                    "This is Sienna AI, automated call screener for John Lanter. Solicitation calls are automatically discarded. If this is a verified business contact, please state your name."
                )
            )

            items(templates) { (label, data) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BentoBackground,
                    border = BorderStroke(1.dp, BentoCardBorder),
                    modifier = Modifier.clickable { onApplyTemplate(data.first, data.second) }
                ) {
                    Text(
                        text = label,
                        color = SiennaAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = greetingScript,
            onValueChange = onScriptChange,
            label = { Text("Spoken Greeting Script") },
            placeholder = { Text("Enter the script Sienna will speak when answering this caller...") },
            minLines = 3,
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BentoBackground,
                unfocusedContainerColor = BentoBackground,
                focusedBorderColor = SiennaAccent,
                unfocusedBorderColor = BentoCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onPreviewVoice,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = SiennaAccent, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Sienna TTS Voice", fontSize = 11.sp, color = SiennaAccent, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun GreetingLibraryCard(
    greeting: GreetingEntity,
    viewModel: SiennaViewModel,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onNavigateToSimulator: (() -> Unit)? = null
) {
    val audioEngine = viewModel.greetingAudioEngine
    val isPlaying by audioEngine.isPlaying.collectAsStateWithLifecycle()
    val playingPath by audioEngine.playingAudioPath.collectAsStateWithLifecycle()
    val playbackProgress by audioEngine.playbackProgress.collectAsStateWithLifecycle()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val isThisPlaying = isPlaying && (
        (greeting.isCustomAudio && playingPath == greeting.audioFilePath)
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BentoSurface,
        border = BorderStroke(
            1.dp,
            if (greeting.isActive) SiennaAccent else BentoCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Type Badge + Title + Active Toggle
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
                            .size(38.dp)
                            .background(
                                when {
                                    greeting.isCustomAudio && greeting.audioSourceType == "RECORDED" -> SiennaAccent.copy(alpha = 0.2f)
                                    greeting.isCustomAudio && greeting.audioSourceType == "UPLOADED" -> CategoryBusiness.copy(alpha = 0.2f)
                                    else -> SiennaPrimary.copy(alpha = 0.2f)
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                greeting.isCustomAudio && greeting.audioSourceType == "RECORDED" -> Icons.Default.Mic
                                greeting.isCustomAudio && greeting.audioSourceType == "UPLOADED" -> Icons.Default.Folder
                                else -> Icons.Default.SmartToy
                            },
                            contentDescription = null,
                            tint = when {
                                greeting.isCustomAudio && greeting.audioSourceType == "RECORDED" -> SiennaAccent
                                greeting.isCustomAudio && greeting.audioSourceType == "UPLOADED" -> CategoryBusiness
                                else -> SiennaPrimaryLight
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = greeting.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Audio Type Tag
                            Text(
                                text = when (greeting.audioSourceType) {
                                    "RECORDED" -> "🎙️ Voice Recording (${greeting.audioDurationSeconds}s)"
                                    "UPLOADED" -> "📁 Uploaded Audio (${greeting.audioDurationSeconds}s)"
                                    else -> "🤖 Sienna TTS"
                                },
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Active Pill / Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (greeting.isActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SentimentPositive.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = SentimentPositive, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Active",
                                    color = SentimentPositive,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onToggleActive,
                            colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant),
                            border = BorderStroke(1.dp, BentoCardBorder),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Set Active", fontSize = 10.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // Target Caller Chip & Rule Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Target Caller Pill
                val (callerLabel, callerColor) = when {
                    greeting.assignedContactName != null -> "👤 ${greeting.assignedContactName}" to CategoryFriendFamily
                    greeting.targetCallerType == "RECRUITER" -> "💼 Recruiters & Jobs" to CategoryRecruiter
                    greeting.targetCallerType == "FRIEND_FAMILY" -> "❤️ Friends & Family" to CategoryFriendFamily
                    greeting.targetCallerType == "VIP" -> "🏢 VIP & Clients" to CategoryBusiness
                    greeting.targetCallerType == "SPAM" -> "🚫 Spam Defense" to CategorySpam
                    else -> "👥 All General Callers" to SentimentNeutral
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = callerColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Target: $callerLabel",
                        color = callerColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (greeting.routingRule != "DEFAULT") {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BentoSurfaceVariant
                    ) {
                        Text(
                            text = "Rule: ${greeting.routingRule}",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Spoken Message Text
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BentoBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${greeting.messageText}\"",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }

            // Waveform Audio Player Bar
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BentoSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.playGreetingAudio(greeting) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (greeting.isCustomAudio) SiennaAccent else Color(0xFF6366F1),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play Greeting",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        WaveformDisplay(
                            waveformCsv = greeting.waveformCsv,
                            progress = if (isThisPlaying) playbackProgress else 0f,
                            activeColor = if (greeting.isCustomAudio) SiennaAccent else SiennaPrimaryLight,
                            inactiveColor = TextTertiary.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${greeting.audioDurationSeconds}s",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Bottom Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onDuplicate,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                }

                if (onNavigateToSimulator != null) {
                    Button(
                        onClick = { onNavigateToSimulator() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = SiennaAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test with Simulator", fontSize = 10.sp, color = SiennaAccent, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = BentoSurface,
            title = {
                Text("Delete Greeting", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete \"${greeting.title}\" from your local Room database?",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun WaveformDisplay(
    waveformCsv: String,
    progress: Float,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    val barValues = remember(waveformCsv) {
        waveformCsv.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .ifEmpty { listOf(20, 35, 60, 80, 50, 30, 70, 85, 40, 20) }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        barValues.forEachIndexed { index, value ->
            val barRatio = (index.toFloat() / barValues.size)
            val isFilled = barRatio <= progress
            val heightDp = (value * 0.18f).coerceIn(4f, 18f).dp

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(heightDp)
                    .background(
                        if (isFilled) activeColor else inactiveColor,
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
