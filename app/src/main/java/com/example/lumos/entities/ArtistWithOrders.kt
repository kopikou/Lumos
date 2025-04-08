//package com.example.lumos.entities
//
//import androidx.room.Embedded
//import androidx.room.Junction
//import androidx.room.Relation
//
//data class ArtistWithOrders(
//    @Embedded
//    val artist: Artist,
//    @Relation(
//        parentColumn = "artistId",
//        entityColumn = "orderId",
//        associateBy = Junction(ArtistOrderCrossRef::class)
//    )
//    val orders: List<Order>
//)
