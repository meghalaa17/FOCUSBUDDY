package com.example.a211393_nelson_lab01.ui.screens

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
import com.example.a211393_nelson_lab01.AppViewModel
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val totalSeconds = 1500
    var timeLeft     by remember { mutableStateOf(totalSeconds) }
    var timerRunning by remember { mutableStateOf(false) }
    var sessionsDone by remember { mutableStateOf(0) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
            if (timeLeft % 60 == 0 && timeLeft != totalSeconds) {
                viewModel.addPoints(1)
            }
        }
        if (timerRunning && timeLeft == 0) {
            timerRunning = false
            sessionsDone++
            viewModel.addPoints(25)
            viewModel.logSession("Timer", 25, 25)
            timeLeft = totalSeconds
        }
    }

    val progress = timeLeft.toFloat() / totalSeconds.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pomodoro Timer", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Sessions done today: $sessionsDone",
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(40.dp))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress    = { progress },
                modifier    = Modifier.size(200.dp),
                strokeWidth = 10.dp,
                trackColor  = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text       = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                fontSize   = 48.sp,
                fontWeight = FontWeight.Black,
                color      = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick  = { timerRunning = !timerRunning },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (timerRunning) Color(0xFFF44336) else MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if (timerRunning) "Pause" else "Start Focus",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick  = { timerRunning = false; timeLeft = totalSeconds },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }

        Spacer(Modifier.height(24.dp))

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn("${viewModel.points.value}", "Total XP")
                StatColumn("$sessionsDone", "Sessions")
                StatColumn("Lv ${viewModel.petLevel}", "Pet Level")
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 12.sp)
    }
}