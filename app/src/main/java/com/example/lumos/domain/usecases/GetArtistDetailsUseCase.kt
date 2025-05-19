package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.Performance

class GetArtistDetailsUseCase(
    private val artistRepository: ArtistRepositoryImpl,
    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl,
    private val performanceRepository: PerformanceRepositoryImpl
) {
    suspend operator fun invoke(artistId: Int): ArtistDetails {
        val artist = artistRepository.getArtistById(artistId)
        val artistPerformances = artistPerformanceRepository.getArtistPerformances()
            .filter { it.artist.id == artistId }

        val performances = artistPerformances.map { ap ->
            val performance = performanceRepository.getPerformanceById(ap.performance.id)
            PerformanceWithRate(performance, ap.rate.rate)
        }

        return ArtistDetails(artist, performances)
    }
}

data class ArtistDetails(
    val artist: Artist,
    val performances: List<PerformanceWithRate>
)

data class PerformanceWithRate(
    val performance: Performance,
    val rate: Double
)