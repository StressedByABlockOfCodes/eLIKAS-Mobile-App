package com.example.elikas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disaster_response")
data class DisasterResponse(
    @PrimaryKey val id: Int,
    val type: String,
) {
    override fun toString(): String {
        return this.type
    }
}
