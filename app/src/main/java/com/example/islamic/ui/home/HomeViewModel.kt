package com.example.islamic.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.islamic.data.model.Hadith
import com.example.islamic.data.model.UserData
import com.example.islamic.data.repository.HadithRepository
import com.example.islamic.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val hadithRepository: HadithRepository = HadithRepository()
) : ViewModel() {

    // User information LiveData
    private val _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String> = _userLocation

    private val _userInstitution = MutableLiveData<String>()
    val userInstitution: LiveData<String> = _userInstitution

    // Hadith display LiveData
    private val _dailyHadith = MutableLiveData<List<Hadith>>()
    val dailyHadith: LiveData<List<Hadith>> = _dailyHadith

    // Status LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Notification status
    private val _hasNotifications = MutableLiveData<Boolean>()
    val hasNotifications: LiveData<Boolean> = _hasNotifications

    fun getUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userData = userRepository.getUserData()
                updateUserData(userData)
                _isLoading.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Failed to load user data: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Update user interface with user data
     */
    private fun updateUserData(userData: UserData) {
        _userLocation.value = userData.location
        _userInstitution.value = userData.institution
    }

    /**
     * Fetch daily hadiths
     */
    fun getDailyHadiths() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val hadiths = hadithRepository.getDailyHadiths()
                _dailyHadith.value = hadiths
                _isLoading.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Failed to load hadiths: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Check for unread notifications
     */
    fun hasUnreadNotifications() {
        viewModelScope.launch {
            try {
                val hasUnread = userRepository.hasUnreadNotifications()
                _hasNotifications.value = hasUnread
            } catch (e: IOException) {
                _errorMessage.value = "Failed to check notifications: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Reset error message after showing it
     */
    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Initialize the ViewModel data
     */
    init {
        // Set default values
        _userLocation.value = "Samarinda"
        _userInstitution.value = "Universitas Muhammadiyah Kaltim"

        // Load initial data
        getUserData()
        getDailyHadiths()
        hasUnreadNotifications()
    }
}

/**
 * Mock repository class for demonstration purposes.
 * In a real application, these would be separate classes with proper DI.
 */
class UserRepository {
    suspend fun getUserData(): UserData {
        // Simulate network delay
        delay(500)

        // Return mock data
        return UserData(
            name = "User",
            location = "Samarinda",
            institution = "Universitas Muhammadiyah Kaltim"
        )
    }

    suspend fun hasUnreadNotifications(): Boolean {
        // Simulate network delay
        delay(300)

        // Mock result
        return true
    }
}

/**
 * Mock repository class for demonstration purposes
 */
class HadithRepository {
    suspend fun getDailyHadiths(): List<Hadith> {
        // Simulate network delay
        delay(700)

        // Return mock data
        return listOf(
            Hadith(
                id = "1",
                title = "Hadist Menuntut Ilmu",
                arabicText = "طلب العلم فريضة على كل مسلم",
                translation = "Menuntut ilmu itu wajib atas setiap Muslim",
                source = "HR. Ibnu Majah"
            ),
            Hadith(
                id = "2",
                title = "Hadist Keutamaan Puasa Syawal",
                arabicText = "من صام رمضان ثم أتبعه ستا من شوال كان كصيام الدهر",
                translation = "Barang siapa yang berpuasa Ramadhan kemudian berpuasa enam hari di bulan Syawal, maka baginya (ganjaran) puasa selama setahun penuh.",
                source = "HR Muslim"
            )
        )
    }
}

/**
 * Data class for user data
 */
data class UserData(
    val name: String,
    val location: String,
    val institution: String
)