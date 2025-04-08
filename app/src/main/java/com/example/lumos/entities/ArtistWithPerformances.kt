//package com.example.lumos.entities
//
//import androidx.room.Embedded
//import androidx.room.Junction
//import androidx.room.Relation
//
//data class ArtistWithPerformances(
//    @Embedded
//    val artist: Artist,
//    @Relation(
//        parentColumn = "artistId",
//        entityColumn = "performanceId",
//        associateBy = Junction(ArtistPerformanceCrossRef::class)
//    )
//    val performances: List<Performance>
//)
