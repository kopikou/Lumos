package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl

class GetFinancialDataUseCase(
    private val orderRepository: OrderRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl
) {
    suspend operator fun invoke(): FinancialData {
        val orders = orderRepository.getOrders()
        val earnings = earningRepository.getEarnings()

        val completedOrders = orders.filter { it.completed }
        val completedOrderIds = completedOrders.map { it.id }

        val totalIncome = completedOrders.sumOf { it.amount }
        val totalExpenses = earnings
            .filter { it.order.id in completedOrderIds }
            .sumOf { it.amount }

        return FinancialData(
            income = totalIncome,
            expenses = totalExpenses,
            profit = totalIncome - totalExpenses
        )
    }
}

data class FinancialData(
    val income: Double,
    val expenses: Double,
    val profit: Double
)