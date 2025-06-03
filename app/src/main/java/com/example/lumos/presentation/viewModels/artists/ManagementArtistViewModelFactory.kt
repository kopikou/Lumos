package com.example.lumos.presentation.viewModels.artists

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.domain.usecases.GetArtistIdUseCase
import com.example.lumos.domain.usecases.GetCompletedOrdersUseCase
import com.example.lumos.domain.usecases.GetOrderDetailsUseCase

class ManagementArtistViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManagementArtistViewModel::class.java)) {
            val tokenManager = TokenManager(context)
            val artistService = ArtistServiceImpl()
            val earningService = EarningServiceImpl()
            val artistRepository = ArtistRepositoryImpl(artistService)
            val earningRepository = EarningRepositoryImpl(earningService)

            val getArtistIdUseCase = GetArtistIdUseCase(artistRepository)
            val getCompletedOrdersUseCase = GetCompletedOrdersUseCase(earningRepository)
            val getOrderDetailsUseCase = GetOrderDetailsUseCase(earningRepository)

            return ManagementArtistViewModel(
                getArtistIdUseCase,
                getCompletedOrdersUseCase,
                getOrderDetailsUseCase,
                tokenManager
            ) as T
        }
        throw IllegalArgumentException("Неизвестная модель")
    }
}