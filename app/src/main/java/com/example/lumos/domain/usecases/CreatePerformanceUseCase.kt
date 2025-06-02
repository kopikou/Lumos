package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.entities.PerformanceCreateUpdateDto

class CreatePerformanceUseCase(private val performanceRepository: PerformanceRepositoryImpl) {
    suspend operator fun invoke(performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto {
        return performanceRepository.createPerformance(performance)
    }
}