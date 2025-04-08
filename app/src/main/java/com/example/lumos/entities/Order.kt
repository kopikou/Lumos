package com.example.lumos.entities

data class Order(
    val id: Int = 0,
    val date: String,
    val location: String,
    val performance: Performance,
    val amount: Double,
    val comment: String,
    val completed: Boolean
)

