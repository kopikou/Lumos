package com.example.lumos.domain.entities

data class ArtistPerformanceCreateUpdateSerializer(
    val artist: Int,
    val performance: Int,
    val rate: Int
)
