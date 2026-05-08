package com.example.a211393_nelson_lab01

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.a211393_nelson_lab01.data.Flashcard
import com.example.a211393_nelson_lab01.data.StudySlot
import com.example.a211393_nelson_lab01.data.StudySession

class AppViewModel : ViewModel() {

    //storage with ui update using mutablestate
    var userName = mutableStateOf("")
        private set

    var points = mutableStateOf(0)
        private set

    var petName = mutableStateOf("Buddy")
        private set

    val petLevel: Int get() = (points.value / 100) + 1

    //storage with ui update using mutablestate
    val studySlots     = mutableStateListOf<StudySlot>()
    val flashcards     = mutableStateListOf<Flashcard>()
    val sessionHistory = mutableStateListOf<StudySession>()

    var quizScore = mutableStateOf(0)
        private set

    private var slotId    = 0
    private var cardId    = 0
    private var sessionId = 0

    fun setUserName(name: String) { userName.value = name }
    fun setPetName(name: String)  { petName.value  = name }

    fun addPoints(amount: Int) { points.value += amount }

    //uses storage and blueprint to make it functional
    fun addStudySlot(subject: String, day: String, time: String, duration: String) {
        studySlots.add(
            StudySlot(
                id       = slotId++,
                subject  = subject,
                day      = day,
                time     = time,
                duration = duration
            )
        )
        addPoints(10)
    }

    fun removeStudySlot(id: Int) { studySlots.removeAll { it.id == id } }

    fun addFlashcard(question: String, answer: String) {
        flashcards.add(Flashcard(id = cardId++, question = question, answer = answer))
        addPoints(5)
    }

    fun removeFlashcard(id: Int) { flashcards.removeAll { it.id == id } }

    fun saveQuizScore(score: Int) {
        quizScore.value = score
        val xp = score * 10
        addPoints(xp)
        logSession("Quiz", 0, xp)
    }

    fun logSession(type: String, durationMinutes: Int, xpEarned: Int) {
        sessionHistory.add(
            StudySession(
                id              = sessionId++,
                type            = type,
                durationMinutes = durationMinutes,
                xpEarned        = xpEarned
            )
        )
    }
}