package com.example.lumos.presentation.viewModels.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.domain.usecases.CreateOrderUseCase
import com.example.lumos.domain.usecases.DeleteOrderUseCase
import com.example.lumos.domain.usecases.GetOrdersUseCase
import com.example.lumos.domain.usecases.UpdateOrderUseCase

class ScheduleManagerViewModelFactory(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleManagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleManagerViewModel(
                getOrdersUseCase,
                createOrderUseCase,
                updateOrderUseCase,
                deleteOrderUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}