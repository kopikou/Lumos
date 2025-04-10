package com.example.lumos.domain.entities

data class Earning(
    val order: Order,
    val artist: Artist,
    val amount: Double,
    val paid: Boolean
)
