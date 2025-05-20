package com.example.lumos.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.Performance
import com.example.lumos.domain.usecases.CreateOrderUseCase
import com.example.lumos.domain.usecases.DeleteOrderUseCase
import com.example.lumos.domain.usecases.GetArtistPerformancesUseCase
import com.example.lumos.domain.usecases.GetArtistsForOrderUseCase
import com.example.lumos.domain.usecases.GetArtistsUseCase
import com.example.lumos.domain.usecases.GetOrdersUseCase
import com.example.lumos.domain.usecases.GetPerformancesUseCase
import com.example.lumos.domain.usecases.UpdateOrderUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//class ScheduleManagerViewModel(
//    private val getOrdersUseCase: GetOrdersUseCase,
//    private val createOrderUseCase: CreateOrderUseCase,
//    private val updateOrderUseCase: UpdateOrderUseCase,
//    private val deleteOrderUseCase: DeleteOrderUseCase,
//    private val getPerformancesUseCase: GetPerformancesUseCase,
//    private val getArtistsUseCase: GetArtistsUseCase,
//    private val getArtistPerformancesUseCase: GetArtistPerformancesUseCase,
//    private val getArtistsForOrderUseCase: GetArtistsForOrderUseCase
//) : ViewModel() {
//    // UI State
//    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
//    val uiState: StateFlow<ScheduleUiState> = _uiState
//
//    private val _performances = MutableStateFlow<List<Performance>>(emptyList())
//    val performances: StateFlow<List<Performance>> = _performances
//
//    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
//    val artists: StateFlow<List<Artist>> = _artists
//
//    private val _artistPerformances = MutableStateFlow<List<ArtistPerformance>>(emptyList())
//    val artistPerformances: StateFlow<List<ArtistPerformance>> = _artistPerformances
//
//    init {
//        loadData()
//    }
//
//    fun loadData() {
//        viewModelScope.launch {
//            _uiState.value = ScheduleUiState.Loading
//            try {
//                val ordersDeferred = async { getOrdersUseCase() }
//                val performancesDeferred = async { getPerformancesUseCase() }
//                val artistsDeferred = async { getArtistsUseCase() }
//                val artistPerformancesDeferred = async { getArtistPerformancesUseCase() }
//
//                _performances.value = performancesDeferred.await()
//                _artists.value = artistsDeferred.await()
//                _artistPerformances.value = artistPerformancesDeferred.await()
//
//                val orders = ordersDeferred.await()
//                _uiState.value = if (orders.isEmpty()) {
//                    ScheduleUiState.Empty
//                } else {
//                    ScheduleUiState.Success(orders)
//                }
//            } catch (e: Exception) {
//                _uiState.value = ScheduleUiState.Error(e.message ?: "Unknown error")
//            }
//        }
//    }
//
//    fun createOrder(
//        date: String,
//        location: String,
//        performance: Performance,
//        amount: Double,
//        comment: String,
//        selectedArtists: List<Artist>
//    ) {
//        viewModelScope.launch {
////            when (createOrderUseCase(
////                date, location, performance, amount, comment, selectedArtists
////            )) {
////                is Result.Success -> loadData()
////                is Result.Failure -> _uiState.value = ScheduleUiState.Error("Failed to create order")
////            }
//            createOrderUseCase(date, location, performance, amount, comment, selectedArtists)
//            loadData()
//        }
//    }
//
//    fun updateOrder(
//        orderId: Int,
//        date: String,
//        location: String,
//        performance: Performance,
//        amount: Double,
//        comment: String,
//        isCompleted: Boolean,
//        selectedArtists: List<Artist>
//    ) {
//        viewModelScope.launch {
////            when (updateOrderUseCase(
////                orderId, date, location, performance, amount, comment, isCompleted, selectedArtists
////            )) {
////                is Result.Success -> loadData()
////                is Result.Failure -> _uiState.value = ScheduleUiState.Error("Failed to update order")
////            }
//            updateOrderUseCase(
//                orderId, date, location, performance, amount, comment, isCompleted, selectedArtists
//            )
//            loadData()
//        }
//    }
//
//    fun deleteOrder(orderId: Int) {
//        viewModelScope.launch {
////            when (deleteOrderUseCase(orderId)) {
////                is Result.Success -> loadData()
////                is Result.Failure -> _uiState.value = ScheduleUiState.Error("Failed to delete order")
////            }
//            deleteOrderUseCase(orderId)
//            loadData()
//        }
//    }
//
//    suspend fun getArtistsForOrder(orderId: Int): List<Artist> {
//        return getArtistsForOrderUseCase(orderId)
//    }
//
//    sealed class ScheduleUiState {
//        object Loading : ScheduleUiState()
//        object Empty : ScheduleUiState()
//        data class Success(val orders: List<Order>) : ScheduleUiState()
//        data class Error(val message: String) : ScheduleUiState()
//    }
//}


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