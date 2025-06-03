package com.example.islamic.ui.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.islamic.LoginActivity
import com.example.islamic.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var editProfileItem: LinearLayout
    private lateinit var changePasswordItem: LinearLayout
    private lateinit var logoutItem: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        auth = FirebaseAuth.getInstance()

        // Inisialisasi view dari layout
        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        editProfileItem = view.findViewById(R.id.editProfileItem)
        changePasswordItem = view.findViewById(R.id.changePasswordItem)
        logoutItem = view.findViewById(R.id.logoutItem)
        bottomNavigation = view.findViewById(R.id.bottomNavigation)

        // Tampilkan data user
        setUserData(auth.currentUser)

        // Listener tombol
        setupClickListeners()

        // Bottom navigation
        setupBottomNavigation()

        return view
    }

    private fun setUserData(user: FirebaseUser?) {
        profileName.text = user?.displayName ?: "User"
        profileEmail.text = user?.email ?: "No Email"
    }

    private fun setupClickListeners() {
        editProfileItem.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }

        changePasswordItem.setOnClickListener {
            Toast.makeText(requireContext(), "Change Password clicked", Toast.LENGTH_SHORT).show()
        }

        logoutItem.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_profil

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_notifications -> {
                    Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_profil -> true
                else -> false
            }
        }
    }
}
