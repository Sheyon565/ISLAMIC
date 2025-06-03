package com.example.islamic.ui.kumpulanDoa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.islamic.databinding.FragmentKumpulanDoaBinding

class KumpulanDoaActivity : AppCompatActivity() {

    private lateinit var binding: FragmentKumpulanDoaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentKumpulanDoaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Opsional: jika user tekan back di navbar Android
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
