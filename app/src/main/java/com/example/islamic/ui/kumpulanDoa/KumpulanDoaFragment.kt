package com.example.islamic.ui.kumpulanDoa

import android.os.Bundle
import android.view.*
import com.example.islamic.R
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.islamic.databinding.FragmentKumpulanDoaBinding


class KumpulanDoaFragment : Fragment() {

    private var _binding: FragmentKumpulanDoaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKumpulanDoaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup toolbar as action bar
        val activity = requireActivity() as AppCompatActivity

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
