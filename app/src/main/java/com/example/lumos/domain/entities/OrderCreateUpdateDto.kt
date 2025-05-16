package com.example.lumos.domain.entities

data class OrderCreateUpdateDto(
    val date: String,
    val location: String,
    val performance: Int,
    val amount: Double,
    val comment: String,
    val completed: Boolean
){
    companion object {
        fun fromOrder(order: Order): OrderCreateUpdateDto {
            return OrderCreateUpdateDto(
                date = order.date,
                location = order.location,
                performance = order.performance.id,
                amount = order.amount,
                comment = order.comment,
                completed = order.completed
            )
        }
    }
}