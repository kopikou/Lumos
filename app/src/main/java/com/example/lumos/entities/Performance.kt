package com.example.lumos.entities

import com.google.gson.annotations.SerializedName

data class Performance(
    val id: Int = 0,
    val title: String,
    val duration: Int,
    val cost: Double,
    val type: Type,
    @SerializedName("cnt_artists") val cntArtists: Int
)
