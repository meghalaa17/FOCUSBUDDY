package com.example.a211393_nelson_lab01.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a211393_nelson_lab01.AppViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigateToStudy: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToPet:   () -> Unit,
    onNavigateToStats: () -> Unit,
    onExit: () -> Unit
) {
    // Remove Scaffold and NavigationBar — just keep the content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome back, ${viewModel.userName.value} 🐾",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
                Text(
                    "${viewModel.points.value} XP · Level ${viewModel.petLevel}",
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (viewModel.points.value % 100) / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "${viewModel.points.value % 100}/100 XP to Level ${viewModel.petLevel + 1}",
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        DashboardMenuGrid(
            onStudy = onNavigateToStudy,
            onTimer = onNavigateToTimer,
            onPet   = onNavigateToPet,
            onStats = onNavigateToStats
        )
    }
}

@Composable
fun DashboardMenuGrid(
    onStudy: () -> Unit,
    onTimer: () -> Unit,
    onPet:   () -> Unit,
    onStats: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            GridButton("\uD83D\uDCDA\nStudy", Modifier.weight(1f), onStudy)
            Spacer(Modifier.width(12.dp))
            GridButton("\u23F1\uFE0F\nTimer", Modifier.weight(1f), onTimer)
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            GridButton("\uD83D\uDC3E\nPet",   Modifier.weight(1f), onPet)
            Spacer(Modifier.width(12.dp))
            GridButton("\uD83D\uDCCA\nStats", Modifier.weight(1f), onStats)
        }
    }
}

@Composable
fun GridButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}