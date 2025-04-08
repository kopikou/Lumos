//package com.example.lumos.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Transaction
//import com.example.lumos.entities.ArtistPerformanceCrossRef
//import com.example.lumos.entities.ArtistWithPerformances
//import com.example.lumos.entities.PerformanceWithArtists
//
//@Dao
//interface ArtistPerformanceCrossRefDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertArtistPerformanceCrossRef(artistPerformanceCrossRef: ArtistPerformanceCrossRef)
//
//    @Delete
//    suspend fun deleteArtistPerformanceCrossRef(artistPerformanceCrossRef: ArtistPerformanceCrossRef)
//
//    @Transaction
//    @Query("SELECT * FROM artist WHERE id = :artistId")
//    suspend fun getPerformancesForArtist(artistId: Int): List<ArtistWithPerformances>
//
//    @Transaction
//    @Query("SELECT * FROM performances WHERE id = :performanceId")
//    suspend fun getArtistsForPerformance(performanceId: Int): List<PerformanceWithArtists>
//}