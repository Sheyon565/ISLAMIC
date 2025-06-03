package com.example.islamic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pesan = intent.getStringExtra("pesan") ?: "Waktu Sholat telah tiba!"
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Buat channel notifikasi
        val channelId = "sholat_channel"
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_azan) // ganti dengan icon notifikasi kamu
            .setContentTitle("Pengingat Sholat")
            .setContentText(pesan)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sholat Reminder", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        manager.notify(1, builder.build())

        // Simpan log ke Firebase Realtime Database
        val db = FirebaseFirestore.getInstance()
        val log = mapOf("pesan" to pesan, "waktu" to System.currentTimeMillis())
        db.collection("notifikasi").add(log)
    }
}