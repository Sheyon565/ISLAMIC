package com.example.islamic.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.islamic.R
import com.example.islamic.data.model.PrayerTime


class PrayerTimesAdapter (private val prayerTimes: List<PrayerTime>) :
    RecyclerView.Adapter<PrayerTimesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val tvPrayerName: TextView = itemView.findViewById(R.id.tvPrayerName)
        val tvPrayerTime: TextView = itemView.findViewById(R.id.tvPrayerTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prayer_time, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prayerTime = prayerTimes[position]
        holder.tvPrayerName.text = prayerTime.name
        holder.tvPrayerTime.text = prayerTime.time
    }

    override fun getItemCount(): Int = prayerTimes.size
    }