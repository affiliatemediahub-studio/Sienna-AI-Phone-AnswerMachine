package com.example.audio

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class RecordingState {
    IDLE,
    RECORDING,
    PAUSED,
    COMPLETED
}

data class RecordedAudioResult(
    val file: File,
    val filePath: String,
    val fileName: String,
    val durationSeconds: Int,
    val waveformCsv: String
)

class GreetingAudioEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)

    // Recording State
    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null
    private var recordingJob: Job? = null
    private var recordingStartTime: Long = 0L
    private var pausedDuration: Long = 0L

    private val _recordingState = MutableStateFlow(RecordingState.IDLE)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private val _liveAmplitude = MutableStateFlow(0.1f)
    val liveAmplitude: StateFlow<Float> = _liveAmplitude.asStateFlow()

    private val recordedAmplitudes = mutableListOf<Float>()

    // Playback State
    private var mediaPlayer: MediaPlayer? = null
    private var playbackProgressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val _playbackCurrentSeconds = MutableStateFlow(0)
    val playbackCurrentSeconds: StateFlow<Int> = _playbackCurrentSeconds.asStateFlow()

    private val _playingAudioPath = MutableStateFlow<String?>(null)
    val playingAudioPath: StateFlow<String?> = _playingAudioPath.asStateFlow()

    private fun getGreetingsDir(): File {
        val dir = File(context.filesDir, "greetings")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    // --- Audio Recording Methods ---

    fun startRecording(): Boolean {
        try {
            stopPlayback()
            val dir = getGreetingsDir()
            val fileName = "voice_greeting_${System.currentTimeMillis()}.m4a"
            val file = File(dir, fileName)
            currentRecordingFile = file
            recordedAmplitudes.clear()

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            recordingStartTime = System.currentTimeMillis()
            _recordingState.value = RecordingState.RECORDING
            _recordingDurationSeconds.value = 0

            startAmplitudePolling()
            return true
        } catch (e: Exception) {
            Log.e("GreetingAudioEngine", "Failed to start audio recording: ${e.message}", e)
            _recordingState.value = RecordingState.IDLE
            return false
        }
    }

    private fun startAmplitudePolling() {
        recordingJob?.cancel()
        recordingJob = scope.launch {
            while (isActive && _recordingState.value == RecordingState.RECORDING) {
                try {
                    val maxAmp = mediaRecorder?.maxAmplitude ?: 0
                    // Map 0..32767 to 0.05..1.0
                    val normalized = (maxAmp / 32767f).coerceIn(0.05f, 1.0f)
                    _liveAmplitude.value = normalized
                    recordedAmplitudes.add(normalized)

                    val elapsed = ((System.currentTimeMillis() - recordingStartTime) / 1000).toInt()
                    _recordingDurationSeconds.value = elapsed
                } catch (e: Exception) {
                    // Ignore transient amplitude polling error
                }
                delay(100)
            }
        }
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && _recordingState.value == RecordingState.RECORDING) {
            try {
                mediaRecorder?.pause()
                _recordingState.value = RecordingState.PAUSED
            } catch (e: Exception) {
                Log.e("GreetingAudioEngine", "Failed to pause recording", e)
            }
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && _recordingState.value == RecordingState.PAUSED) {
            try {
                mediaRecorder?.resume()
                _recordingState.value = RecordingState.RECORDING
                startAmplitudePolling()
            } catch (e: Exception) {
                Log.e("GreetingAudioEngine", "Failed to resume recording", e)
            }
        }
    }

    fun stopRecording(): RecordedAudioResult? {
        recordingJob?.cancel()
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.w("GreetingAudioEngine", "Exception stopping recorder: ${e.message}")
        } finally {
            mediaRecorder = null
        }

        _recordingState.value = RecordingState.COMPLETED
        val file = currentRecordingFile
        if (file == null || !file.exists() || file.length() == 0L) {
            _recordingState.value = RecordingState.IDLE
            return null
        }

        val duration = extractDurationSeconds(file.absolutePath).coerceAtLeast(1)
        val waveform = generateWaveformCsv(recordedAmplitudes)

        return RecordedAudioResult(
            file = file,
            filePath = file.absolutePath,
            fileName = file.name,
            durationSeconds = duration,
            waveformCsv = waveform
        )
    }

    fun cancelRecording() {
        recordingJob?.cancel()
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaRecorder = null
        }
        currentRecordingFile?.delete()
        currentRecordingFile = null
        _recordingState.value = RecordingState.IDLE
        _recordingDurationSeconds.value = 0
        _liveAmplitude.value = 0.1f
    }

    // --- Audio File Upload / Import Methods ---

    fun importAudioFileFromUri(uri: Uri, displayName: String? = null): RecordedAudioResult? {
        try {
            stopPlayback()
            val dir = getGreetingsDir()
            val originalName = displayName ?: "uploaded_greeting_${System.currentTimeMillis()}.m4a"
            val sanitizedName = "upload_${System.currentTimeMillis()}_" + originalName.filter { it.isLetterOrDigit() || it == '.' || it == '_' }
            val destinationFile = File(dir, sanitizedName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return null

            if (!destinationFile.exists() || destinationFile.length() == 0L) {
                return null
            }

            val durationSeconds = extractDurationSeconds(destinationFile.absolutePath).coerceAtLeast(1)
            val generatedWaveform = generateSyntheticWaveformCsv(24)

            return RecordedAudioResult(
                file = destinationFile,
                filePath = destinationFile.absolutePath,
                fileName = originalName,
                durationSeconds = durationSeconds,
                waveformCsv = generatedWaveform
            )
        } catch (e: Exception) {
            Log.e("GreetingAudioEngine", "Failed to import audio from uri: ${e.message}", e)
            return null
        }
    }

    // --- Audio Playback Methods ---

    fun playAudio(filePath: String, onFinished: (() -> Unit)? = null) {
        stopPlayback()
        val file = File(filePath)
        if (!file.exists()) {
            Log.e("GreetingAudioEngine", "Audio file not found: $filePath")
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                _isPlaying.value = true
                _playingAudioPath.value = filePath

                setOnCompletionListener {
                    stopPlayback()
                    onFinished?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    stopPlayback()
                    true
                }
            }

            startPlaybackProgressPolling()
        } catch (e: Exception) {
            Log.e("GreetingAudioEngine", "Failed to play audio: ${e.message}", e)
            stopPlayback()
        }
    }

    fun togglePlayPause(filePath: String) {
        if (_isPlaying.value && _playingAudioPath.value == filePath) {
            pausePlayback()
        } else if (_playingAudioPath.value == filePath && mediaPlayer != null) {
            resumePlayback()
        } else {
            playAudio(filePath)
        }
    }

    fun pausePlayback() {
        try {
            mediaPlayer?.pause()
            _isPlaying.value = false
        } catch (e: Exception) {
            Log.e("GreetingAudioEngine", "Error pausing playback: ${e.message}")
        }
    }

    fun resumePlayback() {
        try {
            mediaPlayer?.start()
            _isPlaying.value = true
            startPlaybackProgressPolling()
        } catch (e: Exception) {
            Log.e("GreetingAudioEngine", "Error resuming playback: ${e.message}")
        }
    }

    fun stopPlayback() {
        playbackProgressJob?.cancel()
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaPlayer = null
        }
        _isPlaying.value = false
        _playbackProgress.value = 0f
        _playbackCurrentSeconds.value = 0
        _playingAudioPath.value = null
    }

    private fun startPlaybackProgressPolling() {
        playbackProgressJob?.cancel()
        playbackProgressJob = scope.launch {
            while (isActive && _isPlaying.value && mediaPlayer != null) {
                try {
                    val current = mediaPlayer?.currentPosition ?: 0
                    val total = mediaPlayer?.duration ?: 1
                    if (total > 0) {
                        _playbackProgress.value = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                        _playbackCurrentSeconds.value = (current / 1000)
                    }
                } catch (e: Exception) {
                    // ignore
                }
                delay(100)
            }
        }
    }

    // --- Helper Utilities ---

    fun extractDurationSeconds(path: String): Int {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val millis = durationStr?.toLongOrNull() ?: 0L
            (millis / 1000).toInt()
        } catch (e: Exception) {
            5
        } finally {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    retriever.close()
                } else {
                    retriever.release()
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    private fun generateWaveformCsv(amplitudes: List<Float>, targetBars: Int = 24): String {
        if (amplitudes.isEmpty()) {
            return generateSyntheticWaveformCsv(targetBars)
        }
        val sampled = if (amplitudes.size <= targetBars) {
            amplitudes
        } else {
            val step = amplitudes.size.toFloat() / targetBars
            List(targetBars) { i ->
                val index = (i * step).toInt().coerceIn(0, amplitudes.size - 1)
                amplitudes[index]
            }
        }
        return sampled.joinToString(",") { amp ->
            val scaled = (amp * 100).toInt().coerceIn(15, 100)
            scaled.toString()
        }
    }

    fun generateSyntheticWaveformCsv(bars: Int = 24): String {
        val basePattern = listOf(20, 35, 60, 85, 95, 70, 45, 80, 65, 50, 90, 75, 40, 60, 85, 65, 45, 70, 80, 55, 35, 25)
        return (0 until bars).map { i ->
            val b = basePattern[i % basePattern.size]
            (b + (-8..8).random()).coerceIn(15, 100)
        }.joinToString(",")
    }

    fun release() {
        cancelRecording()
        stopPlayback()
    }
}
