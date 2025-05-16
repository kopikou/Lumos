package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.Performance
import com.example.lumos.domain.entities.PerformanceCreateUpdateDto

interface PerformanceRepository {
    suspend fun getPerformances(): List<Performance>
    suspend fun getPerformanceById(id: Int): Performance
    suspend fun createPerformance(performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto
    suspend fun updatePerformance(id: Int, performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto
    suspend fun deletePerformance(id: Int)
}