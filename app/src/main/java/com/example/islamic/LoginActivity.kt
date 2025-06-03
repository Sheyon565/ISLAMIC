package com.example.islamic

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.islamic.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Cek apakah user sudah login
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Kalau sudah login, langsung ke HomeActivity
            startActivity(Intent(this, HomeFragment::class.java))
            finish()
            return
        }

        // Kalau belum login, tampilkan tampilan login
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signupLink = findViewById<TextView>(R.id.tvSignup)
        val forgotLink = findViewById<TextView>(R.id.tvForgotPassword)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Login Gagal: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
            }
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotLink.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}

