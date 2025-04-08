//package com.example.lumos.entities
//
//import androidx.room.Embedded
//import androidx.room.Junction
//import androidx.room.Relation
//
//data class OrderWithArtists(
//    @Embedded
//    val order: Order,
//    @Relation(
//        parentColumn = "orderId",
//        entityColumn = "artistId",
//        associateBy = Junction(ArtistOrderCrossRef::class)
//    )
//    val artists: List<Artist>
//)
