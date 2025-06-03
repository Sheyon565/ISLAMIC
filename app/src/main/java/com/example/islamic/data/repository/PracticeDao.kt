package com.example.islamic.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.islamic.data.model.DailyPractice
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeDao {
    @Query("SELECT * FROM daily_practices WHERE id = :id LIMIT 1")
    suspend fun getPracticeById(id: Int): DailyPractice?

    @Query("SELECT * FROM daily_practices ORDER BY time ASC")
    fun getAllPractices(): Flow<List<DailyPractice>>

    @Query("SELECT * FROM daily_practices WHERE userId = :userId ORDER BY time ASC")
    fun getPracticesForUser(userId: String): Flow<List<DailyPractice>>

    @Query("SELECT * FROM daily_practices WHERE day = :day AND userId = :userId ORDER BY time ASC")
    suspend fun getPracticesForDay(day: String, userId: String): List<DailyPractice>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPractice(practice: DailyPractice): Long

    @Update
    suspend fun updatePractice(practice: DailyPractice): Int

    @Delete
    suspend fun deletePractice(practice: DailyPractice): Int
}