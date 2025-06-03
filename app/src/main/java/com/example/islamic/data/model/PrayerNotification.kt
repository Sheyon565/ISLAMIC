package com.example.islamic.data.model

/**
 * Data class representing a prayer notification
 */
data class PrayerNotification(
    val id: String,
    val title: String,
    val message: String,
    var timeAgo: String,
    var isRead: Boolean = false,
    val prayerType: String,
    val prayerName: String,
    val time: String
)

/**
 * Enum representing different prayer types
 */
enum class PrayerType {
    SUBUH,
    DZUHUR,
    ASHAR,
    MAGHRIB,
    ISYA,
    OTHER
}