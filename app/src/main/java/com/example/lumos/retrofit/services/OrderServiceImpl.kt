package com.example.lumos.retrofit.services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateSerializer
import com.example.lumos.domain.services.OrderService
import com.example.lumos.retrofit.ApiClient

class OrderServiceImpl: OrderService {
    val service = ApiClient.getOrderService()
    override suspend fun getOrders(): List<Order> {
        lateinit var orders: List<Order>
        try {
            orders = service.getOrders()
            Log.d(TAG, "Received orders: $orders")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching orders", e)
        }
        return orders
    }

    override suspend fun getOrderById(id: Int): Order {
        lateinit var order: Order
        try {
            order = service.getOrderById(id)
            Log.d(TAG, "Received order: $order")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching order", e)
        }
        return order
    }

    override suspend fun createOrder(_order: OrderCreateUpdateSerializer): OrderCreateUpdateSerializer {
        lateinit var order: OrderCreateUpdateSerializer
        try {
            order = service.createOrder(_order)
            Log.d(TAG, "Created order: $order")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating order", e)
        }
        return order
    }

    override suspend fun updateOrder(
        id: Int,
        _order: OrderCreateUpdateSerializer
    ): OrderCreateUpdateSerializer {
        lateinit var order: OrderCreateUpdateSerializer
        try {
            order = service.updateOrder(id,_order)
            Log.d(TAG, "Updated order: $order")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order", e)
        }
        return order
    }

    override suspend fun deleteOrder(id: Int) {
        try {
            service.deleteOrder(id)
            Log.d(TAG, "Deleted order with: $id")
        } catch (e: Exception) {
            //Log.e(TAG, "Error deleting order", e)
        }
    }
}