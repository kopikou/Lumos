package com.example.lumos.domain.entities
import com.google.gson.annotations.SerializedName

data class Artist(
    val id: Int = 0,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val phone: String,
    val balance: Double
)