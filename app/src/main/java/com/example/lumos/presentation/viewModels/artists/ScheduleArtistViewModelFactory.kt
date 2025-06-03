package com.example.lumos.presentation.viewModels.artists
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.remote.impl.OrderServiceImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.domain.usecases.GetArtistOrdersUseCase
import com.example.lumos.domain.usecases.UpdateOrderStatusUseCase

class ScheduleArtistViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleArtistViewModel::class.java)) {
            return ScheduleArtistViewModel(
                getArtistOrdersUseCase = createGetArtistOrdersUseCase(context),
                updateOrderStatusUseCase = createUpdateOrderStatusUseCase()
            ) as T
        }
        throw IllegalArgumentException("Неизвестная модель")
    }

    private fun createGetArtistOrdersUseCase(context: Context): GetArtistOrdersUseCase {
        val tokenManager = TokenManager(context)
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        val earningRepository = EarningRepositoryImpl(EarningServiceImpl())
        return GetArtistOrdersUseCase(artistRepository, earningRepository, tokenManager)
    }

    private fun createUpdateOrderStatusUseCase(): UpdateOrderStatusUseCase {
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        val earningRepository = EarningRepositoryImpl(EarningServiceImpl())
        val orderRepository = OrderRepositoryImpl(OrderServiceImpl())
        return UpdateOrderStatusUseCase(orderRepository, artistRepository, earningRepository)
    }
}