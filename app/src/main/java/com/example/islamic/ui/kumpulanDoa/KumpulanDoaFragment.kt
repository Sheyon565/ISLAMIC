package com.example.islamic.ui.kumpulandoa

import android.os.Bundle
import android.view.*
import com.example.islamic.R
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.islamic.databinding.ActivityKumpulanDoaBinding


class KumpulanDoaFragment : Fragment() {

    private var _binding: ActivityKumpulanDoaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_kumpulan_doa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup toolbar as action bar
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbarKumpulanDoa)

        // Show back button on toolbar
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "Kumpulan Doa"

        // Enable options menu in fragment
        setHasOptionsMenu(true)
    }

    // Handle toolbar back button press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
