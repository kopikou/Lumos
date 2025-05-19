package com.example.lumos.presentation.viewModels

import androidx.lifecycle.LiveData
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

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _earningsMap = MutableLiveData<Map<Int, Earning>>()
    val earningsMap: LiveData<Map<Int, Earning>> = _earningsMap

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var artistId: Int = 0

    init {
        loadArtistId()
    }

    private fun loadArtistId() {
        viewModelScope.launch {
            try {
                val firstName = tokenManager.getFirstName() ?: return@launch
                val lastName = tokenManager.getLastName() ?: return@launch
                println("TokenManager data: firstName=${tokenManager.getFirstName()}, lastName=${tokenManager.getLastName()}")

                artistId = getArtistIdUseCase(firstName, lastName)
                loadCompletedOrders()
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки ID артиста"
            }
        }
    }

    fun loadCompletedOrders() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val (orders, earnings) = getCompletedOrdersUseCase(artistId)
                _orders.value = orders
                _earningsMap.value = earnings
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки истории заказов"
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun getOrderDetails(orderId: Int): OrderDetails {
        return getOrderDetailsUseCase(orderId, artistId)
    }
}