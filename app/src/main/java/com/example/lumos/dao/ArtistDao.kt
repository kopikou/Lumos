//package com.example.lumos.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
//import com.example.lumos.entities.Artist
//
//@Dao
//interface ArtistDao {
//    @Query("SELECT * FROM artist")
//    fun getAll(): List<Artist>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(artist: Artist)
//
//    @Update
//    suspend fun update(artist: Artist)
//
//    @Delete
//    suspend fun delete(artist: Artist)
//
//}