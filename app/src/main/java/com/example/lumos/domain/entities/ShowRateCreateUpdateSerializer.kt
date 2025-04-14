package com.example.lumos.domain.entities

import com.google.gson.annotations.SerializedName

data class ShowRateCreateUpdateSerializer(
    @SerializedName("show_type") val showType: Int,
    val rate: Double
)
