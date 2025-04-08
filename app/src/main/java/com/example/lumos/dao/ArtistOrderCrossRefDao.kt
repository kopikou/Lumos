//package com.example.lumos.dao
//
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Transaction
//import com.example.lumos.entities.ArtistOrderCrossRef
//import com.example.lumos.entities.ArtistPerformanceCrossRef
//import com.example.lumos.entities.ArtistWithOrders
//import com.example.lumos.entities.ArtistWithPerformances
//import com.example.lumos.entities.OrderWithArtists
//import com.example.lumos.entities.PerformanceWithArtists
//
//interface ArtistOrderCrossRefDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertArtistOrderCrossRef(artistOrderCrossRef: ArtistOrderCrossRef)
//
//    @Delete
//    suspend fun deleteArtistOrderCrossRef(artistOrderCrossRef: ArtistOrderCrossRef)
//
//    @Transaction
//    @Query("SELECT * FROM artist WHERE id = :artistId")
//    suspend fun getOrdersForArtist(artistId: Int): List<ArtistWithOrders>
//
//    @Transaction
//    @Query("SELECT * FROM orders WHERE id = :orderId")
//    suspend fun getArtistsForOrder(orderId: Int): List<OrderWithArtists>
//}