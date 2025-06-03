package com.example.lumos.data.remote.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateDto
import com.example.lumos.data.remote.api.OrderService
import com.example.lumos.data.remote.ApiClient

class OrderServiceImpl: OrderService {
    val service = ApiClient.getOrderService()
    override suspend fun getOrders(): List<Order> {
        lateinit var orders: List<Order>
        try {
            orders = service.getOrders()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки заказов", e)
        }
        return orders
    }

    override suspend fun getOrderById(id: Int): Order {
        lateinit var order: Order
        try {
            order = service.getOrderById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки заказа", e)
        }
        return order
    }

    override suspend fun createOrder(_order: OrderCreateUpdateDto): OrderCreateUpdateDto {
        lateinit var order: OrderCreateUpdateDto
        try {
            order = service.createOrder(_order)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания заказа", e)
        }
        return order
    }

    override suspend fun updateOrder(
        id: Int,
        _order: OrderCreateUpdateDto
    ): OrderCreateUpdateDto {
        lateinit var order: OrderCreateUpdateDto
        try {
            order = service.updateOrder(id,_order)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обновления заказа", e)
        }
        return order
    }

    override suspend fun deleteOrder(id: Int) {
        try {
            service.deleteOrder(id)
        } catch (e: Exception) {
            null
        }
    }
}