package com.example.a211393_nelson_lab01.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a211393_nelson_lab01.AppViewModel

// ===================== STUDY SESSION SCREEN =====================

@Composable
fun StudySessionScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var activeMode by remember { mutableStateOf("schedule") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                "schedule"  to "Schedule",
                "quiz"      to "Quiz",
                "flashcard" to "Flashcard"
            ).forEach { (id, label) ->
                FilterChip(
                    selected = activeMode == id,
                    onClick  = { activeMode = id },
                    label    = { Text(label) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when (activeMode) {
            "schedule"  -> StudySlotView(viewModel)
            "quiz"      -> QuizView(viewModel)
            "flashcard" -> FlashcardView(viewModel)
        }
    }
}

// ===================== STUDY SLOT VIEW =====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySlotView(viewModel: AppViewModel) {
    val subjects  = listOf("Math", "Science", "History", "Biology", "English", "Physics", "Chemistry")
    val days      = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val times     = listOf("6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM",
        "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
        "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM")
    val durations = listOf("30 min", "1 hour", "1.5 hours", "2 hours", "2.5 hours", "3 hours")

    var selectedSubject  by remember { mutableStateOf(subjects[0]) }
    var selectedDay      by remember { mutableStateOf(days[0]) }
    var selectedTime     by remember { mutableStateOf(times[2]) }
    var selectedDuration by remember { mutableStateOf(durations[1]) }

    var subjectExpanded  by remember { mutableStateOf(false) }
    var dayExpanded      by remember { mutableStateOf(false) }
    var timeExpanded     by remember { mutableStateOf(false) }
    var durationExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            "Add Study Slot",
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp
        )
        Text(
            "Plan your weekly study schedule",
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        // Subject dropdown
        ExposedDropdownMenuBox(
            expanded          = subjectExpanded,
            onExpandedChange  = { subjectExpanded = it }
        ) {
            OutlinedTextField(
                value         = selectedSubject,
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Subject") },
                leadingIcon   = { Text("\uD83D\uDCDA", fontSize = 16.sp) },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                modifier      = Modifier.menuAnchor().fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded          = subjectExpanded,
                onDismissRequest  = { subjectExpanded = false }
            ) {
                subjects.forEach { s ->
                    DropdownMenuItem(
                        text    = { Text(s) },
                        onClick = { selectedSubject = s; subjectExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Day dropdown
        ExposedDropdownMenuBox(
            expanded         = dayExpanded,
            onExpandedChange = { dayExpanded = it }
        ) {
            OutlinedTextField(
                value         = selectedDay,
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Day") },
                leadingIcon   = { Text("\uD83D\uDCC5", fontSize = 16.sp) },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                modifier      = Modifier.menuAnchor().fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded         = dayExpanded,
                onDismissRequest = { dayExpanded = false }
            ) {
                days.forEach { d ->
                    DropdownMenuItem(
                        text    = { Text(d) },
                        onClick = { selectedDay = d; dayExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Time and Duration side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Time dropdown
            ExposedDropdownMenuBox(
                expanded         = timeExpanded,
                onExpandedChange = { timeExpanded = it },
                modifier         = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value         = selectedTime,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Time") },
                    leadingIcon   = { Text("\u23F0", fontSize = 14.sp) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded) },
                    modifier      = Modifier.menuAnchor().fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded         = timeExpanded,
                    onDismissRequest = { timeExpanded = false }
                ) {
                    times.forEach { t ->
                        DropdownMenuItem(
                            text    = { Text(t) },
                            onClick = { selectedTime = t; timeExpanded = false }
                        )
                    }
                }
            }

            // Duration dropdown
            ExposedDropdownMenuBox(
                expanded         = durationExpanded,
                onExpandedChange = { durationExpanded = it },
                modifier         = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value         = selectedDuration,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Duration") },
                    leadingIcon   = { Text("\u23F1\uFE0F", fontSize = 14.sp) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = durationExpanded) },
                    modifier      = Modifier.menuAnchor().fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded         = durationExpanded,
                    onDismissRequest = { durationExpanded = false }
                ) {
                    durations.forEach { dur ->
                        DropdownMenuItem(
                            text    = { Text(dur) },
                            onClick = { selectedDuration = dur; durationExpanded = false }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Preview card before adding
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    "Preview",
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "\uD83D\uDCDA $selectedSubject  \u00B7  \uD83D\uDCC5 $selectedDay  \u00B7  \u23F0 $selectedTime  \u00B7  \u23F1\uFE0F $selectedDuration",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    color      = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.addStudySlot( //function called
                    subject  = selectedSubject,
                    day      = selectedDay,
                    time     = selectedTime,
                    duration = selectedDuration
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Text("Add to Schedule (+10 XP)")
        }

        Spacer(Modifier.height(24.dp))

        // My schedule list
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "My Schedule (${viewModel.studySlots.size})",
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp
            )
            if (viewModel.studySlots.isNotEmpty()) {
                Text(
                    "${viewModel.studySlots.size} slots",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (viewModel.studySlots.isEmpty()) {
            Text(
                "No slots yet. Add your first study slot above!",
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        } else {
            // Group by day
            val grouped = viewModel.studySlots.groupBy { it.day }
            val dayOrder = listOf("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday")

            dayOrder.forEach { day ->
                val slotsForDay = grouped[day]
                if (!slotsForDay.isNullOrEmpty()) {
                    Text(
                        day,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 13.sp,
                        color      = MaterialTheme.colorScheme.primary,
                        modifier   = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    slotsForDay.forEach { slot ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Subject colour dot
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
                                        "${slot.time}  ·  ${slot.duration}",
                                        fontSize = 12.sp,
                                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.removeStudySlot(slot.id) }
                                ) {
                                    Text("\uD83D\uDDD1\uFE0F", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ===================== QUIZ VIEW =====================

@Composable
fun QuizView(viewModel: AppViewModel) {

    data class Question(val text: String, val options: List<String>, val correctIndex: Int)

    val questionBank = mapOf(
        "Math" to listOf(
            Question("What is 12 x 12?", listOf("144","124","132","156"), 0),
            Question("What is the square root of 81?", listOf("9","8","7","6"), 0),
            Question("What is 15% of 200?", listOf("30","25","35","20"), 0)
        ),
        "Science" to listOf(
            Question("What is the chemical symbol for water?", listOf("H2O","CO2","O2","H2"), 0),
            Question("How many planets are in our solar system?", listOf("8","9","7","10"), 0),
            Question("What gas do plants absorb from the air?", listOf("CO2","Oxygen","Nitrogen","Hydrogen"), 0)
        ),
        "History" to listOf(
            Question("In which year did World War 2 end?", listOf("1945","1944","1939","1950"), 0),
            Question("Who was the first President of the USA?", listOf("George Washington","Abraham Lincoln","Thomas Jefferson","John Adams"), 0),
            Question("Which ancient wonder was located in Egypt?", listOf("Great Pyramid","Colosseum","Parthenon","Stonehenge"), 0)
        ),
        "Biology" to listOf(
            Question("What is the powerhouse of the cell?", listOf("Mitochondria","Nucleus","Ribosome","Vacuole"), 0),
            Question("How many chromosomes does a human cell have?", listOf("46","23","48","44"), 0),
            Question("What is the process plants use to make food?", listOf("Photosynthesis","Respiration","Digestion","Osmosis"), 0)
        ),
        "English" to listOf(
            Question("What is a synonym for 'happy'?", listOf("Joyful","Sad","Angry","Tired"), 0),
            Question("Which of these is a noun?", listOf("Table","Run","Happy","Quickly"), 0),
            Question("What is an antonym for 'ancient'?", listOf("Modern","Old","Historic","Classic"), 0)
        )
    )

    val subjects = questionBank.keys.toList()

    var selectedSubject by remember { mutableStateOf("") }
    var currentIndex    by remember { mutableStateOf(0) }
    var selectedIndex   by remember { mutableStateOf(-1) }
    var score           by remember { mutableStateOf(0) }
    var finished        by remember { mutableStateOf(false) }
    var answered        by remember { mutableStateOf(false) }

    // ── Subject selection ─────────────────────────────────────────
    if (selectedSubject.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "\uD83E\uDDE0 Choose a Subject",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Each correct answer = +10 XP",
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))

            subjects.forEach { subject ->
                val emoji = when (subject) {
                    "Math"    -> "\uD83D\uDCCA"
                    "Science" -> "\uD83D\uDD2C"
                    "History" -> "\uD83C\uDFDB\uFE0F"
                    "Biology" -> "\uD83E\uDDEC"
                    "English" -> "\uD83D\uDCDD"
                    else      -> "\uD83D\uDCDA"
                }
                // Show scheduled slots for this subject
                val slotsForSubject = viewModel.studySlots.count { it.subject == subject }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedSubject = subject
                            currentIndex    = 0
                            selectedIndex   = -1
                            score           = 0
                            finished        = false
                            answered        = false
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, fontSize = 28.sp)
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(subject, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "3 questions  ·  up to 30 XP",
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (slotsForSubject > 0) {
                                Text(
                                    "\uD83D\uDCC5 $slotsForSubject slot(s) scheduled",
                                    fontSize = 11.sp,
                                    color    = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            "\u25B6",
                            fontSize = 14.sp,
                            color    = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        return
    }

    // ── Quiz finished ─────────────────────────────────────────────
    if (finished) {
        val xpEarned = score * 10
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(if (score == 3) "\uD83C\uDF1F" else "\uD83C\uDF89", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "$selectedSubject Quiz Complete!",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text("Score: $score / 3", fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    "+$xpEarned XP earned",
                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                when {
                    score == 3 -> "Perfect score! \uD83C\uDF1F"
                    score == 2 -> "Good job! Almost perfect!"
                    score == 1 -> "Keep practising!"
                    else       -> "Don't give up, try again!"
                },
                fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick  = { viewModel.saveQuizScore(score); selectedSubject = "" },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save & Back to Subjects") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    currentIndex  = 0
                    selectedIndex = -1
                    score         = 0
                    finished      = false
                    answered      = false
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Retry $selectedSubject") }
        }
        return
    }

    // ── Active quiz ───────────────────────────────────────────────
    val questions = questionBank[selectedSubject] ?: emptyList()
    val q         = questions[currentIndex]

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { selectedSubject = "" }) { Text("\u2190 Subjects") }
            Spacer(Modifier.weight(1f))
            Text(selectedSubject, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        LinearProgressIndicator(
            progress = { (currentIndex + 1f) / questions.size },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Question ${currentIndex + 1} of ${questions.size}  ·  Score: $score",
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                q.text,
                modifier   = Modifier.padding(20.dp),
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(16.dp))

        q.options.forEachIndexed { index, option ->
            val containerColor = when {
                !answered               -> MaterialTheme.colorScheme.surfaceVariant
                index == q.correctIndex -> Color(0xFF4CAF50)
                index == selectedIndex  -> Color(0xFFF44336)
                else                    -> MaterialTheme.colorScheme.surfaceVariant
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(enabled = !answered) {
                        selectedIndex = index
                        answered      = true
                        if (index == q.correctIndex) score++
                    },
                colors = CardDefaults.cardColors(containerColor = containerColor)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${('A' + index)}. ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(option, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    if (answered && index == q.correctIndex) Text("\u2705", fontSize = 14.sp)
                    if (answered && index == selectedIndex && index != q.correctIndex) Text("\u274C", fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (answered) {
            Text(
                if (selectedIndex == q.correctIndex) "+10 XP \uD83C\uDF1F" else "Incorrect \uD83D\uDCDA Keep studying!",
                color      = if (selectedIndex == q.correctIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick  = {
                    if (currentIndex < questions.size - 1) {
                        currentIndex++; selectedIndex = -1; answered = false
                    } else { finished = true }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (currentIndex < questions.size - 1) "Next Question \u2192" else "See Results")
            }
        }
    }
}

// ===================== FLASHCARD VIEW =====================

@Composable
fun FlashcardView(viewModel: AppViewModel) {
    var question    by remember { mutableStateOf("") }
    var answer      by remember { mutableStateOf("") }
    var flippedId   by remember { mutableStateOf(-1) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text("Create Flashcard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value         = question,
            onValueChange = { question = it },
            label         = { Text("Question / Front") },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value         = answer,
            onValueChange = { answer = it },
            label         = { Text("Answer / Back") },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))

        Button(
            onClick  = {
                if (question.isNotBlank() && answer.isNotBlank()) {
                    viewModel.addFlashcard(question, answer)
                    question = ""
                    answer   = ""
                }
            },
            enabled  = question.isNotBlank() && answer.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add Flashcard (+5 XP)") }

        Spacer(Modifier.height(24.dp))

        Text(
            "My Flashcards (${viewModel.flashcards.size})",
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp
        )
        Spacer(Modifier.height(8.dp))

        if (viewModel.flashcards.isEmpty()) {
            Text(
                "No flashcards yet. Create one above!",
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        } else {
            viewModel.flashcards.forEach { card ->
                val isFlipped = flippedId == card.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .animateContentSize()
                        .clickable { flippedId = if (isFlipped) -1 else card.id },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    if (isFlipped) "Answer" else "Question",
                                    fontSize = 11.sp,
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    if (isFlipped) card.answer else card.question,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(if (isFlipped) "\u25B2" else "\u25BC", fontSize = 12.sp)
                        }
                        if (!isFlipped) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Tap to reveal answer",
                                fontSize = 11.sp,
                                color    = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = { viewModel.removeFlashcard(card.id) },
                            colors  = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Delete", fontSize = 12.sp) }
                    }
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}