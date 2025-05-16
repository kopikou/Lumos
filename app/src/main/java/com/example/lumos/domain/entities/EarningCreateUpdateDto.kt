package com.example.lumos.domain.entities

data class EarningCreateUpdateDto(
    val order: Int,
    val artist: Int,
    val amount: Double,
    val paid: Boolean
){
    companion object {
        fun fromEarning(earning: Earning): EarningCreateUpdateDto {
            return EarningCreateUpdateDto(
                order = earning.order.id,
                artist = earning.artist.id,
                amount = earning.amount,
                paid = earning.paid
            )
        }
    }
}