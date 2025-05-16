package com.example.lumos.domain.entities

import com.google.gson.annotations.SerializedName

data class PerformanceCreateUpdateDto(
    val title: String,
    val duration: Int,
    val cost: Double,
    val type: Int,
    @SerializedName("cnt_artists") val cntArtists: Int
)