package com.example.lumos.domain.entities

data class ArtistPerformanceCreateUpdateDto(
    val artist: Int,
    val performance: Int,
    val rate: Int
)