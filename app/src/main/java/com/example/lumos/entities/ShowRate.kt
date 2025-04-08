package com.example.lumos.entities

import com.google.gson.annotations.SerializedName

data class ShowRate(
    val id: Int = 0,
    @SerializedName("show_type") val showType: Type,
    val rate: Double
)

