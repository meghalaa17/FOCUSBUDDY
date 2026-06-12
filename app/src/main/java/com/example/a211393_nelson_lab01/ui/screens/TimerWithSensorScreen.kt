package com.example.a211393_nelson_lab01.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
// SENSOR INTEGRATION — Two sensors on one screen
//
// SENSOR 1: Accelerometer (TYPE_ACCELEROMETER)
//   Measures force in m/s² on X/Y/Z axes.
//   At rest: magnitude = ~9.8 (just gravity pulling down)
//   When shaken: magnitude spikes well above 9.8
//   We detect a shake when magnitude > SHAKE_THRESHOLD (15f)
//   and at least 1000ms has passed since the last shake.
//   On shake during timer → timer pauses (distraction detected).
//
// SENSOR 2: Light sensor (TYPE_LIGHT)
//   Returns ambient brightness in lux (single value).
//   < 50 lux  → too dim, bad for eyes
//   50–300    → okay but could be brighter
//   300+ lux  → good study lighting
//   We update luxLevel and a status string on the main thread
//   using Handler(Looper.getMainLooper()).post{} to avoid
//   Compose state race conditions (sensor callbacks run on a
//   background thread, not the UI thread).
//
// HOW COMPOSE + SENSORS CONNECT:
//   SensorManager is an Android system service, not Compose-aware.
//   We use DisposableEffect to register on entry, unregister on exit.
//   Two separate DisposableEffect blocks — one per sensor — keeps
//   the logic clean and independently testable.
// ================================================================

