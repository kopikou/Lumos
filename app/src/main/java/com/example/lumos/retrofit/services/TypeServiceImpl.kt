package com.example.lumos.retrofit.services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Type
import com.example.lumos.domain.services.TypeService
import com.example.lumos.retrofit.ApiClient

class TypeServiceImpl: TypeService {
    val service = ApiClient.getTypeService()
    override suspend fun getTypes(): List<Type> {
        lateinit var types: List<Type>
        try {
            types = service.getTypes()
            Log.d(TAG, "Received types: $types")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching types", e)
        }
        return types
    }

    override suspend fun getTypeById(id: Int): Type {
        lateinit var type: Type
        try {
            type = service.getTypeById(id)
            Log.d(TAG, "Received type: $type")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching type", e)
        }
        return type
    }

    override suspend fun createType(_type: Type): Type {
        lateinit var type: Type
        try {
            type = service.createType(_type)
            Log.d(TAG, "Created type: $type")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating type", e)
        }
        return type
    }

    override suspend fun updateType(id: Int, _type: Type): Type {
        lateinit var type: Type
        try {
            type = service.updateType(id,_type)
            Log.d(TAG, "Updated type: $type")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating type", e)
        }
        return type
    }

    override suspend fun deleteType(id: Int) {
        try {
            service.deleteType(id)
            Log.d(TAG, "Deleted type with: $id")
        } catch (e: Exception) {
            //Log.e(TAG, "Error deleting type", e)
        }
    }
}