package com.example.lumos.presentation.views.fragments.managers

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R
import com.example.lumos.data.remote.api.EarningService
import com.example.lumos.data.remote.api.OrderService
import com.example.lumos.presentation.views.activities.LoginActivity
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.remote.impl.OrderServiceImpl
import com.example.lumos.data.remote.impl.UserServiceImpl
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class ProfileFragmentManager : Fragment() {
    private lateinit var tokenManager: TokenManager
    private lateinit var artistService: ArtistServiceImpl
    private lateinit var userService: UserServiceImpl
    private lateinit var orderService: OrderService
    private lateinit var earningService: EarningService
    private var userId: Int = 0

    // Views
    private lateinit var tvIncome: TextView
    private lateinit var tvExpenses: TextView
    private lateinit var tvProfit: TextView
    private lateinit var btnLogout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
        artistService = ArtistServiceImpl()
        userService = UserServiceImpl()
        orderService = OrderServiceImpl()
        earningService = EarningServiceImpl()
        userId = tokenManager.getUserId()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_managers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация views
        tvIncome = view.findViewById(R.id.tvIncome)
        tvExpenses = view.findViewById(R.id.tvExpenses)
        tvProfit = view.findViewById(R.id.tvProfit)
        btnLogout = view.findViewById(R.id.btnLogout)

        // Загрузка данных
        loadFinancialData()

        // Обработчик кнопки выхода
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun loadFinancialData() {
        lifecycleScope.launch {
            try {
                val orders = orderService.getOrders()
                val earnings = earningService.getEarnings()

                // Фильтруем завершенные заказы
                val completedOrders = orders.filter { it.completed }
                val completedOrderIds = completedOrders.map { it.id }

                // Рассчитываем показатели
                val totalIncome = completedOrders.sumOf { it.amount }
                val totalExpenses = earnings
                    .filter { it.order.id in completedOrderIds }
                    .sumOf { it.amount }
                val netProfit = totalIncome - totalExpenses

                // Форматируем числа
                val formatter = DecimalFormat("#,###.00 ₽")

                // Обновляем UI
                tvIncome.text = formatter.format(totalIncome)
                tvExpenses.text = formatter.format(totalExpenses)
                tvProfit.text = formatter.format(netProfit)

            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error loading financial data", e)
                tvIncome.text = "Ошибка"
                tvExpenses.text = "Ошибка"
                tvProfit.text = "Ошибка"
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти из профиля?")
            .setPositiveButton("Да") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun performLogout() {
        tokenManager.clearTokens()
        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }
}