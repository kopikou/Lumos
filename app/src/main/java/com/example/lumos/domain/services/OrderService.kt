package com.example.lumos.domain.services

import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateSerializer
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderService {
    @GET("api/orders/")
    suspend fun getOrders(): List<Order>

    @GET("api/orders/{id}/")
    suspend fun getOrderById(@Path("id") id: Int): Order

    @POST("api/orders/")
    suspend fun createOrder(@Body order: OrderCreateUpdateSerializer): OrderCreateUpdateSerializer

    @PUT("api/orders/{id}/")
    suspend fun updateOrder(@Path("id") id: Int, @Body order: OrderCreateUpdateSerializer): OrderCreateUpdateSerializer

    @DELETE("api/orders/{id}/")
    suspend fun deleteOrder(@Path("id") id: Int): Unit
}