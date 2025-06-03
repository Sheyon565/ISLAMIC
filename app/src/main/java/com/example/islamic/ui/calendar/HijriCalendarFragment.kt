package com.example.islamic.ui.calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.islamic.R
import com.example.islamic.utils.HijriCalendarHelper
import androidx.recyclerview.widget.GridLayoutManager

class HijriCalendarFragment : Fragment() {
    private lateinit var hijriCalendarHelper: HijriCalendarHelper

    private var todayDay = 0
    private var todayMonth = 0
    private var todayYear = 0

    private var currentMonth = 0
    private var currentYear = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hijri_calendar, container, false)

        hijriCalendarHelper = HijriCalendarHelper()

        val dateComponents = hijriCalendarHelper.getTodayHijriComponents()
        todayDay = dateComponents.day
        todayMonth = dateComponents.month
        todayYear = dateComponents.year

        currentMonth = todayMonth
        currentYear = todayYear

        // Header Dates
        view.findViewById<TextView>(R.id.tvHijriDate).text = hijriCalendarHelper.getCurrentHijriDate()
        view.findViewById<TextView>(R.id.tvGregorianDate).text = hijriCalendarHelper.getCurrentGregorianDate()
        // Build Calendar Cells
        val prevBtn = view.findViewById<Button>(R.id.btnPrevMonth)
        val nextBtn = view.findViewById<Button>(R.id.btnNextMonth)

        prevBtn.setOnClickListener {
            currentMonth--
            if(currentMonth < 0) {
                currentMonth = 11
                currentYear--
            }
            updateCalendar(view)
        }

        nextBtn.setOnClickListener {
            currentMonth++
            if(currentMonth > 11) {
                currentMonth = 0
                currentYear++
            }
            updateCalendar(view)
        }

        updateCalendar(view)
        return view
    }

    private fun updateCalendar(view: View) {
        val showToday =
            if(currentMonth == todayMonth && currentYear == todayYear) todayDay else null

        val monthName = hijriCalendarHelper.getHijriMonthName(currentMonth)
        view.findViewById<TextView>(R.id.tvCurrentMonth).text = "$monthName $currentYear H"

        val cells = hijriCalendarHelper.generateHijriMonthDays(currentMonth, currentYear, showToday)
        val recyclerView = view.findViewById<RecyclerView>(R.id.calendarGrid)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        recyclerView.adapter = HijriCalendarAdapter(cells)
    }
}