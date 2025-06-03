package com.example.islamic

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.islamic.databinding.UbahPasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: UbahPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan layout dan binding class-nya benar
        binding = UbahPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // 1. Setup toolbar dengan tombol back
        setSupportActionBar(binding.toolbarChangePassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Saat tombol back di toolbar ditekan
        binding.toolbarChangePassword.setNavigationOnClickListener {
            finish()
        }

        // 2. Saat "Ubah Password" ditekan
        binding.btnSavePassword.setOnClickListener {
            val oldPassInput = binding.etOldPassword.text.toString().trim()
            val newPassInput = binding.etNewPassword.text.toString().trim()
            val confirmPassInput = binding.etConfirmPassword.text.toString().trim()

            // Validasi: semua field wajib diisi
            if (oldPassInput.isEmpty()) {
                binding.etOldPassword.error = "Password lama harus diisi"
                return@setOnClickListener
            }
            if (newPassInput.length < 8) {
                binding.etNewPassword.error = "Password baru minimal 8 karakter"
                return@setOnClickListener
            }
            if (newPassInput != confirmPassInput) {
                binding.etConfirmPassword.error = "Konfirmasi password tidak cocok"
                return@setOnClickListener
            }

            // Pastikan user sudah login dengan email/password
            if (currentUser == null || currentUser.email.isNullOrEmpty()) {
                Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lakukan re‐authentication sebelum update password
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassInput)
            currentUser.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Jika reauth sukses, baru update password
                        currentUser.updatePassword(newPassInput)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Password berhasil diubah",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    // Gagal update password (misal token kadaluarsa atau jaringan)
                                    Toast.makeText(
                                        this,
                                        "Gagal mengubah password: ${updateTask.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        // Reauthentication gagal (misal password lama salah)
                        Toast.makeText(
                            this,
                            "Password lama salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    // Jika user menekan back di navbar Android (bukan toolbar)
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
