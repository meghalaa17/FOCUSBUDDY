package com.example.a211393_nelson_lab01.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a211393_nelson_lab01.AppViewModel

@Composable
fun PetGrowthScreen(viewModel: AppViewModel, onBack: () -> Unit) {

    var petNameInput by remember { mutableStateOf(viewModel.petName.value) }
    var editingName  by remember { mutableStateOf(false) }
    val scrollState  = rememberScrollState()

    // Pet emoji and title based on level
    val (petEmoji, petTitle) = when {
        viewModel.petLevel >= 5 -> "\uD83D\uDC09\uD83C\uDF93" to "Scholar Dragon"
        viewModel.petLevel >= 4 -> "\uD83D\uDC15\u200D\uD83E\uDDBA\uD83C\uDF93" to "Graduate Buddy"
        viewModel.petLevel >= 3 -> "\uD83D\uDC36\uD83D\uDCDA" to "Studious Buddy"
        viewModel.petLevel >= 2 -> "\uD83D\uDC36" to "Focused Buddy"
        else                    -> "\uD83D\uDC36" to "Baby Buddy"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Pet display card ──────────────────────────────────────
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text      = petEmoji,
                    fontSize  = 100.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))

                if (editingName) {
                    OutlinedTextField(
                        value         = petNameInput,
                        onValueChange = { petNameInput = it },
                        label         = { Text("Pet name") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            viewModel.setPetName(petNameInput)
                            editingName = false
                        }) { Text("Save") }
                        OutlinedButton(onClick = {
                            petNameInput = viewModel.petName.value
                            editingName  = false
                        }) { Text("Cancel") }
                    }
                } else {
                    Text(
                        text       = viewModel.petName.value,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text     = petTitle,
                        fontSize = 14.sp,
                        color    = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { editingName = true }) {
                        Text("Rename pet \u270F\uFE0F")
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── XP / level card ───────────────────────────────────────
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Level ${viewModel.petLevel}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
                    )
                    Text(
                        "${viewModel.points.value} XP total",
                        color      = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 13.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (viewModel.points.value % 100) / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${viewModel.points.value % 100}/100 XP to Level ${viewModel.petLevel + 1}",
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Quick stats row ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Documents
            ElevatedCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("\uD83D\uDCC5", fontSize = 24.sp)
                    Text(
                        "${viewModel.studySlots.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        color      = MaterialTheme.colorScheme.primary
                    )
                    Text("Slots", fontSize = 11.sp)
                }
            }
            // Flashcards
            ElevatedCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("\uD83C\uDCA0", fontSize = 24.sp)
                    Text(
                        "${viewModel.flashcards.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        color      = MaterialTheme.colorScheme.primary
                    )
                    Text("Cards", fontSize = 11.sp)
                }
            }
            // Sessions
            ElevatedCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("\u23F1\uFE0F", fontSize = 24.sp)
                    Text(
                        "${viewModel.sessionHistory.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        color      = MaterialTheme.colorScheme.primary
                    )
                    Text("Sessions", fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Evolution milestones ──────────────────────────────────
        Text(
            "Evolution Milestones",
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            modifier   = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        listOf(
            Triple(1, "Baby Buddy \uD83D\uDC36",              "Start — 0 XP"),
            Triple(2, "Focused Buddy \uD83D\uDC36",           "100 XP"),
            Triple(3, "Studious Buddy \uD83D\uDC36\uD83D\uDCDA", "200 XP"),
            Triple(4, "Graduate Buddy \uD83C\uDF93",          "300 XP"),
            Triple(5, "Scholar Dragon \uD83D\uDC09",          "400 XP")
        ).forEach { (level, title, requirement) ->
            val unlocked = viewModel.petLevel >= level
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (unlocked)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (unlocked) "\u2705" else "\uD83D\uDD12",
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            requirement,
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (unlocked) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                "Unlocked",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color    = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Interact button ───────────────────────────────────────
        Button(
            onClick  = { },
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Interact with ${viewModel.petName.value} \uD83D\uDC3E",
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}