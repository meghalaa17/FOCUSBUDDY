package com.example.a211393_nelson_lab01.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ================================================================
// WHAT IS THIS?
// This is the "glue" class that connects your Entities and DAOs
// into one actual database file on the device.
//
// @Database annotation tells Room:
//   entities  = which tables exist in this database
//   version   = the schema version number (start at 1, increase
//               whenever you change a table structure)
//   exportSchema = false means don't generate a JSON schema file
//                  (set true in production for migration safety)
//
// RoomDatabase is abstract — Room generates the actual
// implementation class at compile time.
// ================================================================

@Database(
    entities = [
        StudySlotEntity::class,
        FlashcardEntity::class,
        StudySessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FocusBuddyDatabase : RoomDatabase() {

    // Each of these abstract functions tells Room:
    // "I need access to this DAO — please provide it"
    abstract fun studySlotDao(): StudySlotDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun studySessionDao(): StudySessionDao

    // SINGLETON PATTERN
    // A singleton means only ONE instance of the database ever exists.
    // Creating multiple database connections wastes memory and causes
    // data conflicts. @Volatile ensures all threads see the latest value.
    companion object {
        @Volatile
        private var INSTANCE: FocusBuddyDatabase? = null

        fun getDatabase(context: Context): FocusBuddyDatabase {
            // If an instance already exists, return it immediately.
            // Otherwise, enter a synchronized block (only one thread
            // can execute this at a time) to safely create it.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusBuddyDatabase::class.java,
                    "focus_buddy_database"  // the .db filename on disk
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}