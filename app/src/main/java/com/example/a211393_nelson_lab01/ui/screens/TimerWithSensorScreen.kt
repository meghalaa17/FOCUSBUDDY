package com.example.a211393_nelson_lab01.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a211393_nelson_lab01.AppViewModel
import kotlinx.coroutines.delay
import kotlin.math.sqrt

// ================================================================
// SENSOR INTEGRATION — Accelerometer
//
// The accelerometer measures force (in m/s²) on three axes:
//   X = left/right tilt
//   Y = forward/back tilt
//   Z = flat/upright (gravity = ~9.8 when face-up on desk)
//
// HOW SHAKE DETECTION WORKS:
// When the phone is shaken, all three axes spike sharply.
// We calculate the total magnitude: sqrt(x² + y² + z²)
// At rest: magnitude ≈ 9.8 (just gravity)
// When shaken: magnitude >> 9.8
// If magnitude > threshold AND the timer is running → shake detected
//
// HOW COMPOSE + SENSOR CONNECT:
// SensorManager is an Android system service (not Compose-aware).
// We use DisposableEffect to:
//   1. Register the listener when the composable enters the screen
//   2. Unregister when the composable leaves (no memory leak)
// ================================================================

@Composable
fun TimerWithSensorScreen(viewModel: AppViewModel, onBack: () -> Unit) {

    val context = LocalContext.current

    val totalSeconds = 1500
    var timeLeft     by remember { mutableStateOf(totalSeconds) }
    var timerRunning by remember { mutableStateOf(false) }
    var sessionsDone by remember { mutableStateOf(0) }
    var shakeCount   by remember { mutableStateOf(0) }
    var showShakeAlert by remember { mutableStateOf(false) }
    var phoneFlat    by remember { mutableStateOf(true) }   // Z-axis check

    // ------- Sensor Setup -------
    // DisposableEffect runs the setup block when the composable
    // first appears, and the onDispose block when it disappears.
    // This is the correct Compose way to manage lifecycle-aware resources.
    DisposableEffect(Unit) {

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // SensorEventListener is the callback interface Android calls
        // with new sensor readings (typically 10-50 times per second)
        val listener = object : SensorEventListener {

            private var lastShakeTime = 0L
            private val SHAKE_THRESHOLD = 15f   // m/s² above gravity
            private val SHAKE_COOLDOWN  = 1000L // ms between shake events

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Magnitude of acceleration vector
                val magnitude = sqrt(x * x + y * y + z * z)

                // Flat-on-desk detection: Z should dominate when flat
                phoneFlat = z > 8f && kotlin.math.abs(x) < 3f && kotlin.math.abs(y) < 3f

                // Shake detection
                val now = System.currentTimeMillis()
                if (magnitude > SHAKE_THRESHOLD && (now - lastShakeTime) > SHAKE_COOLDOWN) {
                    lastShakeTime = now
                    shakeCount++
                    if (timerRunning) {
                        // Pause the timer on shake — user was distracted
                        timerRunning = false
                        showShakeAlert = true
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Not needed for accelerometer
            }
        }

        // SENSOR_DELAY_GAME = fastest sampling rate (~50Hz).
        // SENSOR_DELAY_NORMAL = ~5Hz, enough for shake detection
        // but SENSOR_DELAY_GAME catches quick shakes better.
        sensorManager.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )

        // onDispose: called when the composable leaves the screen.
        // ALWAYS unregister sensor listeners — they drain battery.
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }


    // ------- Timer coroutine (same as Project 1) -------
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

    // ------- UI -------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Phone status indicator
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (phoneFlat)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        ) {
            Text(
                text = if (phoneFlat) "\uD83D\uDCF1 Phone flat — focus mode" else "\uD83D\uDCF1 Phone moved — stay focused!",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 13.sp,
                color = if (phoneFlat)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Shakes detected: $shakeCount",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Text("Pomodoro Timer", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "Sessions done today: $sessionsDone",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

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

        Spacer(Modifier.height(32.dp))

        Button(
            onClick  = { timerRunning = !timerRunning; showShakeAlert = false },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (timerRunning) Color(0xFFF44336) else MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if (timerRunning) "Pause" else "Start Focus",
                fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick  = { timerRunning = false; timeLeft = totalSeconds },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Reset") }

        // Shake alert banner
        if (showShakeAlert) {
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDCF3", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Shake detected!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Timer paused. Put the phone down and resume when ready.",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}