package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.OrderServiceImpl
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateDto
import com.example.lumos.domain.repositories.OrderRepository

class OrderRepositoryImpl (
    private val orderService: OrderServiceImpl
) : OrderRepository {
    override suspend fun getOrders(): List<Order> {
        return orderService.getOrders()
    }

    override suspend fun getOrderById(id: Int): Order {
        return orderService.getOrderById(id)
    }

    override suspend fun createOrder(order: OrderCreateUpdateDto): OrderCreateUpdateDto {
        return orderService.createOrder(order)
    }

    override suspend fun updateOrder(id: Int, order: OrderCreateUpdateDto): OrderCreateUpdateDto {
        return orderService.updateOrder(id, order)
    }

    override suspend fun deleteOrder(id: Int) {
        orderService.deleteOrder(id)
    }

}