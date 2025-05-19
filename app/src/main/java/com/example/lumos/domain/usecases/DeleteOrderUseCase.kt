package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl

class DeleteOrderUseCase(
    private val orderRepository: OrderRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl
) {
    suspend operator fun invoke(orderId: Int): Result<Unit> {
        return try {
            // Delete related earnings first
            //earningRepository.deleteEarningsByOrder(orderId)

            // Then delete the order
            orderRepository.deleteOrder(orderId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}