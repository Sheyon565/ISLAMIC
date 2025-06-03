package com.example.islamic.data.repository

import com.example.islamic.data.model.Notification
import com.example.islamic.data.model.PrayerNotification
import kotlinx.coroutines.delay

class NotificationRepository {

    private val notifications = mutableListOf(
        PrayerNotification(
            id = "1",
            prayerName = "Shubuh",
            time = "04:51",
            message = "Waktu Sholat Shubuh telah tiba",
            title = "Sholat Subuh",
            timeAgo = "10 menit lalu",
            isRead = false,
            prayerType = "Wajib"
        ),
        PrayerNotification(
            id = "2",
            prayerName = "Dzuhur",
            time = "12:10",
            message = "Waktu Sholat Dzuhur telah tiba",
            title = "Sholat Dzuhur",
            timeAgo = "2 jam lalu",
            isRead = true,
            prayerType = "Wajib"
        ),
        PrayerNotification(
            id = "3",
            prayerName = "Ashar",
            time = "15:51",
            message = "Waktu Sholat Ashar telah tiba",
            title = "Sholat Ashar",
            timeAgo = "Belum",
            isRead = false,
            prayerType = "Wajib"
        )
    )

    // Fungsi untuk mengambil notifikasi
    fun getNotifications(): List<PrayerNotification> {
        return notifications
    }

    // Fungsi untuk menandai notifikasi sebagai sudah dibaca
    fun markAsRead(notificationId: String) {
        val notification = notifications.find { it.id == notificationId }
        notification?.let {
            it.isRead = true
            it.timeAgo = "Sekarang"
        }
    }

    // Fungsi untuk mendapatkan notifikasi simulasi (misalnya dari API)
    suspend fun fetchNotifications(): List<Notification> {
        delay(500) // Simulasi delay API
        return listOf(
            Notification("1", "Waktu Sholat Shubuh telah tiba", false),
            Notification("2", "Waktu Sholat Dzuhur telah tiba", true),
            Notification("3", "Waktu Sholat Ashar telah tiba", false)
        )
    }
}
