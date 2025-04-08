//package com.example.lumos.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Transaction
//import androidx.room.Update
//import com.example.lumos.entities.Performance
//import com.example.lumos.entities.PerformanceWithOrders
//import com.example.lumos.entities.TypeWithRates
//
//@Dao
//interface PerformanceDao {
//    @Query("SELECT * FROM performances")
//    fun getAll(): List<Performance>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(performance: Performance): Long
//
//    @Update
//    suspend fun update(performance: Performance)
//
//    @Delete
//    suspend fun delete(performance: Performance)
//
//    @Transaction
//    @Query("SELECT * FROM performances WHERE id = :performanceId")
//    suspend fun getPerformanceWithOrders(performanceId: Int): List<PerformanceWithOrders>
//}