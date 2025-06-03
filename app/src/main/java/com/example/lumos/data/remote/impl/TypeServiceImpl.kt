package com.example.lumos.data.remote.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Type
import com.example.lumos.data.remote.api.TypeService
import com.example.lumos.data.remote.ApiClient

class TypeServiceImpl: TypeService {
    val service = ApiClient.getTypeService()
    override suspend fun getTypes(): List<Type> {
        lateinit var types: List<Type>
        try {
            types = service.getTypes()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки типов", e)
        }
        return types
    }

    override suspend fun getTypeById(id: Int): Type {
        lateinit var type: Type
        try {
            type = service.getTypeById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки типа", e)
        }
        return type
    }

    override suspend fun createType(_type: Type): Type {
        lateinit var type: Type
        try {
            type = service.createType(_type)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания типа", e)
        }
        return type
    }

    override suspend fun updateType(id: Int, _type: Type): Type {
        lateinit var type: Type
        try {
            type = service.updateType(id,_type)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обновления типа", e)
        }
        return type
    }

    override suspend fun deleteType(id: Int) {
        try {
            service.deleteType(id)
        } catch (e: Exception) {
            null
        }
    }
}