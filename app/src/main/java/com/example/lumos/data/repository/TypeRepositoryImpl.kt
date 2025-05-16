package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.TypeServiceImpl
import com.example.lumos.domain.entities.Type
import com.example.lumos.domain.repositories.TypeRepository

class TypeRepositoryImpl(
    private val typeService: TypeServiceImpl
): TypeRepository{
    override suspend fun getTypes(): List<Type> {
        return typeService.getTypes()
    }

    override suspend fun getTypeById(id: Int): Type {
        return typeService.getTypeById(id)
    }

    override suspend fun createType(type: Type): Type {
        return typeService.createType(type)
    }

    override suspend fun updateType(id: Int, type: Type): Type {
        return typeService.updateType(id, type)
    }

    override suspend fun deleteType(id: Int) {
        return typeService.deleteType(id)
    }
}