package com.example.elikas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ResidentDao {
    @Query("SELECT * FROM affected_residents ORDER BY name")
    fun getResidents(): Flow<List<Resident>>

    @Query("SELECT * FROM affected_residents WHERE family_code = :fam_code")
    fun getResidentsByFamCode(fam_code: String): List<Resident>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(residents: List<Resident>)

    @Delete
    suspend fun deleteResident(resident: Resident)
    
    @Query("DELETE FROM affected_residents")
    suspend fun deleteAll()
}