package com.example.elikas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DisasterResponseDao {
    @Query("SELECT * FROM disaster_response ORDER BY id")
    fun getDisasterResponses(): Flow<List<DisasterResponse>>

    @Query("SELECT * FROM disaster_response WHERE id = :id")
    fun getDisasterResponseByID(id: Int): List<DisasterResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(disasterResponses: List<DisasterResponse>)

    @Delete
    suspend fun deleteDisasterResponse(disasterResponse: DisasterResponse)
    
    @Query("DELETE FROM disaster_response")
    suspend fun deleteAll()
}