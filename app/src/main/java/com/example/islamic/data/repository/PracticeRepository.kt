package com.example.islamic.data.repository

import com.example.islamic.data.repository.PracticeDao
import com.example.islamic.data.model.DailyPractice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PracticeRepository(private val practiceDao: PracticeDao) {

    // Mengambil semua praktik harian
    fun getAllPractices(): Flow<List<DailyPractice>> {
        return practiceDao.getAllPractices()
    }

    fun getPracticeForUser(userId: String): Flow<List<DailyPractice>> {
        return practiceDao.getPracticesForUser(userId)
    }

    suspend fun getPracticesForDay(day: String, userId:String): List<DailyPractice> {
        return practiceDao.getPracticesForDay(day, userId)
    }

    // Menambahkan praktik baru
    suspend fun addPractice(practice: DailyPractice) {
        practiceDao.insertPractice(practice)
    }

    suspend fun addPracticeWithResult(practice: DailyPractice): Long{
        return practiceDao.insertPractice(practice)
    }

    // Mengupdate praktik berdasarkan ID
    suspend fun updatePractice(id: Int, practice: DailyPractice) {
        val updatedPractice = practice.copy(id = id) // Pastikan id digunakan untuk update jika ada field yang memerlukan perubahan
        // Misalnya kita harus mengubah `practice.id` berdasarkan id yang diberikan
        practiceDao.updatePractice(practice) // pastikan id ada di objek DailyPractice
    }

    // Menghapus praktik berdasarkan ID
    suspend fun deletePractice(id: Int) {
        // Cari DailyPractice berdasarkan id
        val practice = practiceDao.getPracticeById(id) // buat fungsi di DAO untuk mencari berdasarkan id
        if (practice != null) {
            practiceDao.deletePractice(practice)
        }
    }
}
