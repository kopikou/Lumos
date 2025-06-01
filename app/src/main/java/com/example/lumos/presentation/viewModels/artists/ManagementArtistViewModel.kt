package com.example.lumos.presentation.viewModels.artists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.usecases.GetArtistIdUseCase
import com.example.lumos.domain.usecases.GetCompletedOrdersUseCase
import com.example.lumos.domain.usecases.GetOrderDetailsUseCase
import com.example.lumos.domain.usecases.OrderDetails
import kotlinx.coroutines.launch

class ManagementArtistViewModel(
    private val getArtistIdUseCase: GetArtistIdUseCase,
    private val getCompletedOrdersUseCase: GetCompletedOrdersUseCase,
    private val getOrderDetailsUseCase: GetOrderDetailsUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    val orders = MutableLiveData<List<Order>>()
    val earningsMap = MutableLiveData<Map<Int, Earning>>()
    val error = MutableLiveData<String?>()

    private var artistId: Int = 0

    init {
        loadArtistId()
    }

    private fun loadArtistId() {
        viewModelScope.launch {
            try {
                val firstName = tokenManager.getFirstName() ?: return@launch
                val lastName = tokenManager.getLastName() ?: return@launch
                artistId = getArtistIdUseCase(firstName, lastName)
                loadCompletedOrders()
            } catch (e: Exception) {
                error.value = "Ошибка загрузки ID артиста"
            }
        }
    }

    fun loadCompletedOrders() {
        viewModelScope.launch {
            try {
                val (ordersList, earnings) = getCompletedOrdersUseCase(artistId)
                orders.value = ordersList
                earningsMap.value = earnings
                error.value = null
            } catch (e: Exception) {
                error.value = "Ошибка загрузки истории заказов"
            }
        }
    }

    suspend fun getOrderDetails(orderId: Int): OrderDetails {
        return getOrderDetailsUseCase(orderId, artistId)
    }
}