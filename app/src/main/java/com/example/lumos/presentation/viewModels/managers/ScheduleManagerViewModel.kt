package com.example.lumos.presentation.viewModels.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.usecases.CreateOrderUseCase
import com.example.lumos.domain.usecases.DeleteOrderUseCase
import com.example.lumos.domain.usecases.GetOrdersUseCase
import com.example.lumos.domain.usecases.UpdateOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleManagerViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Success(val orders: List<Order>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val orders = getOrdersUseCase()
                _orders.value = orders
                _uiState.value = if (orders.isEmpty()) UiState.Empty else UiState.Success(orders)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка загрузки заказов")
            }
        }
    }

    fun createOrder(
        date: String,
        performanceId: Int,
        location: String,
        amount: Double,
        comment: String,
        artistIds: List<Int>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val success = createOrderUseCase(date, performanceId, location, amount, comment, artistIds)
            if (success) {
                loadOrders()
                onSuccess()
            } else {
                _uiState.value = UiState.Error("Ошибка создания заказа")
            }
        }
    }

    fun updateOrder(
        orderId: Int,
        date: String,
        performanceId: Int,
        location: String,
        amount: Double,
        comment: String,
        isCompleted: Boolean,
        artistIds: List<Int>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val success = updateOrderUseCase(
                orderId, date, performanceId, location, amount, comment, isCompleted, artistIds
            )
            if (success) {
                loadOrders()
                onSuccess()
            } else {
                _uiState.value = UiState.Error("Ошибка обновления заказа")
            }
        }
    }

    fun deleteOrder(orderId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val success = deleteOrderUseCase(orderId)
            if (success) {
                loadOrders()
                onSuccess()
            } else {
                _uiState.value = UiState.Error("Ошибка удаления заказа")
            }
        }
    }
}