package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.EarningCreateUpdateDto

interface EarningRepository {
    suspend fun getEarnings(): List<Earning>
    suspend fun getEarningById(id: Int): Earning
    suspend fun createEarning(earning: EarningCreateUpdateDto): EarningCreateUpdateDto
    suspend fun updateEarning(id: Int, earning: EarningCreateUpdateDto): EarningCreateUpdateDto
    suspend fun deleteEarning(id: Int)
}