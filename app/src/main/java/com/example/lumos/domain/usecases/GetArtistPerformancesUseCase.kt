package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.domain.entities.ArtistPerformance

class GetArtistPerformancesUseCase(
    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl
) {
    suspend operator fun invoke(): List<ArtistPerformance> {
        return artistPerformanceRepository.getArtistPerformances()
    }
}