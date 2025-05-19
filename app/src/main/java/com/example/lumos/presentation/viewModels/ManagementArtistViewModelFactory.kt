package com.example.lumos.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.domain.usecases.GetArtistIdUseCase
import com.example.lumos.domain.usecases.GetCompletedOrdersUseCase
import com.example.lumos.domain.usecases.GetOrderDetailsUseCase

class ManagementArtistViewModelFactory(
    private val getArtistIdUseCase: GetArtistIdUseCase,
    private val getCompletedOrdersUseCase: GetCompletedOrdersUseCase,
    private val getOrderDetailsUseCase: GetOrderDetailsUseCase,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManagementArtistViewModel::class.java)) {
            return ManagementArtistViewModel(
                getArtistIdUseCase,
                getCompletedOrdersUseCase,
                getOrderDetailsUseCase,
                tokenManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}