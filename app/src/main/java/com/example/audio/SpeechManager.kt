package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentSpokenText = MutableStateFlow("")
    val currentSpokenText: StateFlow<String> = _currentSpokenText.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("SpeechManager", "Language not supported")
            } else {
                isInitialized = true
                tts?.setPitch(1.08f) // Friendly feminine pitch for Sienna
                tts?.setSpeechRate(1.02f) // Conversational pace
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentSpokenText.value = ""
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentSpokenText.value = ""
                    }
                })
            }
        } else {
            Log.e("SpeechManager", "TTS initialization failed")
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        _currentSpokenText.value = text
        if (!isInitialized || tts == null) {
            _isSpeaking.value = true
            // If TTS engine not ready on device, mock speaking state briefly
            return
        }
        val utteranceId = System.currentTimeMillis().toString()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentSpokenText.value = ""
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    companion object {
        fun generateRandomWaveform(length: Int = 24): List<Float> {
            val base = listOf(25f, 40f, 65f, 85f, 95f, 70f, 50f, 80f, 60f, 45f, 90f, 75f, 35f, 55f, 80f, 65f, 40f, 60f, 75f, 50f, 30f, 20f)
            return List(length) { i ->
                val b = base.getOrElse(i % base.size) { 40f }
                (b + (-10..10).random()).coerceIn(15f, 100f)
            }
        }
    }
}
