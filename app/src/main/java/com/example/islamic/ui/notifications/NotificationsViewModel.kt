package com.example.islamic.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.islamic.data.model.PrayerNotification
import com.example.islamic.data.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _notifications = MutableLiveData<List<PrayerNotification>>()
    val notifications: LiveData<List<PrayerNotification>> = _notifications

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                _notifications.value = repository.getNotifications()
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markAsRead(notificationId)
                loadNotifications() // Refresh notifications
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}
