package com.example.islamic.ui.prayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.TextView
import android.util.Log
import android.widget.ImageButton
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.batoulapps.adhan.*
import com.batoulapps.adhan.data.DateComponents
import com.example.islamic.AlarmReceiver
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import com.example.islamic.MyLocationHelper
import com.example.islamic.R
import java.util.Calendar
import java.util.Date


class JadwalSholatFragment : Fragment() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var locationHelper: MyLocationHelper

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationHelper.getLastLocation()
            } else {
                Toast.makeText(requireContext(), "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(requireContext(), "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_jadwal_sholat, container, false)
    }

    private fun updatePrayerTimesUI(view: View, prayerTimes: PrayerTimes) {
        val timeFormat = java.text.SimpleDateFormat("HH:mm")

        view.findViewById<TextView>(R.id.tvSubuhTime).text = timeFormat.format(prayerTimes.fajr)
        view.findViewById<TextView>(R.id.tvDhuzurTime).text = timeFormat.format(prayerTimes.dhuhr)
        view.findViewById<TextView>(R.id.tvAsharTime).text = timeFormat.format(prayerTimes.asr)
        view.findViewById<TextView>(R.id.tvMaghribTime).text = timeFormat.format(prayerTimes.maghrib)
        view.findViewById<TextView>(R.id.tvIsyaTime).text = timeFormat.format(prayerTimes.isha)
    }

    fun getCurrentDate(view: View) {
        // Normal Date
        val calendar = Calendar.getInstance()
        val dayOfWeek = android.text.format.DateFormat.format("EEEE", calendar)
        val fullDate = android.text.format.DateFormat.format("MMMM dd, yyyy", calendar)

        // Hijri Date Umm Al-Qura Calendar
        // Hijri Date using Umm al-Qura Calendar
        val hijriCalendar = UmmalquraCalendar()
        val hijriDay = hijriCalendar.get(Calendar.DAY_OF_MONTH)
        val hijriMonthIndex = hijriCalendar.get(Calendar.MONTH)
        val hijriYear = hijriCalendar.get(Calendar.YEAR)

        val hijriMonthNames = arrayOf(
            "Muharram", "Safar", "Rabiʿ al-awwal", "Rabiʿ al-thani",
            "Jumada al-awwal", "Jumada al-thani", "Rajab", "Shaʿban",
            "Ramadan", "Shawwal", "Dhu al-Qiʿdah", "Dhu al-Ḥijjah"
        )
        val hijriMonthName = hijriMonthNames[hijriMonthIndex]
        val hijriDate = "$hijriDay $hijriMonthName, $hijriYear"

        view.findViewById<TextView>(R.id.tvDay).text = "Today / $dayOfWeek"
        view.findViewById<TextView>(R.id.tvDate).text = "$fullDate / $hijriDate"

    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationHelper = MyLocationHelper(requireContext(), object : MyLocationHelper.LocationCallback {
            override fun onLocationReceived(location: Location) {
                val lat = location.latitude
                val lon = location.longitude

                val coordinates = Coordinates(lat, lon)
                val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters

                val prayerTimes = PrayerTimes(
                    coordinates,
                    DateComponents.from(Calendar.getInstance().time),
                    params
                )

                Log.d("PrayerTime", "Fajr: ${prayerTimes.fajr}")
                Log.d("PrayerTime", "Dhuhr: ${prayerTimes.dhuhr}")
                Log.d("PrayerTime", "Asr: ${prayerTimes.asr}")
                Log.d("PrayerTime", "Maghrib: ${prayerTimes.maghrib}")
                Log.d("PrayerTime", "Isha: ${prayerTimes.isha}")

                Toast.makeText(requireContext(), "Lokasi: $lat, $lon", Toast.LENGTH_SHORT).show()

                view.let {
                    updatePrayerTimesUI(it, prayerTimes)
                    getCurrentDate(it)

                    val subuhBtn = it.findViewById<ImageButton>(R.id.ivSubuhNotify)
                    val subuhName = "subuh"
                    val dhuzurBtn = it.findViewById<ImageButton>(R.id.ivDhuzurNotify)
                    val dhuzurName = "dhuzur"
                    val asharBtn = it.findViewById<ImageButton>(R.id.ivAsharNotify)
                    val asharName = "ashar"
                    val maghribBtn = it.findViewById<ImageButton>(R.id.ivMaghribNotify)
                    val maghribName = "maghrib"
                    val isyaBtn = it.findViewById<ImageButton>(R.id.ivIsyaNotify)
                    val isyaName = "isya"

// Set initial icon based on saved state
                    if (isNotificationEnabled(subuhName)) {
                        subuhBtn.setImageResource(R.drawable.notifications_off_24px)
                    } else {
                        subuhBtn.setImageResource(R.drawable.notifications_active_24px)
                    }
                    subuhBtn.setOnClickListener {
                        val enabled = isNotificationEnabled(subuhName)
                        if (enabled) {
                            cancelPrayerNotification(subuhName)
                            setNotificationEnabled(subuhName, false)
                            subuhBtn.setImageResource(R.drawable.notifications_active_24px)
                            Toast.makeText(requireContext(), "Notifikasi Subuh dimatikan", Toast.LENGTH_SHORT).show()
                        } else {
                            schedulePrayerNotification(prayerTimes.fajr, subuhName)
                            setNotificationEnabled(subuhName, true)
                            subuhBtn.setImageResource(R.drawable.notifications_off_24px)
                            Toast.makeText(requireContext(), "Notifikasi Subuh diaktifkan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (isNotificationEnabled(dhuzurName)) {
                        dhuzurBtn.setImageResource(R.drawable.notifications_off_24px)
                    } else {
                        dhuzurBtn.setImageResource(R.drawable.notifications_active_24px)
                    }
                    dhuzurBtn.setOnClickListener {
                        val enabled = isNotificationEnabled(dhuzurName)
                        if (enabled) {
                            cancelPrayerNotification(dhuzurName)
                            setNotificationEnabled(dhuzurName, false)
                            dhuzurBtn.setImageResource(R.drawable.notifications_active_24px)
                            Toast.makeText(requireContext(), "Notifikasi Dhuzur dimatikan", Toast.LENGTH_SHORT).show()
                        } else {
                            schedulePrayerNotification(prayerTimes.dhuhr, dhuzurName)
                            setNotificationEnabled(dhuzurName, true)
                            dhuzurBtn.setImageResource(R.drawable.notifications_off_24px)
                            Toast.makeText(requireContext(), "Notifikasi Dhuzur diaktifkan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (isNotificationEnabled(asharName)) {
                        asharBtn.setImageResource(R.drawable.notifications_off_24px)
                    } else {
                        asharBtn.setImageResource(R.drawable.notifications_active_24px)
                    }
                    asharBtn.setOnClickListener {
                        val enabled = isNotificationEnabled(asharName)
                        if (enabled) {
                            cancelPrayerNotification(asharName)
                            setNotificationEnabled(asharName, false)
                            asharBtn.setImageResource(R.drawable.notifications_active_24px)
                            Toast.makeText(requireContext(), "Notifikasi Ashar dimatikan", Toast.LENGTH_SHORT).show()
                        } else {
                            schedulePrayerNotification(prayerTimes.asr, asharName)
                            setNotificationEnabled(asharName, true)
                            asharBtn.setImageResource(R.drawable.notifications_off_24px)
                            Toast.makeText(requireContext(), "Notifikasi Ashar diaktifkan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (isNotificationEnabled(maghribName)) {
                        maghribBtn.setImageResource(R.drawable.notifications_off_24px)
                    } else {
                        maghribBtn.setImageResource(R.drawable.notifications_active_24px)
                    }
                    maghribBtn.setOnClickListener {
                        val enabled = isNotificationEnabled(maghribName)
                        if (enabled) {
                            cancelPrayerNotification(maghribName)
                            setNotificationEnabled(maghribName, false)
                            maghribBtn.setImageResource(R.drawable.notifications_active_24px)
                            Toast.makeText(requireContext(), "Notifikasi Maghrib dimatikan", Toast.LENGTH_SHORT).show()
                        } else {
                            schedulePrayerNotification(prayerTimes.maghrib, maghribName)
                            setNotificationEnabled(maghribName, true)
                            maghribBtn.setImageResource(R.drawable.notifications_off_24px)
                            Toast.makeText(requireContext(), "Notifikasi Maghrib diaktifkan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (isNotificationEnabled(isyaName)) {
                        isyaBtn.setImageResource(R.drawable.notifications_off_24px)
                    } else {
                        isyaBtn.setImageResource(R.drawable.notifications_active_24px)
                    }
                    isyaBtn.setOnClickListener {
                        val enabled = isNotificationEnabled(isyaName)
                        if (enabled) {
                            cancelPrayerNotification(isyaName)
                            setNotificationEnabled(isyaName, false)
                            isyaBtn.setImageResource(R.drawable.notifications_active_24px)
                            Toast.makeText(requireContext(), "Notifikasi Isya dimatikan", Toast.LENGTH_SHORT).show()
                        } else {
                            schedulePrayerNotification(prayerTimes.isha, isyaName)
                            setNotificationEnabled(isyaName, true)
                            isyaBtn.setImageResource(R.drawable.notifications_off_24px)
                            Toast.makeText(requireContext(), "Notifikasi Isya diaktifkan", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Ambil nama lokasi
                locationHelper.getLocationName(lat, lon)
            }

            override fun onLocationError(errorMessage: String) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onLocationNameReceived(locationName: String) {
                Toast.makeText(requireContext(), "Kota: $locationName", Toast.LENGTH_SHORT).show()
            }
        })
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationHelper.getLastLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun schedulePrayerNotification(prayerTime: Date, prayerName: String) {
        val context = requireContext()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("pesan", "waktu Sholat $prayerName telah tiba!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    context,
                    "Please enable exact alarm permission in system settings to receive accurate prayer notifications.",
                    Toast.LENGTH_LONG
                ).show()
                // Optionally, open system settings for this app:
                openExactAlarmSettings(context)
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            prayerTime.time,
            pendingIntent
        )
    }

    private fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = android.net.Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback: open app details page
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            }
        }
    }

    private fun isNotificationEnabled(prayerName: String): Boolean {
        val prefs = requireContext().getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("notif_$prayerName", false)
    }

    private fun setNotificationEnabled(prayerName: String, enabled: Boolean) {
        val prefs = requireContext().getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notif_$prayerName", enabled).apply()
    }

    private fun cancelPrayerNotification(prayerName: String) {
        val context = requireContext()
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}
