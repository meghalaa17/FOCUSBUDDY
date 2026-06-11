package com.example.a211393_nelson_lab01.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a211393_nelson_lab01.AppViewModel

// ================================================================
// NEW SCREEN (Screen 6 of 7)
// Shows a motivational quote fetched live from ZenQuotes API.
//
// HOW THE DATA FLOWS HERE:
// 1. AppViewModel.init{} calls fetchDailyQuote() when app starts
// 2. Retrofit makes an HTTP GET to zenquotes.io/api/random
// 3. JSON response is parsed into QuoteResponse
// 4. viewModel.dailyQuote.value is updated
// 5. collectAsState() below detects the change and recomposes
// ================================================================

@Composable
fun DailyTipScreen(viewModel: AppViewModel, onBack: () -> Unit) {

    // collectAsState() converts StateFlow/MutableState into
    // Compose state. When the ViewModel's value changes,
    // this composable automatically redraws.
    val quote by viewModel.dailyQuote
    val isLoading by viewModel.isQuoteLoading
    val error by viewModel.quoteError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Daily Motivation \uD83C\uDF1F",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Your quote for today",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        // STATE-BASED UI: show different UI depending on the
        // current loading/error/success state
        when {
            isLoading -> {
                // Show a spinner while waiting for the API response
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Fetching your quote...", fontSize = 13.sp)
            }

            error != null -> {
                // Network error — show fallback UI
                Text("\uD83D\uDCF5", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.fetchDailyQuote() }) {
                    Text("Try Again")
                }
            }

            quote != null -> {
                // Success — display the quote
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("\u201C", fontSize = 64.sp, color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = quote!!.q,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "— ${quote!!.a}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { viewModel.fetchDailyQuote() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Get New Quote \uD83D\uDD04")
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Study tip cards (static content, always shown)
        Text(
            "Study Tips",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        listOf(
            "\uD83C\uDF85" to "Pomodoro Technique" to "Study for 25 minutes, rest for 5. Your brain consolidates memory during breaks.",
            "\uD83D\uDCA7" to "Stay Hydrated" to "Even mild dehydration reduces concentration by up to 20%.",
            "\uD83D\uDCA4" to "Sleep Matters" to "Sleep is when your brain transfers short-term to long-term memory.",
            "\uD83D\uDCF5" to "Limit Distractions" to "Put your phone face-down. Notifications split your attention even when ignored."
        ).forEach { (header, detail) ->
            val (icon, title) = header
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(icon, fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.SemiBold)
                        Text(detail, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}