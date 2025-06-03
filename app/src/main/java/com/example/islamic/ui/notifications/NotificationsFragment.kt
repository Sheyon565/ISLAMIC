package com.example.islamic.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.islamic.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotificationViewModel
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        // Set up RecyclerView
        setupRecyclerView()

        // Update current time
        updateCurrentTime()

        // Observe notifications
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.submitList(notifications)
        }

        // Load notifications
        viewModel.loadNotifications()
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter { notification ->
            viewModel.markAsRead(notification.id)
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(context)
        binding.rvNotifications.adapter = adapter
    }

    private fun updateCurrentTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.tvTime.text = timeFormat.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
