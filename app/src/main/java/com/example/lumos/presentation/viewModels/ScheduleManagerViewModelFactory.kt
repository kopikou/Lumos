package com.example.lumos.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.domain.usecases.CreateOrderUseCase
import com.example.lumos.domain.usecases.DeleteOrderUseCase
import com.example.lumos.domain.usecases.GetArtistPerformancesUseCase
import com.example.lumos.domain.usecases.GetArtistsForOrderUseCase
import com.example.lumos.domain.usecases.GetArtistsUseCase
import com.example.lumos.domain.usecases.GetOrdersUseCase
import com.example.lumos.domain.usecases.GetPerformancesUseCase
import com.example.lumos.domain.usecases.UpdateOrderUseCase

class ScheduleManagerViewModelFactory(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase,
    private val getPerformancesUseCase: GetPerformancesUseCase,
    private val getArtistsUseCase: GetArtistsUseCase,
    private val getArtistPerformancesUseCase: GetArtistPerformancesUseCase,
    private val getArtistsForOrderUseCase: GetArtistsForOrderUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleManagerViewModel::class.java)) {
            return ScheduleManagerViewModel(
                getOrdersUseCase,
                createOrderUseCase,
                updateOrderUseCase,
                deleteOrderUseCase,
                getPerformancesUseCase,
                getArtistsUseCase,
                getArtistPerformancesUseCase,
                getArtistsForOrderUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}