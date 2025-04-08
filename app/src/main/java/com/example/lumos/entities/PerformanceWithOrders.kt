//package com.example.lumos.entities
//
//import androidx.room.Embedded
//import androidx.room.Relation
//
//data class PerformanceWithOrders(
//    @Embedded
//    val performance: Performance,
//    @Relation(parentColumn = "id", entity = Performance::class, entityColumn = "performanceId")
//    val orders: List<Order>
//)
