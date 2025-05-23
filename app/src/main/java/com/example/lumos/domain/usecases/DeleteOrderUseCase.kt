package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.OrderRepositoryImpl

class DeleteOrderUseCase(
    private val orderRepository: OrderRepositoryImpl
) {
    suspend operator fun invoke(orderId: Int): Boolean {
        return try {
            orderRepository.deleteOrder(orderId)
            true
        } catch (e: Exception) {
            false
        }
    }
}