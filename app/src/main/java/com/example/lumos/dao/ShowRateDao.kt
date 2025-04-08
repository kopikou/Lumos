//package com.example.lumos.dao
//
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
//import com.example.lumos.entities.ShowRate
//
//interface ShowRateDao {
//    @Query("SELECT * FROM show_rates")
//    fun getAll(): List<ShowRate>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(showRate: ShowRate)
//
//    @Update
//    suspend fun update(showRate: ShowRate)
//
//    @Delete
//    suspend fun delete(showRate: ShowRate)
//
//}