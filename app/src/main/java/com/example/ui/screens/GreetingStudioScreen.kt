package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.PhoneCallback
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BentoCard
import com.example.ui.components.GreetingManager
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.CategoryBusiness
import com.example.ui.theme.CategoryFriendFamily
import com.example.ui.theme.CategoryRecruiter
import com.example.ui.theme.CategorySpam
import com.example.ui.theme.SiennaAccent
import com.example.ui.theme.SiennaPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.SiennaViewModel

@Composable
fun GreetingStudioScreen(
    viewModel: SiennaViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSimulator: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Full Greeting Manager Component (Record Voice, Upload Audio, AI Script TTS, Caller Targeting, Bento Library)
            item {
                GreetingManager(
                    viewModel = viewModel,
                    onNavigateToSimulator = onNavigateToSimulator
                )
            }

            // 2. Dynamic Routing Intelligence Bento Box
            item {
                BentoCard(
                    title = "Caller Matching & Dynamic Routing Logic",
                    subtitle = "How Sienna determines which greeting to play during incoming calls",
                    icon = Icons.Default.Schedule,
                    iconTint = SiennaAccent,
                    backgroundColor = BentoSurface
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        RoutingRuleCard(
                            icon = Icons.Default.Star,
                            iconColor = CategoryBusiness,
                            title = "1. Specific Contact Override (Highest Priority)",
                            desc = "If a custom greeting is assigned directly to a contact in your directory, it overrides all general rules when they call."
                        )

                        RoutingRuleCard(
                            icon = Icons.Default.Business,
                            iconColor = CategoryRecruiter,
                            title = "2. Caller Category Rule (Recruiter / VIP / Friend)",
                            desc = "Matches caller profile (e.g. Talent Lead, Recruiter, Family) and activates designated voice note or screening script."
                        )

                        RoutingRuleCard(
                            icon = Icons.Default.Security,
                            iconColor = CategorySpam,
                            title = "3. Spam & Robocall Defense Shield",
                            desc = "Automated deflector greeting with verification prompts to block unsolicited robo-calls from reaching John."
                        )

                        RoutingRuleCard(
                            icon = Icons.Default.PhoneCallback,
                            iconColor = SiennaAccent,
                            title = "4. Global Default Greeting (Fallback)",
                            desc = "Used when no specific caller match is found. Plays the active general greeting."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutingRuleCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    desc: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BentoSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(iconColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
