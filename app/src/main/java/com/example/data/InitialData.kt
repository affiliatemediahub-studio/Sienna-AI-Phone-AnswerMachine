package com.example.data

import com.example.data.model.ContactEntity
import com.example.data.model.GreetingEntity
import com.example.data.model.VoicemailEntity

object InitialData {
    suspend fun populateDatabase(database: AppDatabase) {
        val greetingDao = database.greetingDao()
        val contactDao = database.contactDao()
        val voicemailDao = database.voicemailDao()

        // 1. Initial Greetings
        val greetings = listOf(
            GreetingEntity(
                id = 1,
                title = "Sienna Standard Screening (Active)",
                messageText = "Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?",
                voiceType = "Sienna Casual",
                isActive = true,
                routingRule = "DEFAULT"
            ),
            GreetingEntity(
                id = 2,
                title = "Recruiter Priority Filter",
                messageText = "Hi there, you've reached John Lanter's line. I'm Sienna, his assistant. John is currently exploring new engineering leadership opportunities but is tied up right now. Please let me know your company, the role, and the best number to reach you!",
                voiceType = "Sienna Executive",
                isActive = false,
                routingRule = "BUSINESS_HOURS"
            ),
            GreetingEntity(
                id = 3,
                title = "After-Hours & Weekend",
                messageText = "Hey! You've reached John Lanter's phone. This is Sienna, his assistant. John is offline for the evening. If it's urgent, leave a quick voice note and I'll notify his Call Tracker right away.",
                voiceType = "Casual Friendly",
                isActive = false,
                routingRule = "AFTER_HOURS"
            )
        )
        greetingDao.insertAll(greetings)

        // 2. Initial Contacts
        val contacts = listOf(
            ContactEntity(
                id = 1,
                name = "Sarah Jenkins",
                phoneNumber = "+1 (415) 890-2341",
                category = "RECRUITER",
                company = "Apex AI Systems",
                relationship = "Headhunter / Talent Lead",
                voiceProfileNotes = "Fast-paced professional tone, Silicon Valley accent",
                handlingRule = "RECRUITER_SCREEN",
                avatarColor = 0xFF6366F1
            ),
            ContactEntity(
                id = 2,
                name = "Mom (Eleanor Lanter)",
                phoneNumber = "+1 (512) 441-9082",
                category = "FRIEND_FAMILY",
                company = null,
                relationship = "Family / Mother",
                voiceProfileNotes = "Warm Texas accent, recognized family caller",
                handlingRule = "WARM_FRIEND",
                avatarColor = 0xFF10B981
            ),
            ContactEntity(
                id = 3,
                name = "Marcus Vance",
                phoneNumber = "+1 (206) 554-1189",
                category = "RECRUITER",
                company = "CloudScale Tech",
                relationship = "Executive Recruiter",
                voiceProfileNotes = "Crisp tone, calling regarding Principal Architect role",
                handlingRule = "RECRUITER_SCREEN",
                avatarColor = 0xFF8B5CF6
            ),
            ContactEntity(
                id = 4,
                name = "Suspected Telemarketer",
                phoneNumber = "+1 (800) 993-4120",
                category = "SPAM",
                company = "Auto Warranty Scam",
                relationship = "Spammer",
                voiceProfileNotes = "Automated robocall cadence, background call center hum",
                handlingRule = "SPAM_BLOCK",
                avatarColor = 0xFFEF4444
            ),
            ContactEntity(
                id = 5,
                name = "Dave Miller",
                phoneNumber = "+1 (650) 332-9011",
                category = "FRIEND_FAMILY",
                company = "Weekend Tennis Club",
                relationship = "Friend / Gym Partner",
                voiceProfileNotes = "Casual voice, friendly demeanor",
                handlingRule = "WARM_FRIEND",
                avatarColor = 0xFF06B6D4
            )
        )
        contactDao.insertAll(contacts)

        // 3. Initial Voicemails with Sentiment & John's Call Tracker notifications
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 3600 * 1000L
        val voicemails = listOf(
            VoicemailEntity(
                id = 1,
                callerName = "Sarah Jenkins",
                callerNumber = "+1 (415) 890-2341",
                callerRelationship = "Recruiter",
                timestamp = now - (15 * 60 * 1000), // 15 mins ago (Today)
                durationSeconds = 42,
                transcript = "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nSarah Jenkins: Hi Sienna! This is Sarah Jenkins from Apex AI Systems. I'm reaching out because we're putting together a founding Staff Mobile Platform team and John's background in distributed mobile architecture looks fantastic. The role offers 220k-260k plus substantial equity. John can reach me back at 415-890-2341 or email s.jenkins@apexai.io. Looking forward to connecting!\n\nSienna: Got it Sarah, thanks so much! John is actively considering high-impact roles, so I will get this over to his Call Tracker immediately. Have a great day!",
                summary = "Sarah from Apex AI Systems called offering a Staff Mobile Platform role ($220k-$260k + equity). Highly enthusiastic about John's background.",
                category = "RECRUITER",
                sentiment = "POSITIVE",
                sentimentScore = 0.95f,
                emotionalTone = "Enthusiastic & Professional",
                keyEmotionalPhrases = "looks fantastic, founding Staff team, substantial equity, looking forward to connecting",
                urgencyLevel = "HIGH",
                recruiterCompany = "Apex AI Systems",
                recruiterRole = "Staff Mobile Platform Engineer",
                recruiterCallback = "+1 (415) 890-2341",
                actionItems = "Call back Sarah Jenkins before 5 PM today,Review Apex AI job specs",
                sentToCallTracker = true,
                trackerSentTimestamp = now - (14 * 60 * 1000),
                audioWaveform = "30,55,75,40,90,85,60,95,80,65,90,75,40,85,60,50,70,80,60,40,25",
                audioFilePath = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_20260817_sarah_jenkins.m4a",
                audioFileUrl = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_sarah_apexai.m4a",
                isRead = false,
                isStarred = true
            ),
            VoicemailEntity(
                id = 2,
                callerName = "Mom (Eleanor Lanter)",
                callerNumber = "+1 (512) 441-9082",
                callerRelationship = "Friend & Family",
                timestamp = now - (2 * 3600 * 1000), // 2 hours ago (Today)
                durationSeconds = 28,
                transcript = "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nMom: Oh hey Sienna, tell my boy John I called! Just checking if he's coming over for Sunday dinner around 6. Dad is making his famous smoked brisket. Call me back when you're free honey, love you!\n\nSienna: Hey Mrs. Lanter! So good to hear from you. I'll make sure John sees this note right away. Have fun with the brisket!",
                summary = "Mom called checking if John is attending Sunday dinner at 6 PM (Dad is making smoked brisket). Casual and loving check-in.",
                category = "FRIEND_FAMILY",
                sentiment = "POSITIVE",
                sentimentScore = 0.92f,
                emotionalTone = "Warm, Affectionate & Loving",
                keyEmotionalPhrases = "famous smoked brisket, love you honey, Sunday dinner around 6",
                urgencyLevel = "LOW",
                recruiterCompany = null,
                recruiterRole = null,
                recruiterCallback = null,
                actionItems = "Confirm Sunday dinner attendance with Mom",
                sentToCallTracker = true,
                trackerSentTimestamp = now - (2 * 3600 * 1000) + 30000,
                audioWaveform = "20,35,50,45,60,70,55,65,50,60,45,40,55,50,35,30,20,15",
                audioFilePath = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_20260817_mom_eleanor.m4a",
                audioFileUrl = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_mom_brisket.m4a",
                isRead = true,
                isStarred = false
            ),
            VoicemailEntity(
                id = 3,
                callerName = "Unknown Robo Spammer",
                callerNumber = "+1 (800) 993-4120",
                callerRelationship = "Spam",
                timestamp = now - (5 * 3600 * 1000), // 5 hours ago (Today)
                durationSeconds = 12,
                transcript = "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nAutomated Voice: This is an urgent final notification regarding your vehicle manufacturer warranty expiration. Press 1 now to speak with an agent...\n\nSienna: This sounds like an automated solicitation. Goodbye.",
                summary = "Robo-call spam attempting vehicle warranty phishing. Sienna terminated the call after 12 seconds.",
                category = "SPAM",
                sentiment = "NEGATIVE",
                sentimentScore = 0.15f,
                emotionalTone = "Robotic, Suspicious & Solicitation",
                keyEmotionalPhrases = "urgent final notification, manufacturer warranty expiration, press 1 now",
                urgencyLevel = "LOW",
                recruiterCompany = null,
                recruiterRole = null,
                recruiterCallback = null,
                actionItems = "Auto-blocked spam number",
                sentToCallTracker = true,
                trackerSentTimestamp = now - (5 * 3600 * 1000) + 15000,
                audioWaveform = "60,60,60,60,60,60,60,10,10,5",
                audioFilePath = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_20260817_spam_warranty.m4a",
                audioFileUrl = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_spam_blocked.m4a",
                isRead = true,
                isStarred = false
            ),
            VoicemailEntity(
                id = 4,
                callerName = "Marcus Vance",
                callerNumber = "+1 (206) 554-1189",
                callerRelationship = "Recruiter",
                timestamp = now - dayMillis, // Yesterday
                durationSeconds = 35,
                transcript = "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nMarcus Vance: Hey Sienna, Marcus from CloudScale Tech. We're looking for a Lead AI Systems Architect to spearhead our next-gen agent orchestration engine. Fully remote, flexible comp. John's profile was referred by our VP of Eng. Give me a buzz back at 206-554-1189.\n\nSienna: Thank you Marcus! I have noted CloudScale Tech and the Lead AI Architect role. I will ping John's tracker right now.",
                summary = "Marcus from CloudScale Tech called regarding a fully remote Lead AI Systems Architect role referred by their VP of Eng.",
                category = "RECRUITER",
                sentiment = "POSITIVE",
                sentimentScore = 0.88f,
                emotionalTone = "Direct, Respectful & Promising",
                keyEmotionalPhrases = "Lead AI Systems Architect, referred by VP of Eng, fully remote",
                urgencyLevel = "MEDIUM",
                recruiterCompany = "CloudScale Tech",
                recruiterRole = "Lead AI Systems Architect",
                recruiterCallback = "+1 (206) 554-1189",
                actionItems = "Follow up with Marcus at CloudScale Tech",
                sentToCallTracker = true,
                trackerSentTimestamp = now - dayMillis + 40000,
                audioWaveform = "25,40,70,80,65,90,85,60,75,90,50,60,70,85,60,40,30",
                audioFilePath = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_20260816_marcus_vance.m4a",
                audioFileUrl = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_marcus_cloudscale.m4a",
                isRead = true,
                isStarred = true
            ),
            VoicemailEntity(
                id = 5,
                callerName = "Dave Miller",
                callerNumber = "+1 (650) 332-9011",
                callerRelationship = "Friend & Family",
                timestamp = now - (4 * dayMillis), // 4 days ago (Last 7 Days)
                durationSeconds = 22,
                transcript = "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nDave Miller: What's up John! Dave here. We've got the court booked for doubles Saturday morning at 9 AM. Let me know if you can bring the Wilson balls. Catch you later buddy!\n\nSienna: Got it Dave! I will add the Saturday tennis details directly to John's tracker summary.",
                summary = "Dave Miller confirmed tennis doubles match this Saturday at 9 AM, asked John to bring tennis balls.",
                category = "FRIEND_FAMILY",
                sentiment = "POSITIVE",
                sentimentScore = 0.90f,
                emotionalTone = "Upbeat, Casual & Energetic",
                keyEmotionalPhrases = "court booked for doubles, Saturday morning at 9 AM, catch you later",
                urgencyLevel = "LOW",
                recruiterCompany = null,
                recruiterRole = null,
                recruiterCallback = "+1 (650) 332-9011",
                actionItems = "Confirm tennis Saturday 9 AM, pack tennis balls",
                sentToCallTracker = true,
                trackerSentTimestamp = now - (4 * dayMillis) + 25000,
                audioWaveform = "30,45,60,80,75,50,65,70,55,40,35,20",
                audioFilePath = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_20260813_dave_tennis.m4a",
                audioFileUrl = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_dave_tennis.m4a",
                isRead = true,
                isStarred = false
            ),
            VoicemailEntity(
                id = 6,
                callerName = "Elena Rostova",
                callerNumber = "+1 (415) 772-9104",
                callerRelationship = "Recruiter",
                timestamp = now - (14 * dayMillis), // 14 days ago (Last 30 Days)
                durationSeconds = 48,
                transcript = "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nElena Rostova: Good afternoon Sienna, this is Elena Rostova, VP of Engineering at QuantumStream. We are hiring a VP of Architecture & Mobile Experience. We loved John's conference keynote on Kotlin Multiplatform. Base is 290k with meaningful executive bonuses. Please have John reach me directly at 415-772-9104 or elena@quantumstream.dev.\n\nSienna: Thank you Elena! That is a very compelling leadership role. I am flagging this high priority on John's tracker.",
                summary = "Elena Rostova (VP of Eng @ QuantumStream) offering VP of Architecture & Mobile Experience role ($290k base + bonuses) after seeing John's keynote.",
                category = "RECRUITER",
                sentiment = "POSITIVE",
                sentimentScore = 0.98f,
                emotionalTone = "Executive, Highly Professional & Urgent",
                keyEmotionalPhrases = "loved John's conference keynote, meaningful executive bonuses, VP of Architecture",
                urgencyLevel = "CRITICAL",
                recruiterCompany = "QuantumStream",
                recruiterRole = "VP of Architecture & Mobile Experience",
                recruiterCallback = "+1 (415) 772-9104",
                actionItems = "Review QuantumStream VP opportunity, Schedule intro call with Elena",
                sentToCallTracker = true,
                trackerSentTimestamp = now - (14 * dayMillis) + 50000,
                audioWaveform = "40,65,85,90,75,85,95,90,70,80,65,85,70,60,45,30",
                audioFilePath = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_20260803_elena_quantumstream.m4a",
                audioFileUrl = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_elena_quantumstream.m4a",
                isRead = true,
                isStarred = true
            ),
            VoicemailEntity(
                id = 7,
                callerName = "Dr. Robert Sterling",
                callerNumber = "+1 (512) 330-8800",
                callerRelationship = "Business",
                timestamp = now - (22 * dayMillis), // 22 days ago (Last 30 Days)
                durationSeconds = 31,
                transcript = "Sienna: Hello, you have reached John Lanter's number but he is away from his phone at this time. This is Sienna his Assistant, can I ask for the reason of this call?\n\nDr. Robert Sterling: Hi John, Dr. Sterling's clinic calling to confirm your annual executive dental checkup and cleaning scheduled for next Tuesday at 10:30 AM. If you need to reschedule, please call our front desk at 512-330-8800.\n\nSienna: Thank you Dr. Sterling's office. I have noted the appointment on John's tracker schedule.",
                summary = "Dental clinic reminder confirming annual executive dental checkup next Tuesday at 10:30 AM.",
                category = "BUSINESS",
                sentiment = "NEUTRAL",
                sentimentScore = 0.70f,
                emotionalTone = "Professional & Informative",
                keyEmotionalPhrases = "annual executive dental checkup, scheduled for next Tuesday at 10:30 AM",
                urgencyLevel = "MEDIUM",
                recruiterCompany = "Sterling Dental Partners",
                recruiterRole = null,
                recruiterCallback = "+1 (512) 330-8800",
                actionItems = "Confirm dental appointment on calendar",
                sentToCallTracker = true,
                trackerSentTimestamp = now - (22 * dayMillis) + 35000,
                audioWaveform = "20,30,40,50,45,60,55,40,30,25,20",
                audioFilePath = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_20260726_dr_sterling.m4a",
                audioFileUrl = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_dr_sterling.m4a",
                isRead = true,
                isStarred = false
            )
        )
        voicemailDao.insertAll(voicemails)
    }
}
