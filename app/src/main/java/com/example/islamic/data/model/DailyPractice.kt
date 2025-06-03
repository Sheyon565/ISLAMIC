package com.example.islamic.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_practices") // Pastikan nama tabel di sini
data class DailyPractice(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val userId: String,
    val time: Long,
    val name: String,
    val day: String,
    val isComplete: Boolean = false
    // kolom lainnya
)
