package com.example.elikas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affected_residents")
data class Resident(
    val family_code: String,
    val sectoral_classification: String,
    @PrimaryKey val name: String
)
