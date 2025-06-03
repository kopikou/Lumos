package com.example.lumos.data.remote.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Performance
import com.example.lumos.domain.entities.PerformanceCreateUpdateDto
import com.example.lumos.data.remote.api.PerformanceService
import com.example.lumos.data.remote.ApiClient

class PerformanceServiceImpl: PerformanceService {
    val service = ApiClient.getPerformanceService()
    override suspend fun getPerformances(): List<Performance> {
        lateinit var performances: List<Performance>
        try {
            performances = service.getPerformances()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки номеров", e)
        }
        return performances
    }

    override suspend fun getPerformanceById(id: Int): Performance {
        lateinit var performance: Performance
        try {
            performance = service.getPerformanceById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки номера", e)
        }
        return performance
    }

    override suspend fun createPerformance(_performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto {
        lateinit var performance: PerformanceCreateUpdateDto
        try {
            performance = service.createPerformance(_performance)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания номера", e)
        }
        return performance
    }

    override suspend fun updatePerformance(id: Int, _performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto {
        lateinit var performance: PerformanceCreateUpdateDto
        try {
            performance = service.updatePerformance(id,_performance)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обновления номера", e)
        }
        return performance
    }

    override suspend fun deletePerformance(id: Int) {
        try {
            service.deletePerformance(id)
        } catch (e: Exception) {
            null
        }
    }
}