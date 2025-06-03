package com.example.islamic.viewmodel

import androidx.lifecycle.*
import com.example.islamic.data.model.DailyPractice
import com.example.islamic.data.repository.PracticeRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class PracticeViewModel(private val repository: PracticeRepository) : ViewModel() {

    // Mengamati semua praktik harian
    val allPractice: LiveData<List<DailyPractice>> = repository.getAllPractices().asLiveData()

    private val _practicesForDay = MutableLiveData<List<DailyPractice>>()
    val practicesForDay: LiveData<List<DailyPractice>> = _practicesForDay

    fun loadPracticesForDay(day: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val result = repository.getPracticesForDay(day, userId)
            _practicesForDay.postValue(result)
        }
    }

    // Tambah data baru
    fun addPractice(practice: DailyPractice) = viewModelScope.launch {
        val result = repository.addPracticeWithResult(practice)
        Log.d("PracticeSave", "Inserted ID: $result")
        loadPracticesForDay(practice.day)
    }

    suspend fun getPracticesForDay(day: String): List<DailyPractice> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid?:return emptyList()
        return repository.getPracticesForDay(day, userId)
    }

    // Update data berdasarkan ID
    fun updatePractice(id: Int, practice: DailyPractice) = viewModelScope.launch {
        repository.updatePractice(id, practice)
        loadPracticesForDay(practice.day)
    }

    // Hapus data berdasarkan ID
    fun deletePractice(id: Int, day:String) = viewModelScope.launch {
        repository.deletePractice(id)
        loadPracticesForDay(day)
    }
}

class PracticeViewModelFactory(
    private val repository: PracticeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PracticeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
