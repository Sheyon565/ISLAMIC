package com.example.islamic

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Calendar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private fun showLoading() {
        findViewById<ConstraintLayout>(R.id.loadingoverlay).visibility = View.VISIBLE
    }

    private fun hideLoading() {
        findViewById<ConstraintLayout>(R.id.loadingoverlay).visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val nameField = findViewById<EditText>(R.id.etName)
        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val dobField = findViewById<EditText>(R.id.etDob)
        val signupButton = findViewById<Button>(R.id.btnSignup)
        val loginRedirect = findViewById<TextView>(R.id.tvLoginRedirect)

        dobField.isFocusable = false
        dobField.isClickable = true

        fun setInputEnabled(enabled: Boolean) {
            nameField.isEnabled = enabled
            emailField.isEnabled = enabled
            passwordField.isEnabled = enabled
            dobField.isEnabled = enabled
            signupButton.isEnabled = enabled
        }

        dobField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format(Locale.US,"%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                dobField.setText(selectedDate)
            }, year, month, day )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }

        signupButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val dob = dobField.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && dob.isNotEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Email tidak valid!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (password.length < 6) {
                    Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                showLoading()
                setInputEnabled(false)
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        hideLoading()
                        setInputEnabled(true)
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                            val user = mapOf(
                                "name" to name,
                                "email" to email,
                                "dob" to dob
                            )
                            db.collection("users").document(userId).set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal menyimpan data: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
            }
        }

        loginRedirect.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
