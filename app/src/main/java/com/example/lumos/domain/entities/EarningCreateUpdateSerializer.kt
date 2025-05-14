package com.example.lumos.domain.entities

data class EarningCreateUpdateSerializer(
    val order: Int,
    val artist: Int,
    val amount: Double,
    val paid: Boolean
){
    companion object {
        fun fromEarning(earning: Earning): EarningCreateUpdateSerializer {
            return EarningCreateUpdateSerializer(
                order = earning.order.id,
                artist = earning.artist.id,
                amount = earning.amount,
                paid = earning.paid
            )
        }
    }
}
