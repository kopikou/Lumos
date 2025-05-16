package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.EarningCreateUpdateDto
import com.example.lumos.domain.repositories.EarningRepository

class EarningRepositoryImpl (
    private val earningService: EarningServiceImpl
) : EarningRepository {
    override suspend fun getEarnings(): List<Earning> {
        return earningService.getEarnings()
    }

    override suspend fun getEarningById(id: Int): Earning {
        return earningService.getEarningById(id)
    }

    override suspend fun createEarning(earning: EarningCreateUpdateDto): EarningCreateUpdateDto {
        return earningService.createEarning(earning)
    }

    override suspend fun updateEarning(
        id: Int,
        earning: EarningCreateUpdateDto
    ): EarningCreateUpdateDto {
        return earningService.updateEarning(id, earning)
    }

    override suspend fun deleteEarning(id: Int) {
        earningService.deleteEarning(id)
    }

    suspend fun deleteEarningsByOrder(orderId: Int){
        earningService.deleteEarningsByOrder(orderId)
    }
}