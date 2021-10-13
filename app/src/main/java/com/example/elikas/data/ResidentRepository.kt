package com.example.elikas.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ResidentRepository(private val residentDao: ResidentDao) {

    val allResidents: Flow<List<Resident>> = residentDao.getResidents()

    fun getResidentsByFamCode(fam_code: String): Flow<List<Resident>>
        = residentDao.getResidentsByFamCode(fam_code)

    fun getEvacuees(): Flow<List<Resident>>
            = residentDao.getEvacuees()

    fun getNonEvacuees(): Flow<List<Resident>>
            = residentDao.getNonEvacuees()

    fun getFamilyHeadsEvacuees(): Flow<List<Resident>>
        =  residentDao.getFamilyHeadsEvacuees("Yes")

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun changeToEvacuee(fam_code: String)
            = residentDao.changeToEvacuee(fam_code)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun changeToNonEvacuee(fam_code: String)
            = residentDao.changeToNonEvacuee(fam_code)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun changeToEvacuees(residents: List<Resident>) {
        residentDao.changeToEvacuees(residents)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun changeToNonEvacuees(residents: List<Resident>) {
        residentDao.changeToNonEvacuees(residents)
    }

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