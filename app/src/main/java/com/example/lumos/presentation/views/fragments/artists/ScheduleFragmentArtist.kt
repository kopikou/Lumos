package com.example.lumos.presentation.views.fragments.artists
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lumos.R
import com.example.lumos.domain.entities.Order
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.remote.impl.OrderServiceImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.databinding.FragmentScheduleArtistsBinding
import com.example.lumos.domain.usecases.GetArtistOrdersUseCase
import com.example.lumos.domain.usecases.UpdateOrderStatusUseCase
import com.example.lumos.presentation.adapters.OrderArtistsAdapter
import com.example.lumos.presentation.viewModels.artists.ScheduleArtistViewModel
import com.example.lumos.presentation.viewModels.artists.ScheduleArtistViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleFragmentArtist : Fragment() {
    private lateinit var viewModel: ScheduleArtistViewModel
    private lateinit var binding: FragmentScheduleArtistsBinding
    private lateinit var adapter: OrderArtistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        viewModel.loadOrders()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        val earningRepository = EarningRepositoryImpl(EarningServiceImpl())
        val orderRepository = OrderRepositoryImpl(OrderServiceImpl())

        val getArtistOrdersUseCase = GetArtistOrdersUseCase(
            artistRepository, earningRepository, tokenManager
        )
        val updateOrderStatusUseCase = UpdateOrderStatusUseCase(
            orderRepository, artistRepository, earningRepository
        )

        viewModel = ViewModelProvider(
            this,
            ScheduleArtistViewModelFactory(getArtistOrdersUseCase, updateOrderStatusUseCase)
        )[ScheduleArtistViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = OrderArtistsAdapter { order ->
            showOrderDetails(order)
        }
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
    private fun showOrderDetails(order: Order) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_order_details, null)

        dialogView.findViewById<TextView>(R.id.tvDate).text = formatDate(order.date)
        dialogView.findViewById<TextView>(R.id.tvPerformance).text = order.performance.title
        dialogView.findViewById<TextView>(R.id.tvLocation).text = order.location
        dialogView.findViewById<TextView>(R.id.tvComment).text = order.comment
        dialogView.findViewById<TextView>(R.id.tvAmount).text = "%,.2f ₽".format(order.amount)//.replace(',', ' ')

        val switchCompleted = dialogView.findViewById<SwitchCompat>(R.id.switchCompleted)
        switchCompleted.isChecked = order.completed


        AlertDialog.Builder(requireContext())
            .setTitle("Детали заказа")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                adapter.updateOrderStatus(order.id, switchCompleted.isChecked)
                viewModel.updateOrderStatus(
                    order.id,
                    switchCompleted.isChecked,
                    onSuccess = {
                        // Показываем Toast после успешного обновления
                        Toast.makeText(
                            requireContext(),
                            "Зарплата начислена",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}