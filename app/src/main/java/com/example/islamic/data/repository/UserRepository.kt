package com.example.islamic.data.repository

import com.example.islamic.data.model.UserData
import kotlinx.coroutines.delay

class UserRepository {
    suspend fun getUserData(): UserData {
        delay(500) // simulasi loading dari Firebase
        return UserData(
            name = "Bustami",
            location = "Samarinda",
            institution = "Universitas Muhammadiyah Kaltim"
        )
    }

    suspend fun hasUnreadNotifications(): Boolean {
        delay(300) // simulasi pengecekan status notifikasi
        return true
    }
}
