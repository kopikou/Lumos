//package com.example.lumos.entities
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//
//@Entity(tableName = "earnings",
//    primaryKeys = ["orderId", "artistId"],
//    foreignKeys = [
//        ForeignKey(entity = Order::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("orderId"),
//            onDelete = ForeignKey.CASCADE),
//        ForeignKey(entity = Artist::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("artistId"),
//            onDelete = ForeignKey.CASCADE)
//    ]
//)
//data class ArtistOrderCrossRef(
//    val orderId: Int,
//    val artistId: Int,
//    val amount: Double,
//    val paid: Boolean
//)
