package com.example.lumos.domain.entities

data class EarningCreateUpdateSerializer(
    val order: Int,
    val artist: Int,
    val amount: Double,
    val paid: Boolean
)
