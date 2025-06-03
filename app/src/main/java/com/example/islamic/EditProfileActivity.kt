package com.example.islamic

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.islamic.databinding.EditProfilBinding
import com.example.islamic.utils.PrefsHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup Toolbar dengan tombol back
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Edit Profile"
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

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                if (newEmail != user.email) {
                    showReauthDialog(user, newEmail, newName, newPhone)
                } else {
                    updateUserProfile(user, newName, newEmail, newPhone)
                }
            } else {
                PrefsHelper.setProfile(this, newName, newEmail, newPhone)
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()  // Kembali ke ProfilFragment
            }

            // Simpan ke SharedPreferences
        }
    }

    private fun showReauthDialog(
        user: FirebaseUser,
        newEmail: String,
        newName: String,
        newPhone: String
    ) {
        val passwordInput = EditText(this)
        passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        AlertDialog.Builder(this)
            .setTitle("Re-authentication required")
            .setMessage("Please enter your current password to update your email")
            .setView(passwordInput)
            .setPositiveButton("Confirm") { dialog: DialogInterface, _ ->
                val currentPassword = passwordInput.text.toString()
                if (currentPassword.isEmpty()) {
                    Toast.makeText(this, "Password harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updateEmail(newEmail).addOnCompleteListener { updateEmailTask ->
                            if (updateEmailTask.isSuccessful) {
                                updateUserProfile(user, newName, newEmail, newPhone)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Gagal memperbaharui email: ${updateEmailTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Re-authentication failed: ${reauthTask.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateUserProfile (
        user: FirebaseUser,
        newName: String,
        newEmail: String,
        newPhone: String
    ) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
        }

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { updateProfileTask ->
                if (updateProfileTask.isSuccessful) {
                    PrefsHelper.setProfile(this, newName, newEmail, newPhone)
                    Toast.makeText(this, "Profile berhasil diperbaharui", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal memperbaharui profile: ${updateProfileTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
