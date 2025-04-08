package com.example.lumos.entities
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//
//@Entity(tableName = "artist_performances",
//    primaryKeys = ["artistId", "performanceId"],
//    foreignKeys = [
//        ForeignKey(entity = Artist::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("artistId"),
//            onDelete = ForeignKey.CASCADE),
//        ForeignKey(entity = Performance::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("performanceId"),
//            onDelete = ForeignKey.CASCADE),
//        ForeignKey(entity = ShowRate::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("rateId"),
//            onDelete = ForeignKey.SET_NULL)
//    ]
//)
//data class ArtistPerformanceCrossRef(
//    val artistId: Int,
//    val performanceId: Int,
//    val rateId: Int
//)