@Composable
fun TimerWithSensorScreen(viewModel: AppViewModel, onBack: () -> Unit) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ------- Timer state -------
    val totalSeconds = 1500
    var timeLeft       by remember { mutableStateOf(totalSeconds) }
    var timerRunning   by remember { mutableStateOf(false) }
    var sessionsDone   by remember { mutableStateOf(0) }

    // ------- Accelerometer state -------
    var shakeCount     by remember { mutableStateOf(0) }
    var showShakeAlert by remember { mutableStateOf(false) }
    var phoneFlat      by remember { mutableStateOf(true) }

    // ------- Light sensor state -------
    var luxLevel       by remember { mutableStateOf(0f) }
    var lightStatus    by remember { mutableStateOf("Checking lighting...") }

    // ================================================================
    // SENSOR 1 — Accelerometer
    // DisposableEffect(Unit): runs once when composable enters,
    // onDispose runs when it leaves. Unit key = never re-runs.
    // ================================================================
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val accelListener = object : SensorEventListener {

            private var lastShakeTime = 0L
            private val SHAKE_THRESHOLD = 15f   // m/s² — spike above normal gravity
            private val SHAKE_COOLDOWN  = 1000L // ms — ignore repeated shakes

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Total magnitude of the acceleration vector.
                // At rest this equals ~9.8 (Earth gravity on Z axis).
                // A shake causes all axes to spike simultaneously.
                val magnitude = sqrt(x * x + y * y + z * z)

                // Flat-on-desk: Z dominates (gravity), X/Y near zero
                Handler(Looper.getMainLooper()).post {
                    phoneFlat = z > 8f &&
                            kotlin.math.abs(x) < 3f &&
                            kotlin.math.abs(y) < 3f
                }

                val now = System.currentTimeMillis()
                if (magnitude > SHAKE_THRESHOLD &&
                    (now - lastShakeTime) > SHAKE_COOLDOWN
                ) {
                    lastShakeTime = now
                    Handler(Looper.getMainLooper()).post {
                        shakeCount++
                        if (timerRunning) {
                            timerRunning   = false
                            showShakeAlert = true
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        // SENSOR_DELAY_GAME = ~50Hz — fast enough to catch quick shakes
        sensorManager.registerListener(
            accelListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )

        onDispose {
            // ALWAYS unregister — sensor listeners drain battery if left running
            sensorManager.unregisterListener(accelListener)
        }
    }

    // ================================================================
    // SENSOR 2 — Light sensor
    // Separate DisposableEffect so each sensor lifecycle is independent.
    // SENSOR_DELAY_NORMAL = ~5Hz — plenty for ambient light readings.
    // ================================================================
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val lightSensor   = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        val lightListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val lux = event.values[0]  // single value — ambient brightness in lux
                // Post to main thread before touching Compose state.
                // onSensorChanged fires on a sensor background thread —
                // mutating mutableStateOf off the main thread causes crashes.
                Handler(Looper.getMainLooper()).post {
                    luxLevel    = lux
                    lightStatus = when {
                        lux < 50f  -> "Too dim — turn on a light!"
                        lux < 300f -> "Okay — could be brighter"
                        else       -> "Good lighting for studying"
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        sensorManager.registerListener(
            lightListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        onDispose {
            sensorManager.unregisterListener(lightListener)
        }
    }

    // ================================================================
    // Timer coroutine
    // LaunchedEffect(timerRunning) restarts whenever timerRunning flips.
    // The while loop ticks every second while running and time remains.
    // ================================================================
    LaunchedEffect(timerRunning) {
        while (timerRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
            // Award 1 XP per completed minute
            if (timeLeft % 60 == 0 && timeLeft != totalSeconds) {
                viewModel.addPoints(1)
            }
        }
        // Session complete
        if (timerRunning && timeLeft == 0) {
            timerRunning = false
            sessionsDone++
            viewModel.addPoints(25)
            viewModel.logSession("Timer", 25, 25)
            timeLeft = totalSeconds
        }
    }

    val progress = timeLeft.toFloat() / totalSeconds.toFloat()

    // ================================================================
    // UI
    // ================================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Pomodoro Timer",
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Sessions done today: $sessionsDone",
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        // ── Sensor status cards ───────────────────────────────────
        // Card 1: Phone position (accelerometer)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            color    = if (phoneFlat)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("\uD83D\uDCF1", fontSize = 18.sp)
                Column {
                    Text(
                        text  = if (phoneFlat) "Phone flat — focus mode" else "Phone moved — stay focused!",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (phoneFlat)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Text(
                        text  = "Shakes detected: $shakeCount",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Card 2: Ambient light (light sensor)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            color    = when {
                luxLevel < 50f  -> MaterialTheme.colorScheme.errorContainer
                luxLevel < 300f -> MaterialTheme.colorScheme.tertiaryContainer
                else            -> MaterialTheme.colorScheme.primaryContainer
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("\uD83D\uDCA1", fontSize = 18.sp)
                Column {
                    Text(
                        text       = lightStatus,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            luxLevel < 50f  -> MaterialTheme.colorScheme.error
                            luxLevel < 300f -> MaterialTheme.colorScheme.tertiary
                            else            -> MaterialTheme.colorScheme.primary
                        }
                    )
                    Text(
                        text  = "${luxLevel.toInt()} lux",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Circular timer ────────────────────────────────────────
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

        Spacer(Modifier.height(28.dp))

        // ── Controls ──────────────────────────────────────────────
        Button(
            onClick  = { timerRunning = !timerRunning; showShakeAlert = false },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerRunning) Color(0xFFF44336)
                else MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text       = if (timerRunning) "Pause" else "Start Focus",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick  = { timerRunning = false; timeLeft = totalSeconds },
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp)
        ) {
            Text("Reset")
        }

        // ── Shake alert banner ────────────────────────────────────
        if (showShakeAlert) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83D\uDCF3", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Shake detected!",
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Timer paused. Put the phone down and resume when ready.",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // ── Lighting warning banner ───────────────────────────────
        // Only shown when timer is running and lighting is poor
        if (timerRunning && luxLevel in 1f..49f) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDCA1", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Lighting too dim!",
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Studying in low light strains your eyes. Turn on a light before continuing.",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}