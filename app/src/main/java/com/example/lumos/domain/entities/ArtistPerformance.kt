package com.example.lumos.domain.entities

data class ArtistPerformance(
    val artist: Artist,
    val performance: Performance,
    val rate: ShowRate
)

