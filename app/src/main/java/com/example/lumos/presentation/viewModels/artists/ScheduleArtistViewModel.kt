package com.example.lumos.presentation.viewModels.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.usecases.GetArtistOrdersUseCase
import com.example.lumos.domain.usecases.UpdateOrderStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleArtistViewModel(
    private val getArtistOrdersUseCase: GetArtistOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
        data class Success(val orders: List<Order>) : UiState()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val orders = getArtistOrdersUseCase()
                _uiState.value = if (orders.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(orders)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun updateOrderStatus(orderId: Int, isCompleted: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val success = updateOrderStatusUseCase(orderId, isCompleted)
                if (success) {
                    loadOrders()
                    onSuccess()
                } else {
                    _uiState.value = UiState.Error("Ошибка обновления статуса заказа")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Ошибка обновления статуса заказа")
            }
        }
    }
}
