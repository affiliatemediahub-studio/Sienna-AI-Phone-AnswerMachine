package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CallAnalysisResult(
    val category: String, // RECRUITER, FRIEND_FAMILY, SPAM, BUSINESS, GENERAL
    val summary: String,
    val sentiment: String, // POSITIVE, NEUTRAL, NEGATIVE
    val sentimentScore: Float,
    val emotionalTone: String,
    val keyEmotionalPhrases: List<String>,
    val urgencyLevel: String, // LOW, MEDIUM, HIGH, CRITICAL
    val recruiterCompany: String?,
    val recruiterRole: String?,
    val recruiterCallback: String?,
    val actionItems: List<String>,
    val trackerCardMessage: String
)

data class LiveTurn(
    val speaker: String, // "Caller" or "Sienna"
    val text: String
)

class GeminiScreenerService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val systemPrompt = """
        You are a professional assistant summarizing incoming voicemails for John Lanter's priority review. You are Sienna, John's personal assistant helping him manage his cell phone. Speak naturally, casually, and casually helpful—never sound like a corporate answering service, automated business machine, or a robot.
        Your main job is to figure out who is calling John and handle them based on these rules:
        RECRUITERS & JOB OPPORTUNITIES: Be polite, sharp, and helpful. Let them know John is looking for roles but can't jump on the line this exact second. Take down all the details: the company name, the specific position or role they are calling about, and the best callback number. Tell them you'll pass it to John right away.
        FRIENDS & FAMILY: Be warm, friendly, and completely informal. Just grab a quick, casual message for John and tell them you'll let him know they called.
        SPAM & SCAMMERS: Don't waste time. The moment you realize it's a robo-call, a telemarketer, or a scam, politely wrap up the call or say goodbye and hang up.
        CRITICAL VOICE RULES:
        Keep your answers short and conversational. People are on a live cell phone call; do not give long, drawn-out paragraphs.
        Match the casual energy of a real person answering a friend's phone. Use normal conversational transitions.
    """.trimIndent()

    /**
     * Generates Sienna's next real-time spoken reply in a live call
     */
    suspend fun generateSiennaReply(
        conversation: List<LiveTurn>,
        callerName: String?,
        callerNumber: String,
        contactCategory: String?
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackSiennaReply(conversation, callerName, contactCategory)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // Build conversation history
            val convoHistory = StringBuilder()
            convoHistory.append("Caller Info: Name=${callerName ?: "Unknown"}, Number=$callerNumber, KnownCategory=${contactCategory ?: "Unknown"}\n\n")
            convoHistory.append("Current Call Transcript:\n")
            conversation.forEach { turn ->
                convoHistory.append("${turn.speaker}: ${turn.text}\n")
            }
            convoHistory.append("\nGenerate Sienna's next spoken line (1-2 short casual sentences max):")

            val contentObj = JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", convoHistory.toString())))
            }
            contentsArray.put(contentObj)

            val rootJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 120)
                })
            }

            val body = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    return@withContext text.trim()
                }
            }
            Log.w("GeminiScreener", "API failed with code ${response.code}, using fallback: $responseBody")
            return@withContext fallbackSiennaReply(conversation, callerName, contactCategory)
        } catch (e: Exception) {
            Log.e("GeminiScreener", "Error in generateSiennaReply", e)
            return@withContext fallbackSiennaReply(conversation, callerName, contactCategory)
        }
    }

    /**
     * Analyzes full transcript to extract sentiment, urgency, recruiter info, and 'John's Call Tracker' card
     */
    suspend fun analyzeCallTranscript(
        transcript: String,
        callerName: String,
        callerNumber: String
    ): CallAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackAnalyzeCall(transcript, callerName, callerNumber)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val prompt = """
                Analyze the following phone call transcript screened by Sienna (John Lanter's assistant) and output a strict JSON object.

                Transcript:
                $transcript

                Caller: $callerName ($callerNumber)

                Required JSON schema:
                {
                  "category": "RECRUITER" | "FRIEND_FAMILY" | "SPAM" | "BUSINESS" | "GENERAL",
                  "summary": "1-2 sentence executive summary for John",
                  "sentiment": "POSITIVE" | "NEUTRAL" | "NEGATIVE",
                  "sentimentScore": 0.0 to 1.0 (float),
                  "emotionalTone": "e.g. Enthusiastic & Professional, Warm & Loving, Anxious, Robotic, Frustrated",
                  "keyEmotionalPhrases": ["phrase1", "phrase2"],
                  "urgencyLevel": "LOW" | "MEDIUM" | "HIGH" | "CRITICAL",
                  "recruiterCompany": "Company name or null",
                  "recruiterRole": "Position name or null",
                  "recruiterCallback": "Phone number or null",
                  "actionItems": ["action item 1", "action item 2"],
                  "trackerCardMessage": "Short high-contrast card description for John's Call Tracker feed"
                }

                Respond ONLY with raw valid JSON without markdown wrapping.
            """.trimIndent()

            val rootJson = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                })
            }

            val body = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    val cleanJson = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val resultJson = JSONObject(cleanJson)

                    val phrasesList = mutableListOf<String>()
                    val phrasesArr = resultJson.optJSONArray("keyEmotionalPhrases")
                    if (phrasesArr != null) {
                        for (i in 0 until phrasesArr.length()) {
                            phrasesList.add(phrasesArr.optString(i))
                        }
                    }

                    val actionsList = mutableListOf<String>()
                    val actionsArr = resultJson.optJSONArray("actionItems")
                    if (actionsArr != null) {
                        for (i in 0 until actionsArr.length()) {
                            actionsList.add(actionsArr.optString(i))
                        }
                    }

                    return@withContext CallAnalysisResult(
                        category = resultJson.optString("category", "GENERAL"),
                        summary = resultJson.optString("summary", "Voicemail recorded for John Lanter."),
                        sentiment = resultJson.optString("sentiment", "NEUTRAL"),
                        sentimentScore = resultJson.optDouble("sentimentScore", 0.75).toFloat(),
                        emotionalTone = resultJson.optString("emotionalTone", "Neutral"),
                        keyEmotionalPhrases = phrasesList,
                        urgencyLevel = resultJson.optString("urgencyLevel", "MEDIUM"),
                        recruiterCompany = resultJson.optString("recruiterCompany").takeIf { it != "null" && it.isNotBlank() },
                        recruiterRole = resultJson.optString("recruiterRole").takeIf { it != "null" && it.isNotBlank() },
                        recruiterCallback = resultJson.optString("recruiterCallback").takeIf { it != "null" && it.isNotBlank() },
                        actionItems = actionsList,
                        trackerCardMessage = resultJson.optString("trackerCardMessage", "New voicemail recorded from $callerName.")
                    )
                }
            }
            return@withContext fallbackAnalyzeCall(transcript, callerName, callerNumber)
        } catch (e: Exception) {
            Log.e("GeminiScreener", "Error in analyzeCallTranscript", e)
            return@withContext fallbackAnalyzeCall(transcript, callerName, callerNumber)
        }
    }

    private fun fallbackSiennaReply(
        conversation: List<LiveTurn>,
        callerName: String?,
        contactCategory: String?
    ): String {
        val lastCallerTurn = conversation.lastOrNull { it.speaker == "Caller" }?.text?.lowercase() ?: ""

        return when {
            lastCallerTurn.contains("scam") || lastCallerTurn.contains("warranty") || lastCallerTurn.contains("irs") ||
            lastCallerTurn.contains("press 1") || lastCallerTurn.contains("credit card") -> {
                "John is not interested in solicitations. Please remove this number from your list. Goodbye."
            }
            lastCallerTurn.contains("recruiter") || lastCallerTurn.contains("role") || lastCallerTurn.contains("position") ||
            lastCallerTurn.contains("salary") || lastCallerTurn.contains("hiring") || contactCategory == "RECRUITER" -> {
                "That sounds like an exciting opportunity! John is definitely open to high-impact roles. What's the company name and your callback number so I can notify him immediately?"
            }
            lastCallerTurn.contains("mom") || lastCallerTurn.contains("dad") || lastCallerTurn.contains("dinner") ||
            lastCallerTurn.contains("friend") || lastCallerTurn.contains("weekend") || contactCategory == "FRIEND_FAMILY" -> {
                "Hey! So good of you to call. John's away from his phone right now, but I'll make sure he sees your note the moment he's back."
            }
            lastCallerTurn.contains("who is this") || lastCallerTurn.contains("who are you") -> {
                "I'm Sienna, John Lanter's personal assistant. I help him screen calls while he's busy. How can I help you today?"
            }
            else -> {
                "Got it, thanks for explaining! Let me make sure I have all the details down for John. Is there anything else you'd like me to pass along?"
            }
        }
    }

    private fun fallbackAnalyzeCall(
        transcript: String,
        callerName: String,
        callerNumber: String
    ): CallAnalysisResult {
        val lower = transcript.lowercase()
        val isSpam = lower.contains("warranty") || lower.contains("press 1") || lower.contains("irs") || lower.contains("scam") || lower.contains("solicitation")
        val isRecruiter = lower.contains("recruiter") || lower.contains("role") || lower.contains("engineer") || lower.contains("hiring") || lower.contains("apex") || lower.contains("talent")
        val isFamily = lower.contains("mom") || lower.contains("dad") || lower.contains("dinner") || lower.contains("love you") || lower.contains("honey") || lower.contains("family")

        return when {
            isSpam -> CallAnalysisResult(
                category = "SPAM",
                summary = "Automated solicitation detected from $callerNumber. Sienna ended the screening.",
                sentiment = "NEGATIVE",
                sentimentScore = 0.15f,
                emotionalTone = "Robotic & Suspicious",
                keyEmotionalPhrases = listOf("urgent notification", "warranty expiration"),
                urgencyLevel = "LOW",
                recruiterCompany = null,
                recruiterRole = null,
                recruiterCallback = null,
                actionItems = listOf("Number flagged as spam"),
                trackerCardMessage = "🚫 Auto-screened Spam blocked from $callerNumber."
            )
            isRecruiter -> CallAnalysisResult(
                category = "RECRUITER",
                summary = "$callerName reached out regarding an engineering opportunity. Details captured for John's priority review.",
                sentiment = "POSITIVE",
                sentimentScore = 0.90f,
                emotionalTone = "Professional & Enthusiastic",
                keyEmotionalPhrases = listOf("great background", "excited to connect", "leadership opportunity"),
                urgencyLevel = "HIGH",
                recruiterCompany = if (lower.contains("apex")) "Apex AI Systems" else "Tech Partner",
                recruiterRole = "Senior / Staff Software Engineer",
                recruiterCallback = callerNumber,
                actionItems = listOf("Call back $callerName at $callerNumber", "Review role compensation details"),
                trackerCardMessage = "💼 Recruiter Opportunity: $callerName left details for Staff Engineer role."
            )
            isFamily -> CallAnalysisResult(
                category = "FRIEND_FAMILY",
                summary = "$callerName left a warm personal message checking in with John.",
                sentiment = "POSITIVE",
                sentimentScore = 0.95f,
                emotionalTone = "Warm & Affectionate",
                keyEmotionalPhrases = listOf("love you", "checking in", "dinner"),
                urgencyLevel = "MEDIUM",
                recruiterCompany = null,
                recruiterRole = null,
                recruiterCallback = null,
                actionItems = listOf("Call back $callerName when free"),
                trackerCardMessage = "❤️ Personal Message from $callerName received."
            )
            else -> CallAnalysisResult(
                category = "GENERAL",
                summary = "Call received from $callerName ($callerNumber). Sienna captured the message.",
                sentiment = "NEUTRAL",
                sentimentScore = 0.70f,
                emotionalTone = "Calm & Inquisitive",
                keyEmotionalPhrases = listOf("calling for John", "leave a message"),
                urgencyLevel = "MEDIUM",
                recruiterCompany = null,
                recruiterRole = null,
                recruiterCallback = null,
                actionItems = listOf("Review voicemail transcript"),
                trackerCardMessage = "📞 Inbound call screened from $callerName."
            )
        }
    }
}
