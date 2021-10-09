package com.example.elikas.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ResidentRepository(private val residentDao: ResidentDao) {

    val allResidents: Flow<List<Resident>> = residentDao.getResidents()

    fun getResidentsByFamCode(fam_code: String) = residentDao.getResidentsByFamCode(fam_code)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(residents: List<Resident>) {
        residentDao.insertAll(residents)
    }

    suspend fun removeResident(resident: Resident) {
        residentDao.deleteResident(resident)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeAll() {
        residentDao.deleteAll()
    }

}