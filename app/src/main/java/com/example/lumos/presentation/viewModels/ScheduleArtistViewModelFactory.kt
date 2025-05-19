package com.example.lumos.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.domain.usecases.GetArtistOrdersUseCase
import com.example.lumos.domain.usecases.UpdateOrderStatusUseCase

class ScheduleArtistViewModelFactory(
    private val getArtistOrdersUseCase: GetArtistOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleArtistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleArtistViewModel(
                getArtistOrdersUseCase,
                updateOrderStatusUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}