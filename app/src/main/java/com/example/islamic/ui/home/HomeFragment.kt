package com.example.islamic.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.islamic.R
import com.example.islamic.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Set up UI elements
        setupUserInfo()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupUserInfo() {
        // Get user data from ViewModel
        viewModel.getUserData()

        // Observe user location updates
        viewModel.userLocation.observe(viewLifecycleOwner) { location ->
            binding.tvUserLocation.text = location
        }

        // Observe user institution updates
        viewModel.userInstitution.observe(viewLifecycleOwner) { institution ->
            binding.tvUserCampus.text = institution
        }
    }

    private fun setupClickListeners() {
        // Set up prayer schedule click listener
        binding.cardJadwalSholat.setOnClickListener {
            navigateToFeature(FeatureType.PRAYER_SCHEDULE)
        }
        binding.tvJadwalSholat.setOnClickListener {
            navigateToFeature(FeatureType.PRAYER_SCHEDULE)
        }

        // Set up prayer collection click listener
        binding.cardKumpulanDoa.setOnClickListener {
            navigateToFeature(FeatureType.PRAYER_COLLECTION)
        }

        // Set up Hijri calendar click listener
        binding.cardKalenderHijriyah.setOnClickListener {
            navigateToFeature(FeatureType.HIJRI_CALENDAR)
        }

        // Set up daily practices click listener
        binding.cardAmalanHarian.setOnClickListener {
            navigateToFeature(FeatureType.DAILY_PRACTICES)
        }

        // Set up notification click listener
        binding.ivNotification.setOnClickListener {
            viewModel.hasUnreadNotifications()
            navigateToFeature(FeatureType.NOTIFICATIONS)
        }
    }

    private fun observeViewModel() {
        // Observe daily hadith updates
        viewModel.dailyHadith.observe(viewLifecycleOwner) { hadithList ->
            // If we have at least one hadith, display it
            if (hadithList.isNotEmpty()) {
                // Update UI with the hadith (this implementation would depend on your exact UI layout)
                // For example, you might update a RecyclerView or specific TextViews
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                // Reset error message after showing it
                viewModel.resetErrorMessage()
            }
        }
    }

    private fun navigateToFeature(featureType: FeatureType) {
        // Implementation depends on your navigation setup
        when (featureType) {
            FeatureType.PRAYER_SCHEDULE -> {
                // Navigate to Prayer Schedule screen
                // findNavController().navigate(R.id.action_homeFragment_to_prayerScheduleFragment)
                findNavController().navigate(R.id.action_homeFragment_to_jadwalSholatFragment)
                Toast.makeText(context, "Navigating to Prayer Schedule", Toast.LENGTH_SHORT).show()
            }

            FeatureType.PRAYER_COLLECTION -> {
                // Navigate to Prayer Collection screen
                // findNavController().navigate(R.id.action_homeFragment_to_prayerCollectionFragment)
                findNavController().navigate(R.id.action_homeFragment_to_kumpulanDoa)
                Toast.makeText(context, "Navigating to Prayer Collection", Toast.LENGTH_SHORT).show()
            }
            FeatureType.HIJRI_CALENDAR -> {
                // Navigate to Hijri Calendar screen
                // findNavController().navigate(R.id.action_homeFragment_to_hijriCalendarFragment)
                findNavController().navigate(R.id.action_homeFragment_to_kalenderHijriyahFragment)
                Toast.makeText(context, "Navigating to Hijri Calendar", Toast.LENGTH_SHORT).show()
            }
            FeatureType.DAILY_PRACTICES -> {
                // Navigate to Daily Practices screen
                // findNavController().navigate(R.id.action_homeFragment_to_dailyPracticesFragment)
                findNavController().navigate(R.id.action_homeFragment_to_amalanHarianFragment)
                Toast.makeText(context, "Navigating to Daily Practices", Toast.LENGTH_SHORT).show()
            }
            FeatureType.NOTIFICATIONS -> {
                // Navigate to Notifications screen
                // findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
                findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
                Toast.makeText(context, "Navigating to Notifications", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Enum class for feature types
enum class FeatureType {
    PRAYER_SCHEDULE,
    PRAYER_COLLECTION,
    HIJRI_CALENDAR,
    DAILY_PRACTICES,
    NOTIFICATIONS
}
