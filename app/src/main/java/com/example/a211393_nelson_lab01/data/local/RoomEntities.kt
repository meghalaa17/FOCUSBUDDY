package com.example.a211393_nelson_lab01.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// ================================================================
// WHAT IS THIS FILE?
// These are your existing data classes (from Models.kt) but with
// Room annotations added. Room reads these annotations and
// automatically creates SQL tables for you — you never write SQL.
//
// ANNOTATION MEANINGS:
// @Entity         → "make a database TABLE for this class"
// @PrimaryKey     → "this column is the unique ID (like a row number)"
// autoGenerate    → Room picks the ID number for you (1, 2, 3...)
// ================================================================

@Entity(tableName = "study_slots")
// tableName = the actual SQL table name in the database file
data class StudySlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Room fills this in automatically
    val subject: String,
    val day: String,
    val time: String,
    val duration: String
)
// WHY a separate Entity class instead of using your existing StudySlot?
// Your existing StudySlot has an `id` you assign manually (slotId++).
// Room needs to control the ID itself (autoGenerate). Keeping them
// separate lets Room manage the DB id while your ViewModel keeps its
// own in-memory id. You'll convert between them in the repository.


@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val answer: String
)


@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val durationMinutes: Int,
    val xpEarned: Int,
    val date: String = "Today"
)