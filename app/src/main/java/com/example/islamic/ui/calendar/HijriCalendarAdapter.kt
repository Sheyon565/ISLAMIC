package com.example.islamic.ui.calendar

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.util.Log
import com.example.islamic.data.model.HijriDateCell
import com.example.islamic.R

class HijriCalendarAdapter(private val days: List<HijriDateCell>) :
    RecyclerView.Adapter<HijriCalendarAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_calendar_day, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = days[position]
            if (item.hijriDay == null) {
                holder.tvDay.text = ""
                holder.tvDay.background = null
            } else {
                holder.tvDay.text = item.hijriDay.toString()
                if (item.isToday) {
                    holder.tvDay.setBackgroundResource(R.drawable.bg_today_circle)
                } else {
                    holder.tvDay.setBackgroundResource(R.drawable.bg_default_day)
                }
            }
        }
        override fun getItemCount(): Int = days.size
    }
