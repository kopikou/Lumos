//package com.example.lumos.entities
//
//import androidx.room.Embedded
//import androidx.room.Junction
//import androidx.room.Relation
//
//data class PerformanceWithArtists(
//    @Embedded
//    val performance: Performance,
//    @Relation(
//        parentColumn = "performanceId",
//        entityColumn = "artistId",
//        associateBy = Junction(ArtistPerformanceCrossRef::class)
//    )
//    val artists: List<Artist>
//)
