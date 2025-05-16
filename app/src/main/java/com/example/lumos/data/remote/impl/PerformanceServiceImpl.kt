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
            Log.d(TAG, "Received performances: $performances")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching performances", e)
        }
        return performances
    }

    override suspend fun getPerformanceById(id: Int): Performance {
        lateinit var performance: Performance
        try {
            performance = service.getPerformanceById(id)
            Log.d(TAG, "Received performance: $performance")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching performance", e)
        }
        return performance
    }

    override suspend fun createPerformance(_performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto {
        lateinit var performance: PerformanceCreateUpdateDto
        try {
            performance = service.createPerformance(_performance)
            Log.d(TAG, "Created performance: $performance")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating performance", e)
        }
        return performance
    }

    override suspend fun updatePerformance(id: Int, _performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto {
        lateinit var performance: PerformanceCreateUpdateDto
        try {
            performance = service.updatePerformance(id,_performance)
            Log.d(TAG, "Updated performance: $performance")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating performance", e)
        }
        return performance
    }

    override suspend fun deletePerformance(id: Int) {
        try {
            service.deletePerformance(id)
            Log.d(TAG, "Deleted performance with: $id")
        } catch (e: Exception) {
            //Log.e(TAG, "Error deleting performance", e)
        }
    }
}