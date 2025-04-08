//package com.example.lumos.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
//import com.example.lumos.entities.Order
//
//@Dao
//interface OrderDao {
//    @Query("SELECT * FROM orders")
//    fun getAll(): List<Order>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(order: Order)
//
//    @Update
//    suspend fun update(order: Order)
//
//    @Delete
//    suspend fun delete(order: Order)
//
//}