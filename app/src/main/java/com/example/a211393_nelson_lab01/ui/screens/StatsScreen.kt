package com.example.a211393_nelson_lab01.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a211393_nelson_lab01.AppViewModel
import com.example.a211393_nelson_lab01.ui.components.PerformanceCard
import com.example.a211393_nelson_lab01.ui.components.StatChip

@Composable
fun StatsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // ── Overview card ─────────────────────────────────────────
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatChip("${viewModel.points.value}", "Total XP")
                    StatChip("${viewModel.petLevel}", "Pet Level")
                    StatChip("${viewModel.sessionHistory.size}", "Sessions")
                    StatChip("${viewModel.studySlots.size}", "Slots")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── XP bar chart ──────────────────────────────────────────
        Text("XP Progress", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                val bars = listOf(
                    "Mon" to 0.3f, "Tue" to 0.5f, "Wed" to 0.4f,
                    "Thu" to 0.7f, "Fri" to 0.6f, "Sat" to 0.9f, "Sun" to 0.2f
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    bars.forEach { (day, h) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier
                                    .width(24.dp)
                                    .fillMaxHeight(h)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(day, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Performance expandable cards ──────────────────────────
        Text("Performance", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))

        PerformanceCard(
            icon   = "\u23F1\uFE0F",
            title  = "Focus time",
            value  = "${viewModel.sessionHistory.filter { it.type == "Timer" }.size * 25}m",
            detail = "Each completed Pomodoro = 25 minutes of focused study. Keep it up!"
        )
        PerformanceCard(
            icon   = "\uD83E\uDDE0",
            title  = "Quiz score",
            value  = "${viewModel.quizScore.value}/3",
            detail = "Your last quiz score. Retake quizzes to improve and earn more XP."
        )
        PerformanceCard(
            icon   = "\uD83C\uDFC6",
            title  = "Flashcards created",
            value  = "${viewModel.flashcards.size}",
            detail = "Creating flashcards earns 5 XP each. Reviewing them reinforces memory."
        )
        PerformanceCard(
            icon   = "\uD83D\uDCC5",
            title  = "Study slots scheduled",
            value  = "${viewModel.studySlots.size}",
            detail = "Schedule your study sessions by subject and day to build a consistent study habit."
        )

        Spacer(Modifier.height(20.dp))

        // ── Session history ───────────────────────────────────────
        Text(
            "Session History (${viewModel.sessionHistory.size})",
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp
        )
        Spacer(Modifier.height(8.dp))

        if (viewModel.sessionHistory.isEmpty()) {
            Text(
                "No sessions yet. Complete a timer or quiz to see your history here.",
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        } else {
            viewModel.sessionHistory.reversed().forEach { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            when (session.type) {
                                "Timer"     -> "\u23F1\uFE0F"
                                "Quiz"      -> "\uD83E\uDDE0"
                                "Flashcard" -> "\uD83C\uDCA0"
                                else        -> "\uD83D\uDCDA"
                            },
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(session.type, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                session.date,
                                fontSize = 11.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "+${session.xpEarned} XP",
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── My Schedule ── ADD + DISPLAY requirement ──────────────
        // Slots added on StudySessionScreen, displayed here
        Text(
            "My Schedule (${viewModel.studySlots.size})",
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp
        )
        Spacer(Modifier.height(8.dp))

        if (viewModel.studySlots.isEmpty()) {
            Text(
                "No slots yet. Add a study slot in the Schedule tab.",
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        } else {
            viewModel.studySlots.forEach { slot ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape    = RoundedCornerShape(8.dp),
                            color    = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    when (slot.subject) {
                                        "Math"      -> "\uD83D\uDCCA"
                                        "Science"   -> "\uD83D\uDD2C"
                                        "History"   -> "\uD83C\uDFDB\uFE0F"
                                        "Biology"   -> "\uD83E\uDDEC"
                                        "English"   -> "\uD83D\uDCDD"
                                        "Physics"   -> "\u26A1"
                                        "Chemistry" -> "\uD83E\uDDEA"
                                        else        -> "\uD83D\uDCDA"
                                    },
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                slot.subject,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 14.sp
                            )
                            Text(
                                "\uD83D\uDCC5 ${slot.day}",
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "\u23F0 ${slot.time}  \u00B7  \u23F1\uFE0F ${slot.duration}",
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                slot.subject,
                                modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize   = 10.sp,
                                color      = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}