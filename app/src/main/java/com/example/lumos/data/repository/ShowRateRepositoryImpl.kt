package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.ShowRateServiceImpl
import com.example.lumos.domain.entities.ShowRate
import com.example.lumos.domain.entities.ShowRateCreateUpdateDto
import com.example.lumos.domain.repositories.ShowRateRepository

class ShowRateRepositoryImpl (
    private val showRateService: ShowRateServiceImpl
) : ShowRateRepository {
    override suspend fun getShowRates(): List<ShowRate> {
        return  showRateService.getShowRates()
    }

    override suspend fun getShowRateById(id: Int): ShowRate {
        return  showRateService.getShowRateById(id)
    }

    override suspend fun createShowRate(showRate: ShowRateCreateUpdateDto): ShowRateCreateUpdateDto {
        return  showRateService.createShowRate(showRate)
    }

    override suspend fun updateShowRate(
        id: Int,
        showRate: ShowRateCreateUpdateDto
    ): ShowRateCreateUpdateDto {
        return  showRateService.updateShowRate(id, showRate)
    }

    override suspend fun deleteShowRate(id: Int) {
        showRateService.deleteShowRate(id)
    }
}