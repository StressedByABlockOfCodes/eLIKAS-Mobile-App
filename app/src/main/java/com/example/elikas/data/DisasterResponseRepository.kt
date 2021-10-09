package com.example.elikas.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class DisasterResponseRepository(private val disasterResponseDao: DisasterResponseDao) {

    val allDisasterResponses: Flow<List<DisasterResponse>> = disasterResponseDao.getDisasterResponses()

    fun getDisasterResponseByID(id: Int) = disasterResponseDao.getDisasterResponseByID(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(disasterResponses: List<DisasterResponse>) {
        disasterResponseDao.insertAll(disasterResponses)
    }

    suspend fun removeDisasterResponse(disasterResponse: DisasterResponse) {
        disasterResponseDao.deleteDisasterResponse(disasterResponse)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeAll() {
        disasterResponseDao.deleteAll()
    }

}