package com.example.lumos.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "artist")
data class Artist(
    //@PrimaryKey(autoGenerate = true)
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val balance: Double
)

