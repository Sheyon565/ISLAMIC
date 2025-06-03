package com.example.islamic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.islamic.databinding.ActivityKumpulanDoaBinding

class KumpulanDoaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKumpulanDoaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKumpulanDoaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set toolbar sebagai action bar
        setSupportActionBar(binding.toolbarKumpulanDoa)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kumpulan Doa"

        // Tombol back di toolbar
        binding.toolbarKumpulanDoa.setNavigationOnClickListener {
            finish() // Kembali ke Home
        }
    }

    // Opsional: jika user tekan back di navbar Android
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
