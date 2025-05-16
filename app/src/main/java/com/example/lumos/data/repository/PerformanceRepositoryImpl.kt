package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.PerformanceServiceImpl
import com.example.lumos.domain.entities.Performance
import com.example.lumos.domain.entities.PerformanceCreateUpdateDto
import com.example.lumos.domain.repositories.PerformanceRepository

class PerformanceRepositoryImpl (
    private val performanceService: PerformanceServiceImpl
) : PerformanceRepository {
    override suspend fun getPerformances(): List<Performance> {
        return performanceService.getPerformances()
    }

    override suspend fun getPerformanceById(id: Int): Performance {
        return performanceService.getPerformanceById(id)
    }

    override suspend fun createPerformance(performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto {
        return performanceService.createPerformance(performance)
    }

    override suspend fun updatePerformance(
        id: Int,
        performance: PerformanceCreateUpdateDto
    ): PerformanceCreateUpdateDto {
        return performanceService.updatePerformance(id, performance)
    }

    override suspend fun deletePerformance(id: Int) {
        performanceService.deletePerformance(id)
    }

}