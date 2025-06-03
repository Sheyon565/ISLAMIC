package com.example.islamic.ui.prayer

import android.Manifest
import android.annotation.SuppressLint
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
import com.batoulapps.adhan.*
import com.batoulapps.adhan.data.DateComponents
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import com.example.islamic.MyLocationHelper
import com.example.islamic.R
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat


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
        return inflater.inflate(R.layout.activity_jadwal_sholat, container, false)
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

                view?.let {
                    updatePrayerTimesUI(it, prayerTimes)
                    getCurrentDate(it)
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

}
