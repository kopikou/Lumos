//package com.example.lumos.dao
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Transaction
//import androidx.room.Update
//import com.example.lumos.entities.Type
//import com.example.lumos.entities.TypeWithPerformances
//import com.example.lumos.entities.TypeWithRates
//
//@Dao
//interface TypeDao {
//    @Query("SELECT * FROM type")
//    fun getAll(): List<Type>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(type: Type)
//
//    @Update
//    suspend fun update(type: Type)
//
//    @Delete
//    suspend fun delete(type: Type)
//
//    @Transaction
//    @Query("SELECT * FROM type WHERE id = :showTypeId")
//    suspend fun getTypeWithRates(showTypeId: Int): List<TypeWithRates>
//
//    @Transaction
//    @Query("SELECT * FROM type WHERE id = :type")
//    suspend fun getTypeWithPerformaces(type: Int): List<TypeWithPerformances>
//}