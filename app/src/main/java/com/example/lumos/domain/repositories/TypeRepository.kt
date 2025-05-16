package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.Type

interface TypeRepository {
    suspend fun getTypes(): List<Type>
    suspend fun getTypeById(id: Int): Type
    suspend fun createType(type: Type): Type
    suspend fun updateType(id: Int, type: Type): Type
    suspend fun deleteType(id: Int)
}