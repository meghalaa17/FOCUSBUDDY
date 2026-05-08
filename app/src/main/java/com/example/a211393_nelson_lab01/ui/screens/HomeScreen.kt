package com.example.a211393_nelson_lab01.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onLoginClick: () -> Unit, onEnterDashboard: (String) -> Unit) { //param for name
    //local state of text field
    var name by remember { mutableStateOf("") }

    //places all up and down or vertically
    Column(
        modifier = Modifier //style for every composition
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row( //places name and login button side by side
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically //align in middle
        ) {
            Text(
                "Focus Buddy \uD83D\uDC3E",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            //navigate to login screen
            TextButton(onClick = onLoginClick) {
                Text("Login | Sign up")
            }
        }

        Text("Matric No: A211393", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Start your session", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Enter your name to begin tracking focus points.", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Your name...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onEnterDashboard(name) },//pash name to dashboard
                    enabled = name.isNotBlank(), // disable enter button
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enter Dashboard")
                }
            }
        }
    }
}