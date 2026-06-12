package com.example.a211393_nelson_lab01.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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

    abstract fun studySlotDao(): StudySlotDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: FocusBuddyDatabase? = null

        fun getDatabase(context: Context): FocusBuddyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusBuddyDatabase::class.java,
                    "focus_buddy_database"
                )
                    // If you tweak an Entity while polishing, this prevents
                    // a crash on existing installs (wipes local data instead).
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}