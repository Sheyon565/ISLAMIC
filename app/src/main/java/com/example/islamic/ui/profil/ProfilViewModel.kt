package com.example.islamic.ui.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfilViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Profil Fragment"
    }
    val text: LiveData<String> = _text

    // Tambahkan data user profil
    private val _userName = MutableLiveData<String>().apply {
        value = "Bustami"
    }
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>().apply {
        value = "bustami@gmail.com"
    }
    val userEmail: LiveData<String> = _userEmail

    // Fungsi untuk memperbarui data profil
    fun updateUserProfile(name: String, email: String) {
        _userName.value = name
        _userEmail.value = email
    }

    // Fungsi untuk logout
    fun logout() {
        // Implementasi logout, seperti menghapus data user dari SharedPreferences
        // atau memanggil API logout
    }
}