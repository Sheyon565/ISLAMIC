package com.example.islamic.ui.calendar

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.islamic.R
import android.widget.CalendarView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

// Adapter untuk ViewPager2 agar dapat swipe antar bulan
class CalendarPagerAdapter(
    private val onDateSelected: (selectedMillis: Long) -> Unit
) : RecyclerView.Adapter<CalendarPagerAdapter.CalendarViewHolder>() {

    companion object {
        // Kita gunakan total 1000 halaman, dengan halaman tengah sebagai "bulan sekarang"
        const val TOTAL_PAGES = 1000
        const val MID_PAGE = TOTAL_PAGES / 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_page, parent, false)
        return CalendarViewHolder(view)
    }

    override fun getItemCount(): Int = TOTAL_PAGES

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        // Hitung selisih bulan dari posisi tengah
        val offsetFromCurrent = position - MID_PAGE

        // Ambil instance Calendar (Android ICU) untuk menghitung firstDayOfMonth
        val cal = Calendar.getInstance()
        // Set ke tanggal 1 bulan ini terlebih dahulu
        cal.set(Calendar.DAY_OF_MONTH, 1)
        // Tambahkan offset bulan
        cal.add(Calendar.MONTH, offsetFromCurrent)

        // Set CalendarView supaya fokus ke bulan tersebut (tanggal 1)
        holder.calendarView.date = cal.timeInMillis

        // Saat user memilih satu tanggal di CalendarView, panggil callback:
        holder.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Hitung waktu milis actual dari tanggal yang dipilih
            val selCal = Calendar.getInstance()
            selCal.set(year, month, dayOfMonth)
            onDateSelected(selCal.timeInMillis)
        }
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val calendarView: CalendarView = itemView.findViewById(R.id.calendarView)
    }
}