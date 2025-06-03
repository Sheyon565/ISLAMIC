package com.example.islamic.ui.daily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.islamic.R
import com.example.islamic.data.model.DailyPractice

class DailyPracticeAdapter(
    private val onEditClick: (DailyPractice) -> Unit,
    private val onDeleteClick: (DailyPractice) -> Unit
) : ListAdapter<DailyPractice, DailyPracticeAdapter.PracticeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_practice, parent, false)
        return PracticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PracticeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PracticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvPracticeName)
        private val tvTime: TextView = itemView.findViewById(R.id.tvPracticeTime)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(practice: DailyPractice) {
            val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val formattedTime = formatter.format(java.util.Date(practice.time))
            tvName.text = practice.name
            tvTime.text = formattedTime

            btnEdit.setOnClickListener { onEditClick(practice) }
            btnDelete.setOnClickListener { onDeleteClick(practice) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DailyPractice>() {
        override fun areItemsTheSame(oldItem: DailyPractice, newItem: DailyPractice): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DailyPractice, newItem: DailyPractice): Boolean {
            return oldItem == newItem
        }
    }
}
