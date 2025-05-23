package com.example.lumos.presentation.views.fragments.artists
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lumos.R
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.Order
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.databinding.FragmentManagementArtistsBinding
import com.example.lumos.domain.usecases.GetArtistIdUseCase
import com.example.lumos.domain.usecases.GetCompletedOrdersUseCase
import com.example.lumos.domain.usecases.GetOrderDetailsUseCase
import com.example.lumos.domain.usecases.OrderDetails
import com.example.lumos.presentation.adapters.DateFormatter
import com.example.lumos.presentation.adapters.HistoryOrderAdapter
import com.example.lumos.presentation.viewModels.artists.ManagementArtistViewModel
import com.example.lumos.presentation.viewModels.artists.ManagementArtistViewModelFactory
import kotlinx.coroutines.launch

class ManagementFragmentArtist : Fragment() {
    private lateinit var binding: FragmentManagementArtistsBinding
    private lateinit var viewModel: ManagementArtistViewModel
    private lateinit var adapter: HistoryOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManagementArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HistoryOrderAdapter(requireContext()) { order ->
            showOrderDetails(order)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupViewModel()
        setupObservers()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        val earningRepository = EarningRepositoryImpl(EarningServiceImpl())

        val getArtistIdUseCase = GetArtistIdUseCase(artistRepository)
        val getCompletedOrdersUseCase = GetCompletedOrdersUseCase(earningRepository)
        val getOrderDetailsUseCase = GetOrderDetailsUseCase(earningRepository)

        val factory = ManagementArtistViewModelFactory(
            getArtistIdUseCase,
            getCompletedOrdersUseCase,
            getOrderDetailsUseCase,
            tokenManager
        )

        viewModel = ViewModelProvider(this, factory)[ManagementArtistViewModel::class.java]
    }

    private fun setupObservers() {
        // Объединяем наблюдение за orders и earningsMap
        val combinedLiveData = MediatorLiveData<Pair<List<Order>, Map<Int, Earning>?>>().apply {
            var lastOrders: List<Order> = emptyList()
            var lastEarnings: Map<Int, Earning>? = null

            addSource(viewModel.orders) { orders ->
                lastOrders = orders
                value = Pair(lastOrders, lastEarnings)
            }

            addSource(viewModel.earningsMap) { earnings ->
                lastEarnings = earnings
                value = Pair(lastOrders, lastEarnings)
            }
        }

        combinedLiveData.observe(viewLifecycleOwner) { (orders, earningsMap) ->
            if (orders.isEmpty()) {
                showEmptyView(true)
            } else {
                showEmptyView(false)
                val earningsList = earningsMap?.values?.toList() ?: emptyList()
                println("Orders: $orders")
                println("EarningsMap: $earningsMap")
                adapter.updateOrders(orders, earningsList)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOrderDetails(order: Order) {
        lifecycleScope.launch {
            try {
                val details = viewModel.getOrderDetails(order.id)
                showOrderDetailsDialog(details)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOrderDetailsDialog(details: OrderDetails) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_order_history_details, null)

        //Заполняем основные данные
        val order = details.order
        val earning = details.earning
        dialogView.findViewById<TextView>(R.id.tvDate).text = DateFormatter.formatLong(order.date)
        dialogView.findViewById<TextView>(R.id.tvPerformance).text = order.performance.title
        dialogView.findViewById<TextView>(R.id.tvLocation).text = order.location
        dialogView.findViewById<TextView>(R.id.tvComment).text = order.comment
        dialogView.findViewById<TextView>(R.id.tvAmount).text = "%,.2f ₽".format(order.amount)

        // Определяем статусы
        val completionStatus = if (order.completed) "Выполнен" else "Не выполнен"
        val paymentStatus = when {
            !order.completed -> "Не выплачивается"
            earning == null -> "Ошибка: запись о выплате не найдена"
            earning.paid -> "Зарплата выплачена (${earning.amount} ₽)"
            else -> "Ожидает выплаты (${earning.amount} ₽)"
        }

        // Устанавливаем статусы
        dialogView.findViewById<TextView>(R.id.tvCompletionStatus).text = completionStatus
        dialogView.findViewById<TextView>(R.id.tvPaymentStatus).text = paymentStatus

        // Цвета статусов
        val completionColor = if (order.completed) R.color.green else R.color.orange
        val paymentColor = when {
            !order.completed -> R.color.orange
            earning?.paid == true -> R.color.green
            else -> R.color.yellow
        }

        dialogView.findViewById<TextView>(R.id.tvCompletionStatus).setTextColor(
            ContextCompat.getColor(requireContext(), completionColor))
        dialogView.findViewById<TextView>(R.id.tvPaymentStatus).setTextColor(
            ContextCompat.getColor(requireContext(), paymentColor))

        AlertDialog.Builder(requireContext())
            .setTitle("Детали заказа")
            .setView(dialogView)
            .setPositiveButton("Закрыть", null)
            .show()
    }

    private fun showEmptyView(show: Boolean) {
        binding.emptyView.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }
}