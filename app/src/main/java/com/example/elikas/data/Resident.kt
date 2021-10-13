package com.example.elikas.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affected_residents")
data class Resident(
    val id: Int,
    @PrimaryKey val name: String,
    val family_code: String,
    val sectoral_classification: String,
    val is_family_head: String,
    var type: String,
    var is_checked: Boolean
)
