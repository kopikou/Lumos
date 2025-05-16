package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.ShowRate
import com.example.lumos.domain.entities.ShowRateCreateUpdateDto

interface ShowRateRepository {
    suspend fun getShowRates(): List<ShowRate>
    suspend fun getShowRateById(id: Int): ShowRate
    suspend fun createShowRate(showRate: ShowRateCreateUpdateDto): ShowRateCreateUpdateDto
    suspend fun updateShowRate(id: Int, showRate: ShowRateCreateUpdateDto): ShowRateCreateUpdateDto
    suspend fun deleteShowRate(id: Int)
}