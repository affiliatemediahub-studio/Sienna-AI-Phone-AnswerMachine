package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CallAnalysisResult
import com.example.ai.DetectedEntity
import com.example.ai.GeminiScreenerService
import com.example.ai.GeminiTranscriptionService
import com.example.ai.LiveTurn
import com.example.ai.StreamTranscriptionChunk
import com.example.ai.TranscriptionResult
import com.example.audio.GreetingAudioEngine
import com.example.audio.RecordedAudioResult
import com.example.audio.RecordingState
import com.example.audio.SpeechManager
import com.example.data.AppDatabase
import com.example.data.model.ContactEntity
import com.example.data.model.GreetingEntity
import com.example.data.model.VoicemailEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class DateRangePreset(val label: String) {
    ALL_TIME("All Time"),
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    LAST_7_DAYS("Past 7 Days"),
    LAST_30_DAYS("Past 30 Days"),
    LAST_90_DAYS("Past 90 Days"),
    CUSTOM("Custom Range")
}

data class ArchiveSearchFilter(
    val keyword: String = "",
    val callerQuery: String = "",
    val selectedCallerChip: String? = null,
    val dateRangePreset: DateRangePreset = DateRangePreset.ALL_TIME,
    val customStartDate: Long? = null,
    val customEndDate: Long? = null,
    val categoryFilter: String = "ALL", // ALL, RECRUITER, FRIEND_FAMILY, SPAM, BUSINESS, GENERAL
    val sentimentFilter: String = "ALL", // ALL, POSITIVE, NEUTRAL, NEGATIVE
    val urgencyFilter: String = "ALL", // ALL, HIGH_CRITICAL, MEDIUM, LOW
    val starredOnly: Boolean = false
)

enum class CallState {
    IDLE,
    INCOMING_RINGING,
    CONNECTED,
    SIENNA_SPEAKING,
    CALLER_SPEAKING,
    ANALYZING,
    CALL_ENDED
}

data class TrackerAlert(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val message: String,
    val category: String,
    val sentiment: String,
    val timestamp: Long = System.currentTimeMillis()
)

class SiennaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = AppRepository(
        database.voicemailDao(),
        database.contactDao(),
        database.greetingDao()
    )
    private val screenerService = GeminiScreenerService()
    val transcriptionService = GeminiTranscriptionService()
    val speechManager = SpeechManager(application)
    val greetingAudioEngine = GreetingAudioEngine(application)

    // Real-Time Gemini Transcription Stream
    private val _liveStreamingTranscription = MutableStateFlow("")
    val liveStreamingTranscription: StateFlow<String> = _liveStreamingTranscription.asStateFlow()

    private val _liveDetectedEntities = MutableStateFlow<List<DetectedEntity>>(emptyList())
    val liveDetectedEntities: StateFlow<List<DetectedEntity>> = _liveDetectedEntities.asStateFlow()

    private val _isTranscribingStream = MutableStateFlow(false)
    val isTranscribingStream: StateFlow<Boolean> = _isTranscribingStream.asStateFlow()

    // Data streams
    val allVoicemails: StateFlow<List<VoicemailEntity>> = repository.allVoicemails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGreetings: StateFlow<List<GreetingEntity>> = repository.allGreetings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGreeting: StateFlow<GreetingEntity?> = repository.activeGreeting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val unreadCount: StateFlow<Int> = repository.unreadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val trackerDispatches: StateFlow<List<VoicemailEntity>> = repository.trackerDispatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Filters
    private val _selectedFilter = MutableStateFlow("ALL") // ALL, RECRUITER, FRIEND_FAMILY, HIGH_URGENCY, POSITIVE, SPAM
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered Voicemails for Dashboard
    val filteredVoicemails: StateFlow<List<VoicemailEntity>> = combine(
        allVoicemails,
        _selectedFilter,
        _searchQuery
    ) { list, filter, query ->
        list.filter { item ->
            val matchesFilter = when (filter) {
                "ALL" -> true
                "RECRUITER" -> item.category == "RECRUITER"
                "FRIEND_FAMILY" -> item.category == "FRIEND_FAMILY"
                "HIGH_URGENCY" -> item.urgencyLevel in listOf("HIGH", "CRITICAL")
                "POSITIVE" -> item.sentiment == "POSITIVE"
                "SPAM" -> item.category == "SPAM"
                else -> true
            }
            val matchesQuery = if (query.isBlank()) true else {
                item.callerName.contains(query, ignoreCase = true) ||
                item.summary.contains(query, ignoreCase = true) ||
                item.transcript.contains(query, ignoreCase = true) ||
                (item.recruiterCompany?.contains(query, ignoreCase = true) == true)
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Searchable Archive State & Engine ---
    private val _archiveFilter = MutableStateFlow(ArchiveSearchFilter())
    val archiveFilter: StateFlow<ArchiveSearchFilter> = _archiveFilter.asStateFlow()

    val activeFilterCount: StateFlow<Int> = _archiveFilter.map { filter ->
        var count = 0
        if (filter.keyword.isNotBlank()) count++
        if (filter.callerQuery.isNotBlank() || filter.selectedCallerChip != null) count++
        if (filter.dateRangePreset != DateRangePreset.ALL_TIME) count++
        if (filter.categoryFilter != "ALL") count++
        if (filter.sentimentFilter != "ALL") count++
        if (filter.urgencyFilter != "ALL") count++
        if (filter.starredOnly) count++
        count
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val archiveSearchResults: StateFlow<List<VoicemailEntity>> = combine(
        allVoicemails,
        _archiveFilter
    ) { voicemails, filter ->
        voicemails.filter { item ->
            // 1. Keyword search (transcription, summary, caller name, company, role, key phrases, action items)
            val matchesKeyword = if (filter.keyword.isBlank()) true else {
                val kw = filter.keyword.trim()
                item.transcript.contains(kw, ignoreCase = true) ||
                item.summary.contains(kw, ignoreCase = true) ||
                item.callerName.contains(kw, ignoreCase = true) ||
                item.callerNumber.contains(kw, ignoreCase = true) ||
                (item.recruiterCompany?.contains(kw, ignoreCase = true) == true) ||
                (item.recruiterRole?.contains(kw, ignoreCase = true) == true) ||
                (item.recruiterCallback?.contains(kw, ignoreCase = true) == true) ||
                item.emotionalTone.contains(kw, ignoreCase = true) ||
                item.keyEmotionalPhrases.contains(kw, ignoreCase = true) ||
                item.actionItems.contains(kw, ignoreCase = true)
            }

            // 2. Caller query search (by name or number)
            val matchesCallerQuery = if (filter.callerQuery.isBlank()) true else {
                val cq = filter.callerQuery.trim()
                val cleanItemPhone = item.callerNumber.replace(Regex("[^0-9+]"), "")
                val cleanQueryPhone = cq.replace(Regex("[^0-9+]"), "")
                item.callerName.contains(cq, ignoreCase = true) ||
                item.callerNumber.contains(cq, ignoreCase = true) ||
                (cleanQueryPhone.isNotEmpty() && cleanItemPhone.contains(cleanQueryPhone))
            }

            // 3. Selected Caller Chip
            val matchesSelectedCaller = if (filter.selectedCallerChip.isNullOrBlank()) true else {
                item.callerName.equals(filter.selectedCallerChip, ignoreCase = true)
            }

            // 4. Date range filter
            val matchesDate = isWithinDateRange(
                item.timestamp,
                filter.dateRangePreset,
                filter.customStartDate,
                filter.customEndDate
            )

            // 5. Category filter
            val matchesCategory = when (filter.categoryFilter) {
                "ALL" -> true
                else -> item.category.equals(filter.categoryFilter, ignoreCase = true)
            }

            // 6. Sentiment filter
            val matchesSentiment = when (filter.sentimentFilter) {
                "ALL" -> true
                else -> item.sentiment.equals(filter.sentimentFilter, ignoreCase = true)
            }

            // 7. Urgency filter
            val matchesUrgency = when (filter.urgencyFilter) {
                "ALL" -> true
                "HIGH_CRITICAL" -> item.urgencyLevel in listOf("HIGH", "CRITICAL")
                else -> item.urgencyLevel.equals(filter.urgencyFilter, ignoreCase = true)
            }

            // 8. Starred filter
            val matchesStarred = if (filter.starredOnly) item.isStarred else true

            matchesKeyword && matchesCallerQuery && matchesSelectedCaller && matchesDate &&
                    matchesCategory && matchesSentiment && matchesUrgency && matchesStarred
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audio Playback State
    private val _playingVoicemailId = MutableStateFlow<Long?>(null)
    val playingVoicemailId: StateFlow<Long?> = _playingVoicemailId.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private var playbackJob: Job? = null

    // Selected Voicemail for Detail Sheet
    private val _selectedVoicemail = MutableStateFlow<VoicemailEntity?>(null)
    val selectedVoicemail: StateFlow<VoicemailEntity?> = _selectedVoicemail.asStateFlow()

    // Notification toast stream for 'John's Call Tracker'
    private val _trackerAlerts = MutableSharedFlow<TrackerAlert>()
    val trackerAlerts: SharedFlow<TrackerAlert> = _trackerAlerts.asSharedFlow()

    private val _activeAlert = MutableStateFlow<TrackerAlert?>(null)
    val activeAlert: StateFlow<TrackerAlert?> = _activeAlert.asStateFlow()

    // Live Call Simulator State
    private val _callState = MutableStateFlow(CallState.IDLE)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _activeCaller = MutableStateFlow<ContactEntity?>(null)
    val activeCaller: StateFlow<ContactEntity?> = _activeCaller.asStateFlow()

    private val _customCallerNumber = MutableStateFlow("+1 (415) 555-0199")
    val customCallerNumber: StateFlow<String> = _customCallerNumber.asStateFlow()

    private val _liveConversation = MutableStateFlow<List<LiveTurn>>(emptyList())
    val liveConversation: StateFlow<List<LiveTurn>> = _liveConversation.asStateFlow()

    private val _liveCallDuration = MutableStateFlow(0)
    val liveCallDuration: StateFlow<Int> = _liveCallDuration.asStateFlow()

    private var callTimerJob: Job? = null
    private var callStartTime = 0L

    // Google Chat Webhook URL (customizable)
    private val _callTrackerWebhookUrl = MutableStateFlow("https://chat.googleapis.com/v1/spaces/SPACE_JOHNS_CALL_TRACKER/messages")
    val callTrackerWebhookUrl: StateFlow<String> = _callTrackerWebhookUrl.asStateFlow()

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectVoicemail(voicemail: VoicemailEntity?) {
        _selectedVoicemail.value = voicemail
        if (voicemail != null && !voicemail.isRead) {
            viewModelScope.launch {
                repository.markAsRead(voicemail.id, true)
            }
        }
    }

    fun toggleStarred(voicemail: VoicemailEntity) {
        viewModelScope.launch {
            repository.toggleStarred(voicemail.id, !voicemail.isStarred)
        }
    }

    fun deleteVoicemail(id: Long) {
        viewModelScope.launch {
            if (_playingVoicemailId.value == id) {
                stopAudioPlayback()
            }
            if (_selectedVoicemail.value?.id == id) {
                _selectedVoicemail.value = null
            }
            repository.deleteVoicemail(id)
        }
    }

    // Audio Playback simulation
    fun togglePlayVoicemail(voicemail: VoicemailEntity) {
        if (_playingVoicemailId.value == voicemail.id) {
            stopAudioPlayback()
        } else {
            stopAudioPlayback()
            _playingVoicemailId.value = voicemail.id
            _playbackProgress.value = 0f

            // Read aloud summary or transcript snippet with speech manager
            speechManager.speak(voicemail.summary)

            playbackJob = viewModelScope.launch {
                val totalSteps = (voicemail.durationSeconds.coerceAtLeast(10)) * 10
                for (i in 0..totalSteps) {
                    _playbackProgress.value = i.toFloat() / totalSteps
                    delay(100)
                }
                stopAudioPlayback()
            }
        }
    }

    fun stopAudioPlayback() {
        playbackJob?.cancel()
        playbackJob = null
        _playingVoicemailId.value = null
        _playbackProgress.value = 0f
        speechManager.stop()
    }

    // Live Call Simulation Actions
    fun startIncomingCall(contact: ContactEntity? = null, customNumber: String? = null) {
        stopAudioPlayback()
        _activeCaller.value = contact
        _customCallerNumber.value = customNumber ?: contact?.phoneNumber ?: "+1 (415) 890-2341"
        _liveConversation.value = emptyList()
        _liveCallDuration.value = 0
        _callState.value = CallState.INCOMING_RINGING
    }

    fun answerCall() {
        _callState.value = CallState.CONNECTED
        callStartTime = System.currentTimeMillis()
        startCallTimer()

        viewModelScope.launch {
            // Dynamically resolve custom greeting for this caller from Room
            val callerGreeting = repository.getGreetingForCaller(
                contactId = _activeCaller.value?.id,
                phoneNumber = _customCallerNumber.value,
                category = _activeCaller.value?.category
            )

            val greetingText = callerGreeting?.messageText ?: activeGreeting.value?.messageText ?: "Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?"

            val initialTurn = LiveTurn(speaker = "Sienna", text = greetingText)
            _liveConversation.value = listOf(initialTurn)
            _callState.value = CallState.SIENNA_SPEAKING

            if (callerGreeting?.isCustomAudio == true && !callerGreeting.audioFilePath.isNullOrBlank()) {
                // Play custom recorded/uploaded voice greeting
                greetingAudioEngine.playAudio(callerGreeting.audioFilePath) {
                    _callState.value = CallState.CALLER_SPEAKING
                }
            } else {
                speechManager.speak(greetingText) {
                    _callState.value = CallState.CALLER_SPEAKING
                }
            }

            // Timer fallback to transition out of speaking state
            delay(3500)
            if (_callState.value == CallState.SIENNA_SPEAKING) {
                _callState.value = CallState.CALLER_SPEAKING
            }
        }
    }

    fun callerSpeak(text: String, audioBytes: ByteArray? = null) {
        if (text.isBlank() && audioBytes == null) return
        
        viewModelScope.launch {
            _callState.value = CallState.CALLER_SPEAKING
            _isTranscribingStream.value = true
            _liveStreamingTranscription.value = ""
            
            val finalizedTranscript: String = if (audioBytes != null) {
                // Real Gemini API audio stream transcription
                val result = transcriptionService.transcribeAudioBytes(audioBytes) { chunk ->
                    _liveStreamingTranscription.value = chunk.accumulatedText
                    _liveDetectedEntities.value = chunk.detectedEntities
                }
                result.formattedTranscript
            } else {
                // Stream text chunk by chunk with real-time token pacing and live entity detection
                val words = text.trim().split(" ")
                val builder = StringBuilder()
                for (word in words) {
                    builder.append(if (builder.isEmpty()) word else " $word")
                    val currentText = builder.toString()
                    _liveStreamingTranscription.value = currentText
                    _liveDetectedEntities.value = transcriptionService.extractEntitiesFromText(currentText)
                    delay(55)
                }
                builder.toString()
            }

            _isTranscribingStream.value = false
            _liveStreamingTranscription.value = ""

            // Append finalized turn to conversation list
            val currentList = _liveConversation.value.toMutableList()
            currentList.add(LiveTurn(speaker = "Caller", text = finalizedTranscript))
            _liveConversation.value = currentList
            _callState.value = CallState.SIENNA_SPEAKING

            // Call Gemini to generate Sienna's smart response
            val siennaReply = screenerService.generateSiennaReply(
                conversation = currentList,
                callerName = _activeCaller.value?.name,
                callerNumber = _customCallerNumber.value,
                contactCategory = _activeCaller.value?.category
            )

            val updatedList = _liveConversation.value.toMutableList()
            updatedList.add(LiveTurn(speaker = "Sienna", text = siennaReply))
            _liveConversation.value = updatedList

            speechManager.speak(siennaReply) {
                _callState.value = CallState.CALLER_SPEAKING
            }

            delay(3000)
            if (_callState.value == CallState.SIENNA_SPEAKING) {
                _callState.value = CallState.CALLER_SPEAKING
            }
        }
    }

    /**
     * Direct Gemini API real-time audio stream transcription for incoming voicemail audio streams
     */
    fun processIncomingVoicemailAudioStream(
        audioBytes: ByteArray,
        callerContact: ContactEntity? = null,
        phoneNumber: String? = null,
        onTranscriptionFinished: ((TranscriptionResult) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isTranscribingStream.value = true
            _callState.value = CallState.ANALYZING

            val result = transcriptionService.transcribeAudioBytes(audioBytes) { chunk ->
                _liveStreamingTranscription.value = chunk.accumulatedText
                _liveDetectedEntities.value = chunk.detectedEntities
            }

            _isTranscribingStream.value = false
            _liveStreamingTranscription.value = ""
            onTranscriptionFinished?.invoke(result)
        }
    }

    fun endAndScreenCall() {
        stopCallTimer()
        speechManager.stop()
        _callState.value = CallState.ANALYZING

        viewModelScope.launch {
            val transcriptBuilder = StringBuilder()
            _liveConversation.value.forEach { turn ->
                transcriptBuilder.append("${turn.speaker}: ${turn.text}\n\n")
            }
            val fullTranscript = transcriptBuilder.toString().trim()
            val callerName = _activeCaller.value?.name ?: "Unknown Caller"
            val callerNumber = _customCallerNumber.value

            // Perform AI analysis & sentiment classification
            val analysis: CallAnalysisResult = if (fullTranscript.isNotBlank()) {
                screenerService.analyzeCallTranscript(fullTranscript, callerName, callerNumber)
            } else {
                screenerService.analyzeCallTranscript(
                    "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nCaller: Hey John, just checking in!",
                    callerName,
                    callerNumber
                )
            }

            // Save to Room Database
            val voicemail = VoicemailEntity(
                callerName = callerName,
                callerNumber = callerNumber,
                callerRelationship = when (analysis.category) {
                    "RECRUITER" -> "Recruiter"
                    "FRIEND_FAMILY" -> "Friend & Family"
                    "SPAM" -> "Spam"
                    else -> "General Caller"
                },
                timestamp = System.currentTimeMillis(),
                durationSeconds = _liveCallDuration.value.coerceAtLeast(15),
                transcript = fullTranscript.ifBlank { "Sienna: Hello, you have reached John Lanter's number... Call ended." },
                summary = analysis.summary,
                category = analysis.category,
                sentiment = analysis.sentiment,
                sentimentScore = analysis.sentimentScore,
                emotionalTone = analysis.emotionalTone,
                keyEmotionalPhrases = analysis.keyEmotionalPhrases.joinToString(", "),
                urgencyLevel = analysis.urgencyLevel,
                recruiterCompany = analysis.recruiterCompany,
                recruiterRole = analysis.recruiterRole,
                recruiterCallback = analysis.recruiterCallback ?: callerNumber,
                actionItems = analysis.actionItems.joinToString(", "),
                sentToCallTracker = true,
                trackerSentTimestamp = System.currentTimeMillis(),
                audioWaveform = SpeechManager.generateRandomWaveform().joinToString(",") { it.toInt().toString() },
                isRead = false,
                isStarred = analysis.urgencyLevel in listOf("HIGH", "CRITICAL")
            )

            val insertedId = repository.saveVoicemail(voicemail)
            val savedVoicemail = voicemail.copy(id = insertedId)

            // Immediately send summary notification to 'John's Call Tracker'
            val alert = TrackerAlert(
                title = "John's Call Tracker Alert",
                message = analysis.trackerCardMessage,
                category = analysis.category,
                sentiment = analysis.sentiment
            )
            _trackerAlerts.emit(alert)
            _activeAlert.value = alert

            _callState.value = CallState.CALL_ENDED
            _selectedVoicemail.value = savedVoicemail

            // Dismiss active toast after 6 seconds
            delay(6000)
            if (_activeAlert.value?.id == alert.id) {
                _activeAlert.value = null
            }
        }
    }

    fun dismissAlert() {
        _activeAlert.value = null
    }

    fun resetCallState() {
        speechManager.stop()
        stopCallTimer()
        _callState.value = CallState.IDLE
        _liveConversation.value = emptyList()
        _activeCaller.value = null
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _liveCallDuration.value += 1
            }
        }
    }

    private fun stopCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = null
    }

    // Greeting Studio Operations
    fun setActiveGreeting(greetingId: Long) {
        viewModelScope.launch {
            repository.setActiveGreeting(greetingId)
        }
    }

    fun setActiveGreetingForCallerType(greetingId: Long, callerType: String) {
        viewModelScope.launch {
            repository.setActiveGreetingForCallerType(greetingId, callerType)
        }
    }

    fun setActiveGreetingForContact(greetingId: Long, contactId: Long) {
        viewModelScope.launch {
            repository.setActiveGreetingForContact(greetingId, contactId)
        }
    }

    fun saveGreeting(greeting: GreetingEntity) {
        viewModelScope.launch {
            repository.addGreeting(greeting)
        }
    }

    fun updateGreeting(greeting: GreetingEntity) {
        viewModelScope.launch {
            repository.updateGreeting(greeting)
        }
    }

    fun deleteGreeting(greeting: GreetingEntity) {
        viewModelScope.launch {
            repository.deleteGreeting(greeting)
        }
    }

    fun duplicateGreeting(greeting: GreetingEntity) {
        viewModelScope.launch {
            val copy = greeting.copy(
                id = 0,
                title = "${greeting.title} (Copy)",
                isActive = false,
                createdAt = System.currentTimeMillis()
            )
            repository.addGreeting(copy)
        }
    }

    fun addCustomGreeting(title: String, message: String, voiceType: String, routingRule: String) {
        viewModelScope.launch {
            val newGreeting = GreetingEntity(
                title = title,
                messageText = message,
                voiceType = voiceType,
                isActive = false,
                routingRule = routingRule,
                isCustomAudio = false,
                audioSourceType = "TTS",
                targetCallerType = "ALL"
            )
            repository.addGreeting(newGreeting)
        }
    }

    fun playGreetingAudio(greeting: GreetingEntity) {
        if (greeting.isCustomAudio && !greeting.audioFilePath.isNullOrBlank()) {
            greetingAudioEngine.togglePlayPause(greeting.audioFilePath)
        } else {
            speechManager.speak(greeting.messageText)
        }
    }

    fun stopGreetingAudio() {
        greetingAudioEngine.stopPlayback()
        speechManager.stop()
    }

    fun previewGreetingVoice(text: String) {
        speechManager.speak(text)
    }

    // Contact Directory Operations
    fun addContact(name: String, phone: String, category: String, company: String?, relationship: String, handlingRule: String) {
        viewModelScope.launch {
            val contact = ContactEntity(
                name = name,
                phoneNumber = phone,
                category = category,
                company = company,
                relationship = relationship,
                handlingRule = handlingRule
            )
            repository.addContact(contact)
        }
    }

    fun deleteContact(id: Long) {
        viewModelScope.launch {
            repository.deleteContact(id)
        }
    }

    // Searchable Archive Filter Actions
    fun setArchiveKeyword(keyword: String) {
        _archiveFilter.value = _archiveFilter.value.copy(keyword = keyword)
    }

    fun setArchiveCallerQuery(query: String) {
        _archiveFilter.value = _archiveFilter.value.copy(callerQuery = query)
    }

    fun setSelectedCallerChip(callerName: String?) {
        _archiveFilter.value = _archiveFilter.value.copy(selectedCallerChip = callerName)
    }

    fun setDateRangePreset(preset: DateRangePreset) {
        _archiveFilter.value = _archiveFilter.value.copy(dateRangePreset = preset)
    }

    fun setCustomDateRange(startMillis: Long?, endMillis: Long?) {
        _archiveFilter.value = _archiveFilter.value.copy(
            dateRangePreset = DateRangePreset.CUSTOM,
            customStartDate = startMillis,
            customEndDate = endMillis
        )
    }

    fun setArchiveCategory(category: String) {
        _archiveFilter.value = _archiveFilter.value.copy(categoryFilter = category)
    }

    fun setArchiveSentiment(sentiment: String) {
        _archiveFilter.value = _archiveFilter.value.copy(sentimentFilter = sentiment)
    }

    fun setArchiveUrgency(urgency: String) {
        _archiveFilter.value = _archiveFilter.value.copy(urgencyFilter = urgency)
    }

    fun toggleArchiveStarredOnly() {
        _archiveFilter.value = _archiveFilter.value.copy(starredOnly = !_archiveFilter.value.starredOnly)
    }

    fun resetArchiveFilters() {
        _archiveFilter.value = ArchiveSearchFilter()
    }

    private fun isWithinDateRange(
        timestamp: Long,
        preset: DateRangePreset,
        customStart: Long?,
        customEnd: Long?
    ): Boolean {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        return when (preset) {
            DateRangePreset.ALL_TIME -> true
            DateRangePreset.TODAY -> {
                calendar.timeInMillis = now
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfToday = calendar.timeInMillis
                timestamp >= startOfToday
            }
            DateRangePreset.YESTERDAY -> {
                calendar.timeInMillis = now
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfYesterday = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val startOfToday = calendar.timeInMillis
                timestamp in startOfYesterday until startOfToday
            }
            DateRangePreset.LAST_7_DAYS -> {
                timestamp >= (now - 7 * 24 * 3600 * 1000L)
            }
            DateRangePreset.LAST_30_DAYS -> {
                timestamp >= (now - 30 * 24 * 3600 * 1000L)
            }
            DateRangePreset.LAST_90_DAYS -> {
                timestamp >= (now - 90 * 24 * 3600 * 1000L)
            }
            DateRangePreset.CUSTOM -> {
                val afterStart = customStart == null || timestamp >= customStart
                val beforeEnd = customEnd == null || timestamp <= customEnd
                afterStart && beforeEnd
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.shutdown()
        greetingAudioEngine.release()
        stopAudioPlayback()
        stopCallTimer()
    }
}
