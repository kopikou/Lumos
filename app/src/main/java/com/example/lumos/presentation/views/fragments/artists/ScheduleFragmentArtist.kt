package com.example.lumos.presentation.views.fragments.artists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lumos.R
import com.example.lumos.databinding.FragmentScheduleArtistsBinding
import com.example.lumos.domain.entities.Order
import com.example.lumos.presentation.utils.DateFormatter
import com.example.lumos.presentation.adapters.OrderArtistsAdapter
import com.example.lumos.presentation.dialogs.OrderDetailsDialogFragment
import com.example.lumos.presentation.viewModels.artists.ScheduleArtistViewModel
import com.example.lumos.presentation.viewModels.artists.ScheduleArtistViewModelFactory
import kotlinx.coroutines.launch

class ScheduleFragmentArtist : Fragment() {

    private var _binding: FragmentScheduleArtistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ScheduleArtistViewModel
    private lateinit var adapter: OrderArtistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setupRecyclerView()
        setupObservers()
        viewModel.loadOrders()
    }

    private fun initViewModel() {
        val factory = ScheduleArtistViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ScheduleArtistViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = OrderArtistsAdapter(
            onItemClick = { showOrderDetails(it) },
            dateFormatter = DateFormatter
        )

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ScheduleFragmentArtist.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        ScheduleArtistViewModel.UiState.Loading -> showLoading()
                        ScheduleArtistViewModel.UiState.Empty -> showEmptyState()
                        is ScheduleArtistViewModel.UiState.Error -> showError(state.message)
                        is ScheduleArtistViewModel.UiState.Success -> showOrders(state.orders)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.recyclerView.isVisible = false
        binding.emptyView.isVisible = false
    }

    private fun showEmptyState() {
        binding.recyclerView.isVisible = false
        binding.emptyView.isVisible = true
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showOrders(orders: List<Order>) {
        binding.recyclerView.isVisible = true
        binding.emptyView.isVisible = false
        adapter.submitList(orders)
    }

    private fun showOrderDetails(order: Order) {
        val dialog = OrderDetailsDialogFragment.newInstance(order)
        dialog.setOnStatusChangedListener(object : OrderDetailsDialogFragment.OnStatusChangedListener {
            override fun onStatusChanged(orderId: Int, isCompleted: Boolean) {
                viewModel.updateOrderStatus(orderId, isCompleted) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.salary_credited),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        dialog.show(parentFragmentManager, "OrderDetailsDialog")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}