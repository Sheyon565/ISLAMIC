package com.example.islamic

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.islamic.databinding.EditProfilBinding
import com.example.islamic.utils.PrefsHelper

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup Toolbar dengan tombol back
        setSupportActionBar(binding.toolbarEditProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarEditProfile.setNavigationOnClickListener {
            // Cukup finish, kembali ke ProfilFragment
            finish()
        }

        // 2. Prefill form dengan data dari SharedPreferences (jika sudah pernah disimpan)
        val currentName = PrefsHelper.getName(this) ?: ""
        val currentEmail = PrefsHelper.getEmail(this) ?: ""
        val currentPhone = PrefsHelper.getPhone(this) ?: ""

        binding.etFullname.setText(currentName)
        binding.etEmail.setText(currentEmail)
        binding.etPhone.setText(currentPhone)

        // 3. Tombol “Simpan Perubahan” diklik
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etFullname.text.toString().trim()
            val newEmail = binding.etEmail.text.toString().trim()
            val newPhone = binding.etPhone.text.toString().trim()

            // Validasi sederhana: tidak boleh kosong
            if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan ke SharedPreferences
            PrefsHelper.setProfile(this, newName, newEmail, newPhone)
            Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            finish()  // Kembali ke ProfilFragment
        }
    }
}
