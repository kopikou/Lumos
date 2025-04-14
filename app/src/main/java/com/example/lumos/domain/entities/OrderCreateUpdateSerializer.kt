package com.example.lumos.domain.entities

data class OrderCreateUpdateSerializer(
    val date: String,
    val location: String,
    val performance: Int,
    val amount: Double,
    val comment: String,
    val completed: Boolean
)
