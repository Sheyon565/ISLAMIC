package com.example.islamic.ui.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.islamic.ChangePasswordActivity
import com.example.islamic.EditProfileActivity
import com.example.islamic.LoginActivity
import com.example.islamic.R
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

        // Tampilkan data user
        setUserData(auth.currentUser)

        // Listener tombol
        setupClickListeners()

        // Bottom navigation
        return view
    }

    private fun setUserData(user: FirebaseUser?) {
        profileName.text = user?.displayName ?: "User"
        profileEmail.text = user?.email ?: "No Email"
        Log.d("ProfilFragment", "Display Name: ${user?.displayName}")
    }

    private fun setupClickListeners() {
        editProfileItem.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }

        changePasswordItem.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "Change Password clicked", Toast.LENGTH_SHORT).show()
        }

        logoutItem.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }


}
