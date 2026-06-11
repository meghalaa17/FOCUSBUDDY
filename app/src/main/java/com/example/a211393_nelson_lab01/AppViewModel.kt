package com.example.a211393_nelson_lab01

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a211393_nelson_lab01.data.api.QuoteApi
import com.example.a211393_nelson_lab01.data.api.QuoteResponse
import com.example.a211393_nelson_lab01.data.local.FocusBuddyDatabase
import com.example.a211393_nelson_lab01.data.local.FlashcardEntity
import com.example.a211393_nelson_lab01.data.local.StudySessionEntity
import com.example.a211393_nelson_lab01.data.local.StudySlotEntity
import com.example.a211393_nelson_lab01.data.remote.CommunityGoal
import com.example.a211393_nelson_lab01.data.remote.FirestoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ================================================================
// CHANGES FROM PROJECT 1:
//
// 1. AndroidViewModel instead of ViewModel
//    We need `application` context to access the database.
//    AndroidViewModel provides `application` automatically.
//
// 2. Room DAOs replace the in-memory mutableStateListOf
//    Instead of storing slots in RAM, we read/write to the DB.
//    The UI observes StateFlow which Room updates automatically.
//
// 3. API call added for daily quote
//    viewModelScope.launch runs a coroutine — a lightweight
//    background thread that won't freeze the UI.
//
// 4. Firestore calls for community goals
// ================================================================

class AppViewModel(application: Application) : AndroidViewModel(application) {

    // ------- Simple state (unchanged from Project 1) -------
    var userName = mutableStateOf("")
        private set
    var points = mutableStateOf(0)
        private set
    var petName = mutableStateOf("Buddy")
        private set
    val petLevel: Int get() = (points.value / 100) + 1
    var quizScore = mutableStateOf(0)
        private set


    // ------- Room Database setup -------
    // We get the database instance here once, then access its DAOs
    private val db = FocusBuddyDatabase.getDatabase(application)
    private val slotDao     = db.studySlotDao()
    private val cardDao     = db.flashcardDao()
    private val sessionDao  = db.studySessionDao()

    // StateFlow<List<...>> is the "live stream" we discussed.
    // .stateIn() converts a Flow into a StateFlow that the UI can
    // observe with collectAsState(). When Room data changes,
    // this flow emits the new list and the UI recomposes.
    //
    // SharingStarted.WhileSubscribed(5000) means: keep the flow
    // active for 5 seconds after the last observer disappears
    // (e.g. screen rotates). Prevents unnecessary DB re-reads.
    val studySlots: StateFlow<List<StudySlotEntity>> =
        slotDao.getAllSlots()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<FlashcardEntity>> =
        cardDao.getAllCards()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessionHistory: StateFlow<List<StudySessionEntity>> =
        sessionDao.getAllSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // ------- API state (daily motivational quote) -------
    var dailyQuote = mutableStateOf<QuoteResponse?>(null)
        private set
    var isQuoteLoading = mutableStateOf(false)
        private set
    var quoteError = mutableStateOf<String?>(null)
        private set

    // Fetch quote when ViewModel is first created
    // init{} runs once when the ViewModel is instantiated
    init {
        fetchDailyQuote()
    }

    fun fetchDailyQuote() {
        // viewModelScope.launch starts a coroutine tied to this ViewModel.
        // If the ViewModel is destroyed (user leaves), the coroutine
        // is automatically cancelled — no memory leaks.
        viewModelScope.launch {
            isQuoteLoading.value = true
            quoteError.value = null
            try {
                val result = QuoteApi.service.getRandomQuote()
                dailyQuote.value = result.firstOrNull()
                // .firstOrNull() gets the first item or null if empty
            } catch (e: Exception) {
                // If there's no internet or the API is down,
                // we catch the error and show a fallback message
                quoteError.value = "Could not load quote. Check your connection."
            } finally {
                isQuoteLoading.value = false
                // finally{} always runs (even if there was an exception)
            }
        }
    }


    // ------- Firestore community goals -------
    val communityGoals: StateFlow<List<CommunityGoal>> =
        FirestoreRepository.getCommunityGoals()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun postCommunityGoal(goal: String, subject: String) {
        viewModelScope.launch {
            FirestoreRepository.postGoal(
                CommunityGoal(
                    userName = userName.value.ifBlank { "Anonymous" },
                    goal = goal,
                    subject = subject
                )
            )
        }
    }


    // ------- ViewModel functions (now write to Room too) -------
    fun setUserName(name: String) { userName.value = name }
    fun setPetName(name: String)  { petName.value  = name }
    fun addPoints(amount: Int)    { points.value  += amount }

    fun addStudySlot(subject: String, day: String, time: String, duration: String) {
        viewModelScope.launch {
            // Room insert is a suspend function — must run in coroutine
            slotDao.insert(StudySlotEntity(subject = subject, day = day, time = time, duration = duration))
            // The StateFlow above (studySlots) automatically updates
            // because Room notifies all observers when data changes
            addPoints(10)
        }
    }

    fun removeStudySlot(slot: StudySlotEntity) {
        viewModelScope.launch {
            slotDao.delete(slot)
            // Again, studySlots StateFlow will auto-update
        }
    }

    fun addFlashcard(question: String, answer: String) {
        viewModelScope.launch {
            cardDao.insert(FlashcardEntity(question = question, answer = answer))
            addPoints(5)
        }
    }

    fun removeFlashcard(card: FlashcardEntity) {
        viewModelScope.launch {
            cardDao.delete(card)
        }
    }

    fun saveQuizScore(score: Int) {
        quizScore.value = score
        val xp = score * 10
        addPoints(xp)
        logSession("Quiz", 0, xp)
    }

    fun logSession(type: String, durationMinutes: Int, xpEarned: Int) {
        viewModelScope.launch {
            sessionDao.insert(
                StudySessionEntity(type = type, durationMinutes = durationMinutes, xpEarned = xpEarned)
            )
        }
    }
}