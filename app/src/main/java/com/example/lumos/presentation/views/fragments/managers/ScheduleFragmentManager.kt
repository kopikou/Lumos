package com.example.lumos.presentation.views.fragments.managers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lumos.data.remote.impl.*
import com.example.lumos.data.repository.*
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.Performance
import com.example.lumos.databinding.FragmentScheduleManagersBinding
import com.example.lumos.domain.usecases.*
import com.example.lumos.presentation.adapters.OrderManagerAdapter
import com.example.lumos.presentation.dialogs.CreateOrderDialogFragment
import com.example.lumos.presentation.dialogs.DeleteOrderDialogFragment
import com.example.lumos.presentation.dialogs.EditOrderDialogFragment
import com.example.lumos.presentation.viewModels.managers.ScheduleManagerViewModel
import com.example.lumos.presentation.viewModels.managers.ScheduleManagerViewModelFactory
import kotlinx.coroutines.launch

class ScheduleFragmentManager : Fragment() {
    private var _binding: FragmentScheduleManagersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleManagerViewModel by viewModels {
        val orderRepository = OrderRepositoryImpl(OrderServiceImpl())
        val earningRepositoryImpl = EarningRepositoryImpl(EarningServiceImpl())
        val artistPerformanceRepository = ArtistPerformanceRepositoryImpl(ArtistPerformanceServiceImpl())
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())

        ScheduleManagerViewModelFactory(
            GetOrdersUseCase(orderRepository),
            CreateOrderUseCase(orderRepository, earningRepositoryImpl, artistPerformanceRepository),
            UpdateOrderUseCase(orderRepository, earningRepositoryImpl, artistPerformanceRepository, artistRepository),
            DeleteOrderUseCase(orderRepository)
        )
    }

    private lateinit var adapter: OrderManagerAdapter

    private val performanceRepositoryImpl = PerformanceRepositoryImpl(PerformanceServiceImpl())
    private val artistPerformanceRepository = ArtistPerformanceRepositoryImpl(ArtistPerformanceServiceImpl())
    private val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
    private val earningRepositoryImpl = EarningRepositoryImpl(EarningServiceImpl())

    private lateinit var performances: List<Performance>
    private lateinit var artistPerformances: List<ArtistPerformance>
    private lateinit var allArtists: List<Artist>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleManagersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        viewModel.loadOrders()

        binding.fabAddOrder.setOnClickListener {
            showCreateOrderDialog()
        }

        loadPerformancesAndArtists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = OrderManagerAdapter(
            onItemClick = { order -> showEditOrderDialog(order) },
            onDeleteClick = { order -> showDeleteConfirmationDialog(order) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orders.collect { orders ->
                    adapter.updateOrders(orders)
                }
            }
        }
    }

    private fun loadPerformancesAndArtists() {
        lifecycleScope.launch {
            try {
                performances = performanceRepositoryImpl.getPerformances()
                artistPerformances = artistPerformanceRepository.getArtistPerformances()
                allArtists = artistRepository.getArtists()
            } catch (e: Exception) {
                showToast("Ошибка загрузки данных")
            }
        }
    }

    private fun showCreateOrderDialog() {
        val dialog = CreateOrderDialogFragment().apply {
            setData(
                performances,
                artistPerformances.map { it.performance.id to it.artist.id },
                allArtists
            )
            onCreateOrderListener = { date, performanceId, location, amount, comment, artistIds ->
                viewModel.createOrder(
                    date = date,
                    performanceId = performanceId,
                    location = location,
                    amount = amount,
                    comment = comment,
                    artistIds = artistIds
                ) {
                    showToast("Заказ создан")
                }
            }
        }
        dialog.show(parentFragmentManager, "CreateOrderDialog")
    }

    private fun showEditOrderDialog(order: Order) {
        lifecycleScope.launch {
            try {
                val currentArtists = earningRepositoryImpl.getEarnings()
                    .filter { it.order.id == order.id }
                    .map { it.artist }

                val dialog = EditOrderDialogFragment().apply {
                    setData(
                        order,
                        performances,
                        artistPerformances.map { it.performance.id to it.artist.id },
                        allArtists,
                        currentArtists
                    )
                    onUpdateOrderListener = { orderId, date, performanceId, location, amount, comment, isCompleted, artistIds ->
                        viewModel.updateOrder(
                            orderId = orderId,
                            date = date,
                            performanceId = performanceId,
                            location = location,
                            amount = amount,
                            comment = comment,
                            isCompleted = isCompleted,
                            artistIds = artistIds
                        ) {
                            showToast("Заказ обновлен")
                        }
                    }
                }
                dialog.show(parentFragmentManager, "EditOrderDialog")
            } catch (e: Exception) {
                showToast("Ошибка загрузки данных артистов")
            }
        }
    }

    private fun showDeleteConfirmationDialog(order: Order) {
        val dialog = DeleteOrderDialogFragment().apply {
            setOrderId(order.id)
            onDeleteConfirmedListener = { orderId ->
                viewModel.deleteOrder(orderId) {
                    showToast("Заказ удален")
                }
            }
        }
        dialog.show(parentFragmentManager, "DeleteConfirmationDialog")
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = ScheduleFragmentManager()
    }
}