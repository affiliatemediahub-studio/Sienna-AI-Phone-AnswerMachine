package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CategoryFriendFamily
import com.example.ui.theme.CategoryRecruiter
import com.example.ui.theme.CategorySpam
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentNeutral
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.UrgencyCritical
import com.example.ui.theme.UrgencyHigh
import com.example.ui.theme.UrgencyLow
import com.example.ui.theme.UrgencyMedium

@Composable
fun SentimentBadge(
    sentiment: String,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val (bgColor, textColor, icon, label) = when (sentiment.uppercase()) {
        "POSITIVE" -> Quadruple(
            Color(0xFF064E3B),
            SentimentPositive,
            Icons.Default.SentimentSatisfied,
            "Positive Tone"
        )
        "NEGATIVE" -> Quadruple(
            Color(0xFF4C0519),
            SentimentNegative,
            Icons.Default.SentimentDissatisfied,
            "Negative / Spam"
        )
        else -> Quadruple(
            Color(0xFF1E3A5F),
            SentimentNeutral,
            Icons.Default.SentimentNeutral,
            "Neutral Tone"
        )
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            if (showLabel) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (category.uppercase()) {
        "RECRUITER" -> Triple(Color(0xFF312E81), CategoryRecruiter, "Recruiter Opportunity")
        "FRIEND_FAMILY" -> Triple(Color(0xFF064E3B), CategoryFriendFamily, "Friends & Family")
        "SPAM" -> Triple(Color(0xFF450A0A), CategorySpam, "Robo Spam Blocked")
        else -> Triple(Color(0xFF1E293B), Color(0xFF94A3B8), "General Call")
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun UrgencyBadge(
    urgency: String,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (urgency.uppercase()) {
        "CRITICAL" -> Pair(UrgencyCritical, "Critical Urgency")
        "HIGH" -> Pair(UrgencyHigh, "High Priority")
        "MEDIUM" -> Pair(UrgencyMedium, "Medium")
        else -> Pair(UrgencyLow, "Normal")
    }

    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
