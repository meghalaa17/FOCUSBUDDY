package com.example.a211393_nelson_lab01.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a211393_nelson_lab01.AppViewModel

// ================================================================
// NEW SCREEN (Screen 7 of 7)
// Shows community study goals stored in Firebase Firestore.
//
// HOW THE DATA FLOWS HERE:
// 1. FirestoreRepository.getCommunityGoals() returns a Flow
// 2. Firestore adds a real-time listener to the collection
// 3. Whenever ANY user posts a goal, Firestore pushes the update
// 4. The Flow emits the new list
// 5. StateFlow in ViewModel emits it
// 6. collectAsState() here recomposes automatically
//
// This is REAL-TIME — no refresh button needed.
// ================================================================

@Composable
fun CommunityScreen(viewModel: AppViewModel, onBack: () -> Unit) {

    // collectAsState() — same pattern as DailyTipScreen.
    // communityGoals is a StateFlow<List<CommunityGoal>> in the ViewModel.
    val goals by viewModel.communityGoals.collectAsState()

    var newGoal    by remember { mutableStateOf("") }
    var newSubject by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        // --- Post a new goal ---
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Share Your Study Goal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "Visible to all Focus Buddy users",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = newGoal,
                    onValueChange = { newGoal = it },
                    label = { Text("My goal today") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = newSubject,
                    onValueChange = { newSubject = it },
                    label = { Text("Subject (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (newGoal.isNotBlank()) {
                            // This calls FirestoreRepository.postGoal()
                            // inside a coroutine in the ViewModel.
                            // The new goal immediately appears in the
                            // list because Firestore's listener fires.
                            viewModel.postCommunityGoal(newGoal, newSubject)
                            newGoal    = ""
                            newSubject = ""
                        }
                    },
                    enabled = newGoal.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Post to Community \uD83C\uDF0D")
                }
            }
        }

        // --- Community goals list ---
        Text(
            "Community Goals (${goals.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))

        if (goals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83C\uDF0D", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No community goals yet.")
                    Text(
                        "Be the first to share!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            // LazyColumn only renders items visible on screen.
            // For long lists this is much more efficient than
            // a regular Column with forEach.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(goals, key = { it.id }) { goal ->
                    // key = { it.id } tells Compose to identify each
                    // item by its Firestore document ID. This lets
                    // Compose animate additions/removals correctly.
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        goal.userName.firstOrNull()?.toString() ?: "?",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(goal.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(goal.goal, fontSize = 13.sp)
                                if (goal.subject.isNotBlank()) {
                                    Text(
                                        "\uD83D\uDCDA ${goal.subject}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}