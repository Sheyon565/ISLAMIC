package com.example.islamic.data.repository

import com.example.islamic.data.model.Hadith
import kotlinx.coroutines.delay

class HadithRepository {
    suspend fun getDailyHadiths(): List<Hadith> {
        delay(700) // simulasi loading dari API/Database
        return listOf(
            Hadith(
                id = "1",
                title = "Hadist Menuntut Ilmu",
                arabicText = "طلب العلم فريضة على كل مسلم",
                translation = "Menuntut ilmu itu wajib atas setiap Muslim",
                source = "HR. Ibnu Majah"
            ),
            Hadith(
                id = "2",
                title = "Keutamaan Puasa Syawal",
                arabicText = "من صام رمضان ثم أتبعه ستا من شوال كان كصيام الدهر",
                translation = "Barang siapa yang berpuasa Ramadhan kemudian berpuasa enam hari di bulan Syawal, maka baginya (ganjaran) puasa selama setahun penuh.",
                source = "HR Muslim"
            )
        )
    }
}
