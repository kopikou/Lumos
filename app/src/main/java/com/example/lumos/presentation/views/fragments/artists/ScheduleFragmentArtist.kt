package com.example.lumos.presentation.views.fragments.artists
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.example.lumos.presentation.viewModels.ScheduleArtistViewModel
import com.example.lumos.presentation.viewModels.ScheduleArtistViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//class ScheduleFragmentArtist : Fragment() {
//    private lateinit var tokenManager: TokenManager
//    private lateinit var earningService: EarningServiceImpl
//    private lateinit var artistService: ArtistServiceImpl
//    private lateinit var orderService: OrderServiceImpl
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: OrderAdapter
//    private lateinit var emptyView: TextView
//    private var artistId: Int = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        tokenManager = TokenManager(requireContext())
//        earningService = EarningServiceImpl()
//        artistService = ArtistServiceImpl()
//        orderService = OrderServiceImpl()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_schedule_artists, container, false)
//        recyclerView = view.findViewById(R.id.recyclerView)
//        emptyView = view.findViewById(R.id.emptyView)
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        loadOrders()
//    }
//
//    private fun setupRecyclerView() {
//        adapter = OrderAdapter { order ->
//            showOrderDetails(order)
//        }
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//    }
//
//    private fun loadOrders() {
//        lifecycleScope.launch {
//            try {
//                artistId = artistService.getArtistByName(
//                    tokenManager.getFirstName(),
//                    tokenManager.getLastName()
//                ).id
//
//                val earnings = earningService.getEarnings()
//                    .filter { it.artist.id == artistId }
//
//                val today = Calendar.getInstance().apply {
//                    set(Calendar.HOUR_OF_DAY, 0)
//                    set(Calendar.MINUTE, 0)
//                    set(Calendar.SECOND, 0)
//                    set(Calendar.MILLISECOND, 0)
//                }
//
//                val orders = earnings
//                    .map { it.order }
//                    .filter { order ->
//                        val orderDate = parseDate(order.date)
//                        !order.completed && !orderDate.before(today.time)
//                    }
//                    .sortedBy { it.date }
//
//                if (orders.isEmpty()) {
//                    showEmptyView(true)
//                } else {
//                    showEmptyView(false)
//                    adapter.updateOrders(orders)
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка загрузки заказов", Toast.LENGTH_SHORT).show()
//                showEmptyView(true)
//            }
//        }
//    }
//
//    private fun showOrderDetails(order: Order) {
//        val dialogView = LayoutInflater.from(requireContext())
//            .inflate(R.layout.dialog_order_details, null)
//
//        dialogView.findViewById<TextView>(R.id.tvDate).text = formatDate(order.date)
//        dialogView.findViewById<TextView>(R.id.tvPerformance).text = order.performance.title
//        dialogView.findViewById<TextView>(R.id.tvLocation).text = order.location
//        dialogView.findViewById<TextView>(R.id.tvComment).text = order.comment
//        dialogView.findViewById<TextView>(R.id.tvAmount).text = "%,.2f ₽".format(order.amount)//.replace(',', ' ')
//
//        val switchCompleted = dialogView.findViewById<SwitchCompat>(R.id.switchCompleted)
//        switchCompleted.isChecked = order.completed
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Детали заказа")
//            .setView(dialogView)
//            .setPositiveButton("Сохранить") { _, _ ->
//                updateOrderStatus(order, switchCompleted.isChecked)
//            }
//            .setNegativeButton("Закрыть", null)
//            .show()
//    }
//
//    private fun updateOrderStatus(order: Order, isCompleted: Boolean) {
//        lifecycleScope.launch {
//            try {
//                // 1. Обновляем статус заказа
//                val updatedOrder = order.copy(completed = isCompleted)
//                orderService.updateOrder(order.id, OrderCreateUpdateDto.fromOrder(updatedOrder))
//
//                // 2. Если заказ выполнен, начисляем зарплату
//                if (isCompleted) {
//                    calculateAndAddSalary(order)
//                }
//
//                // 3. Обновляем UI
//                adapter.updateOrderStatus(order.id, isCompleted)
//                Toast.makeText(requireContext(), "Статус обновлен", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private suspend fun calculateAndAddSalary(order: Order) {
//        try {
//            // 1. Получаем текущего артиста
//            val artist = artistService.getArtistByName(
//                tokenManager.getFirstName(),
//                tokenManager.getLastName()
//            )
//
//            // 2. Получаем информацию о заработке для этого заказа
//            val earning = earningService.getEarnings()
//                .firstOrNull { it.order.id == order.id && it.artist.id == artist.id }
//
//            earning?.let {
//                // 3. Если зарплата еще не выплачена
//                if (!it.paid) {
//                    // 4. Обновляем баланс артиста
//                    val newBalance = artist.balance + it.amount
//                    val updatedArtist = artist.copy(balance = newBalance)
//                    artistService.updateArtist(artist.id, updatedArtist)
//
//                    updateBalanceInUI(newBalance)
//
//                    Toast.makeText(
//                        requireContext(),
//                        "Зарплата ${it.amount} ₽ начислена",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        } catch (e: Exception) {
//            Toast.makeText(
//                requireContext(),
//                "Ошибка начисления зарплаты",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    private fun updateBalanceInUI(newBalance: Double) {
//        view?.findViewById<TextView>(R.id.tvBalance)?.text =
//            "%,.2f ₽".format(newBalance)//.replace(',', ' ')
//    }
//
//    private fun parseDate(dateString: String): Date {
//        return try {
//            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
//        } catch (e: ParseException) {
//            Date()
//        }
//    }
//
//    private fun showEmptyView(show: Boolean) {
//        emptyView.visibility = if (show) View.VISIBLE else View.GONE
//        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
//    }
//    private fun formatDate(dateString: String): String {
//        return try {
//            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
//            val date = inputFormat.parse(dateString)
//            outputFormat.format(date ?: Date())
//        } catch (e: Exception) {
//            dateString
//        }
//    }
//
//    inner class OrderAdapter(
//        private val onItemClick: (Order) -> Unit
//    ) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
//
//        private val orders = mutableListOf<Order>()
//
//        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val date: TextView = itemView.findViewById(R.id.orderDate)
//            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_order, parent, false)
//            return OrderViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//            val order = orders[position]
//            holder.date.text = formatDateShort(order.date)
//            holder.performance.text = order.performance.title
//
//            holder.itemView.setOnClickListener {
//                onItemClick(order)
//            }
//        }
//
//        override fun getItemCount() = orders.size
//
//        fun updateOrders(newOrders: List<Order>) {
//            orders.clear()
//            orders.addAll(newOrders)
//            notifyDataSetChanged()
//        }
//
//        fun updateOrderStatus(orderId: Int, isCompleted: Boolean) {
//            orders.find { it.id == orderId }?.completed = isCompleted
//            notifyDataSetChanged()
//        }
//
//        private fun formatDateShort(dateString: String): String {
//            return try {
//                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val outputFormat = SimpleDateFormat("dd MMM", Locale("ru"))
//                val date = inputFormat.parse(dateString)
//                outputFormat.format(date ?: Date())
//            } catch (e: Exception) {
//                dateString
//            }
//        }
//    }
//}

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
            orderRepository, artistRepository, earningRepository, tokenManager
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

//    private fun setupObservers() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.uiState.collect { state ->
//                    when (state) {
//                        is ScheduleArtistViewModel.UiState.Loading -> {
//                            //binding.progressBar.visibility = View.VISIBLE
//                            binding.emptyView.visibility = View.GONE
//                            binding.recyclerView.visibility = View.GONE
//                        }
//                        is ScheduleArtistViewModel.UiState.Empty -> {
//                            //binding.progressBar.visibility = View.GONE
//                            binding.emptyView.visibility = View.VISIBLE
//                            binding.recyclerView.visibility = View.GONE
//                        }
//                        is ScheduleArtistViewModel.UiState.Success -> {
//                            //binding.progressBar.visibility = View.GONE
//                            binding.emptyView.visibility = View.GONE
//                            binding.recyclerView.visibility = View.VISIBLE
//                        }
//                        is ScheduleArtistViewModel.UiState.Error -> {
//                            //binding.progressBar.visibility = View.GONE
//                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//        }
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.orders.collect { orders ->
//                    adapter.updateOrders(orders)
//                }
//            }
//        }
//    }


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