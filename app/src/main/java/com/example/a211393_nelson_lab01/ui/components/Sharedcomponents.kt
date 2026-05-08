package com.example.a211393_nelson_lab01.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =======================
// STAT CHIP
// Used in: StatsScreen — shows a single XP / level / count value
// =======================
@Composable
fun StatChip(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = value,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary
        )
        Text(
            text     = label,
            fontSize = 11.sp,
            color    = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// =======================
// PERFORMANCE CARD
// Used in: StatsScreen — expandable card showing a stat with detail text
// =======================
@Composable
fun PerformanceCard(
    icon: String,
    title: String,
    value: String,
    detail: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape    = RoundedCornerShape(8.dp),
                    color    = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(icon, color = Color.White, fontSize = 16.sp)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text  = title,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = value,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text     = if (expanded) "\u25B2" else "\u25BC",
                    fontSize = 12.sp
                )
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(text = detail, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

// =======================
// SECTION HEADER
// Used in: any screen — consistent bold section title with optional subtitle
// =======================
@Composable
fun SectionHeader(title: String, subtitle: String = "") {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text       = title,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp
        )
        if (subtitle.isNotBlank()) {
            Text(
                text     = subtitle,
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =======================
// EMPTY STATE
// Used in: any screen — shown when a list is empty
// =======================
@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = message,
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =======================
// XP PROGRESS CARD
// Used in: DashboardScreen, PetGrowthScreen
// Shows current XP bar + level info in a reusable card
// =======================
@Composable
fun XpProgressCard(points: Int, petLevel: Int) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Level $petLevel",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
                Text(
                    text     = "$points XP total",
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (points % 100) / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = "${points % 100}/100 XP to Level ${petLevel + 1}",
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =======================
// XP BADGE
// Used in: anywhere you want to show a small "+X XP" pill
// =======================
@Composable
fun XpBadge(xp: Int) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text     = "+$xp XP",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color    = MaterialTheme.colorScheme.primary
        )
    }
}