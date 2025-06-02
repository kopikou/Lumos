package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.PerformanceRepositoryImpl

class DeletePerformanceUseCase(private val performanceRepository: PerformanceRepositoryImpl) {
    suspend operator fun invoke(performanceId: Int) {
        performanceRepository.deletePerformance(performanceId)
    }
}