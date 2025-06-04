package com.example.islamic

import android.content.DialogInterface
import android.os.Bundle
import android.os.Build
import android.text.InputType
import android.widget.Toast
import android.util.Patterns
import android.widget.EditText
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Base64
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
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
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

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

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.imageAvatar.setImageURI(uri)
                val imageBase64 = uriToBase64(uri)
                if (imageBase64 != null) {
                    uploadImageToImgBB(imageBase64)
                } else {
                    Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermissionLauncher =
            registerForActivityResult (ActivityResultContracts.RequestPermission()) { isGranted : Boolean ->
                if (isGranted) {
                    openImagePicker()
                } else {
                    Toast.makeText(this, "Permission required to access photos", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.btnChangePhoto.setOnClickListener {
            checkStoragePermissionAndOpenPicker()
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

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun checkStoragePermissionAndOpenPicker() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun uriToBase64(uri: Uri): String? {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun uploadImageToImgBB(imageBase64: String) {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("image", imageBase64)
            .add("type", "base64")
            .build()

        val request = Request.Builder()
            .url("https://api.imgur.com/3/image")
            .addHeader("Authorization", "Client-ID f2f2dd71b755961")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity,
                        "Gagal mengunggah foto",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = JSONObject(response.body?.string() ?: "")

                if (jsonResponse.getBoolean("success")) {
                        val url = jsonResponse.getJSONObject("data").getString("link")

                        runOnUiThread {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                val profileUpdates = userProfileChangeRequest {
                                    photoUri = Uri.parse(url)
                                }
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this@EditProfileActivity,
                                                "Image uploaded: $url", Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        } else {
                                            Toast.makeText(
                                                this@EditProfileActivity,
                                                "Failed to update photo: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }

                        }
                    } else {
                        runOnUiThread{
                            Toast.makeText(this@EditProfileActivity,
                                "Upload Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
            })
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
