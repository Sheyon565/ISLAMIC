package com.example.islamic.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.islamic.R
import com.example.islamic.data.model.PrayerNotification
import com.example.islamic.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val onNotificationClick: (PrayerNotification) -> Unit
) : ListAdapter<PrayerNotification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onNotificationClick(getItem(position))
                }
            }

            binding.ivMore.setOnClickListener { view ->
                val notification = getItem(adapterPosition)
                showPopupMenu(view, notification)
            }
        }

        fun bind(notification: PrayerNotification) {
            binding.apply {
                tvPrayerName.text = notification.title
                tvPrayerInfo.text = notification.message
                tvTime.text = notification.timeAgo

                // Set icon based on prayer type if needed
                // ivIcon.setImageResource(getIconForPrayerType(notification.prayerType))
            }
        }

        private fun showPopupMenu(view: android.view.View, notification: PrayerNotification) {
            PopupMenu(view.context, view).apply {
                inflate(R.menu.menu_notification_options)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_mark_read -> {
                            onNotificationClick(notification)
                            true
                        }
                        R.id.action_delete -> {
                            // Handle delete action
                            // viewModel.deleteNotification(notification.id)
                            true
                        }
                        else -> false
                    }
                }
                show()
            }
        }
    }
}

class NotificationDiffCallback : DiffUtil.ItemCallback<PrayerNotification>() {
    override fun areItemsTheSame(oldItem: PrayerNotification, newItem: PrayerNotification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PrayerNotification, newItem: PrayerNotification): Boolean {
        return oldItem == newItem
    }
}