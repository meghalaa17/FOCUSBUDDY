package com.example.a211393_nelson_lab01.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ================================================================
// WHAT IS A DAO?
// DAO = Data Access Object. It is an INTERFACE (not a class) where
// you declare what database operations you want. Room reads these
// annotations and WRITES THE SQL FOR YOU at compile time.
//
// You never implement this interface — Room generates the code.
// You just define the functions and Room figures out the SQL.
//
// ANNOTATION MEANINGS:
// @Dao     → "this interface is a database accessor"
// @Insert  → generates: INSERT INTO table VALUES (...)
// @Delete  → generates: DELETE FROM table WHERE id = ...
// @Query   → you write the SQL yourself for custom reads
//
// Flow<List<...>> → this is a "live stream" of data.
// Whenever the DB changes, any screen observing this Flow
// automatically gets the new list — no manual refresh needed.
// ================================================================

@Dao
interface StudySlotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // onConflict = REPLACE means: if a row with the same ID exists,
    // overwrite it. Useful for "save or update" behaviour.
    suspend fun insert(slot: StudySlotEntity)
    // `suspend` = this runs on a background thread (coroutine).
    // Database operations must NOT run on the main UI thread
    // because they can take time and would freeze the screen.

    @Delete
    suspend fun delete(slot: StudySlotEntity)

    @Query("SELECT * FROM study_slots ORDER BY day, time")
    // This is the one place you write actual SQL.
    // "Give me all rows from study_slots, sorted by day then time"
    fun getAllSlots(): Flow<List<StudySlotEntity>>
    // Notice: no `suspend` here. Flow handles the async nature
    // itself — it's an ongoing stream, not a one-time call.
}


@Dao
interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: FlashcardEntity)

    @Delete
    suspend fun delete(card: FlashcardEntity)

    @Query("SELECT * FROM flashcards")
    fun getAllCards(): Flow<List<FlashcardEntity>>
}


@Dao
interface StudySessionDao {

    @Insert
    suspend fun insert(session: StudySessionEntity)

    @Query("SELECT * FROM study_sessions ORDER BY id DESC")
    // ORDER BY id DESC = newest sessions first (biggest ID = most recent)
    fun getAllSessions(): Flow<List<StudySessionEntity>>
}