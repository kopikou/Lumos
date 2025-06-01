package com.example.lumos.presentation.views.fragments.artists
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lumos.databinding.FragmentManagementArtistsBinding
import com.example.lumos.presentation.adapters.HistoryOrderAdapter
import com.example.lumos.presentation.dialogs.OrderHistoryDetailsDialogFragment
import com.example.lumos.presentation.viewModels.artists.ManagementArtistViewModel
import com.example.lumos.presentation.viewModels.artists.ManagementArtistViewModelFactory

class ManagementFragmentArtist : Fragment() {
    private var _binding: FragmentManagementArtistsBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: ManagementArtistViewModel
    private lateinit var adapter: HistoryOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagementArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ManagementArtistViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(ManagementArtistViewModel::class.java)
        adapter = HistoryOrderAdapter(requireContext()) { order ->
            showOrderDetails(order.id)
        }

        binding.recyclerView.apply {
            adapter = this@ManagementFragmentArtist.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders.isEmpty()) {
                showEmptyView(true)
            } else {
                showEmptyView(false)
                // Получаем текущую карту выплат
                val currentEarningsMap = viewModel.earningsMap.value ?: emptyMap()
                // Создаем список выплат в том же порядке, что и заказы
                val earningsList = orders.map { order ->
                    currentEarningsMap[order.id]
                }
                adapter.updateOrders(orders, earningsList.filterNotNull())
            }
        }

        viewModel.earningsMap.observe(viewLifecycleOwner) { earningsMap ->
            // Когда обновляется карта выплат, обновляем адаптер с текущими заказами
            val currentOrders = viewModel.orders.value ?: emptyList()
            if (currentOrders.isNotEmpty()) {
                val earningsList = currentOrders.map { order ->
                    earningsMap?.get(order.id)
                }
                adapter.updateOrders(currentOrders, earningsList.filterNotNull())
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e("OrdersError", it)
            }
        }
    }

    private fun showOrderDetails(orderId: Int) {
        OrderHistoryDetailsDialogFragment.newInstance(orderId)
            .show(childFragmentManager, "order_details_dialog")
    }

    private fun showEmptyView(show: Boolean) {
        binding.emptyView.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}