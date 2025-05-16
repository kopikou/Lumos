package com.example.lumos.data.remote.api

import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateDto
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
    suspend fun createOrder(@Body order: OrderCreateUpdateDto): OrderCreateUpdateDto

    @PUT("api/orders/{id}/")
    suspend fun updateOrder(@Path("id") id: Int, @Body order: OrderCreateUpdateDto): OrderCreateUpdateDto

    @DELETE("api/orders/{id}/")
    suspend fun deleteOrder(@Path("id") id: Int): Unit
}