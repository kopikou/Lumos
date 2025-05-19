package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.domain.entities.Order

class GetOrdersUseCase(
    private val orderRepository: OrderRepositoryImpl
) {
    suspend operator fun invoke(): List<Order> {
        return orderRepository.getOrders()
    }
}