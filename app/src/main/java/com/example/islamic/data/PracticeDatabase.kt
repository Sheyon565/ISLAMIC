package com.example.islamic.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.islamic.data.repository.PracticeDao
import com.example.islamic.data.model.DailyPractice

@Database(entities = [DailyPractice::class], version = 3, exportSchema = false)
abstract class PracticeDatabase : RoomDatabase() {
    abstract fun practiceDao(): PracticeDao

    companion object {
        @Volatile
        private var INSTANCE: PracticeDatabase? = null

        fun getDatabase(context: Context): PracticeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PracticeDatabase::class.java,
                    "practice_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }
}