package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.domain.entities.Artist

class GetPerformanceArtistsUseCase(
    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl
) {
    suspend operator fun invoke(performanceId: Int): List<Artist> {
        val artistPerformances = artistPerformanceRepository.getArtistPerformances()
            .filter { it.performance.id == performanceId }

        return artistPerformances.map { ap ->
            artistRepository.getArtistById(ap.artist.id)
        }
    }
}