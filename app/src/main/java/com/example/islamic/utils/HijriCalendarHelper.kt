package com.example.islamic.utils

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import com.example.islamic.data.model.HijriDateCell
import java.text.SimpleDateFormat
import java.util.*

class HijriCalendarHelper {
    data class HijriComponents(
        val day: Int,
        val month: Int,
        val year: Int,
        val dayOfWeek: Int
    )

    // Mendapatkan tanggal Hijriyah saat ini
    fun getCurrentHijriDate(): String {
       val cal = UmmalquraCalendar()
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        val monthName = getHijriMonthName(month)

        return "$day $monthName $year"
    }

    // Mendapatkan nama bulan Hijriyah
    fun getHijriMonthName(month: Int): String {
        val hijriMonths = arrayOf(
            "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
            "Jumada'ul Awwal", "Jumada'ul Akhir", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhul-Qi'dah", "Dhul-Hijjah"
        )
        return hijriMonths[month]
    }

    // Mendapatkan tanggal Gregorian saat ini
    fun getCurrentGregorianDate(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getTodayHijriComponents(): HijriComponents {
        val cal = UmmalquraCalendar()
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dayIndex = cal.get(Calendar.DAY_OF_WEEK)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        return HijriComponents(day, month, year, dayIndex)
    }

    fun getDaysInHijriMonth(month: Int, year: Int): Int {
        val cal = UmmalquraCalendar()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        // count days until month changes
        val originalMonth = cal.get(Calendar.MONTH)
        var count = 0
        while (cal.get(Calendar.MONTH) == originalMonth) {
            count++
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return count
    }

    fun hijriToGregorianDate(day: Int, month: Int, year: Int): Date {
        val cal = UmmalquraCalendar()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.time
    }

    fun generateHijriMonthDays(month: Int, year: Int, todayDay: Int?): List<HijriDateCell> {
        val result = mutableListOf<HijriDateCell>()

        val cal = UmmalquraCalendar()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - 1 )
        val daysInMonth = getDaysInHijriMonth(month, year)

        for (i in 0 until firstDayOfWeek) {
            result.add(HijriDateCell(null, false))
        }

        for (day in 1..daysInMonth) {
            val isToday = todayDay != null && day == todayDay
            result.add(HijriDateCell(day, isToday))
        }
        return result
    }
}
