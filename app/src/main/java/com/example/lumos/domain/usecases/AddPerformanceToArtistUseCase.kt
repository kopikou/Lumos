package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.domain.entities.ArtistPerformanceCreateUpdateDto

class AddPerformanceToArtistUseCase(
    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl
) {
    suspend operator fun invoke(dto: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto {
        return artistPerformanceRepository.createArtistPerformance(dto)
    }
}