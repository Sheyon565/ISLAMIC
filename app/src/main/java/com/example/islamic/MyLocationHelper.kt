package com.example.islamic

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class MyLocationHelper(
    private val context: Context,
    private val callback: LocationCallback
) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastLocation() {
        if (hasPermission()) {
            fusedClient.lastLocation.addOnSuccessListener { location ->
                location?.let { callback.onLocationReceived(it) }
                    ?: callback.onLocationError("Lokasi tidak tersedia")
            }
        } else {
            callback.onLocationError("Izin lokasi belum diberikan")
        }
    }

    fun getLocationName(lat: Double, lon: Double) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val geocoder = Geocoder(context, context.resources.configuration.locales[0])
            geocoder.getFromLocation(lat, lon, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    if (addresses.isNotEmpty()) {
                        callback.onLocationNameReceived(addresses[0].locality ?: "Tidak diketahui")
                    } else {
                        callback.onLocationError("Nama lokasi tidak ditemukan")
                    }
                }

                override fun onError(errorMessage: String?) {
                    callback.onLocationError("Gagal konversi lokasi: $errorMessage")
                }
            })
        } else {
            Thread {
                try {
                    val geocoder = Geocoder(context)
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val locationName = addresses[0].locality ?: "Tidak Diketahui"
                        (context as? android.app.Activity)?.runOnUiThread {
                            callback.onLocationNameReceived(locationName)
                        }

                    } else {
                        (context as? android.app.Activity)?.runOnUiThread {
                            callback.onLocationError("Nama lokasi tidak ditemukan")
                        }
                    }
                } catch (e: Exception) {
                    (context as? android.app.Activity)?.runOnUiThread {
                        callback.onLocationError("Gagal konversi lokasi: ${e.message}")
                    }
                }
            }.start()
        }
    }
    interface LocationCallback {
        fun onLocationReceived(location: Location)
        fun onLocationError(errorMessage: String)
        fun onLocationNameReceived(locationName: String)
    }
}
