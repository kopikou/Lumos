//package com.example.lumos.entities
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "performances",
//    foreignKeys = [
//        ForeignKey(entity = Type::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("type"),
//            onDelete = ForeignKey.CASCADE)
//    ])
//data class Performance(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int,
//    val title: String,
//    val duration: Int,
//    val cost: Double,
//    val type: Int,
//    val cntArtists: Int
//)
