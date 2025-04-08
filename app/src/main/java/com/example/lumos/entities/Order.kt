//package com.example.lumos.entities
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//import java.time.LocalDate
//
//@Entity(tableName = "orders",
//    foreignKeys = [
//        ForeignKey(entity = Performance::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("performanceId"),
//            onDelete = ForeignKey.CASCADE),
//    ]
//)
//data class Order(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int,
//    val date: LocalDate,
//    val location: String,
//    val performanceId: Int,
//    val amount: Double,
//    val comment: String,
//    val completed: Boolean
//)

