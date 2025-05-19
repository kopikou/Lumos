package com.example.lumos.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.usecases.FinancialData
import com.example.lumos.domain.usecases.GetFinancialDataUseCase
import com.example.lumos.domain.usecases.LogoutUseCase
import kotlinx.coroutines.launch

class ProfileManagerViewModel(
    private val getFinancialDataUseCase: GetFinancialDataUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _financialData = MutableLiveData<FinancialData>()
    val financialData: LiveData<FinancialData> = _financialData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadFinancialData() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val financialData = getFinancialDataUseCase()
                _financialData.value = financialData//getFinancialDataUseCase()
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки финансовых данных"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        logoutUseCase()
    }
}