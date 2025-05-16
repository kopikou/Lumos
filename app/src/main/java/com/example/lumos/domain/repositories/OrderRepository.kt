package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateDto

interface OrderRepository {
    suspend fun getOrders(): List<Order>
    suspend fun getOrderById(id: Int): Order
    suspend fun createOrder(order: OrderCreateUpdateDto): OrderCreateUpdateDto
    suspend fun updateOrder(id: Int, order: OrderCreateUpdateDto): OrderCreateUpdateDto
    suspend fun deleteOrder(id: Int)
}