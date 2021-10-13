package com.example.elikas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ResidentDao {
    @Query("SELECT * FROM affected_residents ORDER BY name")
    fun getResidents(): Flow<List<Resident>>

    @Query("SELECT * FROM affected_residents WHERE family_code = :fam_code")
    fun getResidentsByFamCode(fam_code: String):  Flow<List<Resident>>

    @Query("SELECT * FROM affected_residents WHERE type = 'Evacuee'")
    fun getEvacuees(): Flow<List<Resident>>

    @Query("SELECT * FROM affected_residents WHERE type = 'Non-evacuee'")
    fun getNonEvacuees(): Flow<List<Resident>>

    @Query("SELECT * FROM affected_residents WHERE is_family_head = :condition AND type = 'Evacuee'")
    fun getFamilyHeadsEvacuees(condition: String): Flow<List<Resident>>

    @Query("UPDATE affected_residents SET type = 'Evacuee' WHERE family_code = :fam_code")
    suspend fun changeToEvacuee(fam_code: String)

    @Query("UPDATE affected_residents SET type = 'Non-evacuee' WHERE family_code = :fam_code")
    suspend fun changeToNonEvacuee(fam_code: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun changeToEvacuees(residents: List<Resident>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun changeToNonEvacuees(residents: List<Resident>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(residents: List<Resident>)

    @Delete
    suspend fun deleteResident(resident: Resident)
    
    @Query("DELETE FROM affected_residents")
    suspend fun deleteAll()
}