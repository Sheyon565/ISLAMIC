package com.example.islamic

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.etForgotEmail)
        val resetButton = findViewById<Button>(R.id.forgotBtn)
        val backToLogin = findViewById<TextView>(R.id.tvLoginRedirect)

        resetButton.setOnClickListener {
            val email = emailField.text.toString()

            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email reset password berhasil dikirim", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Isi field email!", Toast.LENGTH_SHORT).show()

            }
        }

        backToLogin.setOnClickListener {
            finish()
        }
    }
}