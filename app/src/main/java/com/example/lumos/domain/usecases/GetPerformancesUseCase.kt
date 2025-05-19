package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.entities.Performance

class GetPerformancesUseCase(
    private val performanceRepository: PerformanceRepositoryImpl
) {
    suspend operator fun invoke(): List<Performance> {
        return performanceRepository.getPerformances()
    }
}