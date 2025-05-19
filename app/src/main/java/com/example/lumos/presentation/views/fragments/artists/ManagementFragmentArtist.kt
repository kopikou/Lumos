package com.example.lumos.presentation.views.fragments.artists
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.example.lumos.presentation.viewModels.ManagementArtistViewModel
import com.example.lumos.presentation.viewModels.ManagementArtistViewModelFactory
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//class ManagementFragmentArtist : Fragment() {
//    private lateinit var tokenManager: TokenManager
//    private lateinit var earningService: EarningServiceImpl
//    private lateinit var artistService: ArtistServiceImpl
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: HistoryOrderAdapter
//    private lateinit var emptyView: TextView
//    private var artistId: Int = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        tokenManager = TokenManager(requireContext())
//        earningService = EarningServiceImpl()
//        artistService = ArtistServiceImpl()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_management_artists, container, false)
//        recyclerView = view.findViewById(R.id.recyclerView)
//        emptyView = view.findViewById(R.id.emptyView)
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        loadCompletedOrders()
//    }
//
//    private fun setupRecyclerView() {
//        adapter = HistoryOrderAdapter { order ->
//            showOrderDetails(order)
//        }
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//    }
//
//    private fun loadCompletedOrders() {
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
//                val completedOrders = earnings
//                    .map { it.order }
//                    .filter { order ->
//                        val orderDate = parseDate(order.date)
//                        order.completed && !orderDate.after(today.time)
//                    }
//                    .sortedByDescending { it.date } // Сортируем по дате в обратном порядке
//
//                if (completedOrders.isEmpty()) {
//                    showEmptyView(true)
//                } else {
//                    showEmptyView(false)
//                    adapter.updateOrders(completedOrders,earnings)
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка загрузки истории заказов", Toast.LENGTH_SHORT).show()
//                showEmptyView(true)
//            }
//        }
//    }
//
//    private fun showOrderDetails(order: Order) {
//        lifecycleScope.launch {
//            try {
//                // Получаем информацию о выплате для этого заказа и артиста
//                val earning = earningService.getEarnings()
//                    .firstOrNull { it.order.id == order.id && it.artist.id == artistId }
//
//                val dialogView = LayoutInflater.from(requireContext())
//                    .inflate(R.layout.dialog_order_history_details, null)
//
//                // Заполняем основные данные
//                dialogView.findViewById<TextView>(R.id.tvDate).text = formatDate(order.date)
//                dialogView.findViewById<TextView>(R.id.tvPerformance).text = order.performance.title
//                dialogView.findViewById<TextView>(R.id.tvLocation).text = order.location
//                dialogView.findViewById<TextView>(R.id.tvComment).text = order.comment
//                dialogView.findViewById<TextView>(R.id.tvAmount).text = "%,.2f ₽".format(order.amount)
//
//                // Определяем статусы
//                val completionStatus = if (order.completed) "Выполнен" else "Не выполнен"
//                val paymentStatus = when {
//                    !order.completed -> "Не выплачивается"
//                    earning == null -> "Ошибка: запись о выплате не найдена"
//                    earning.paid -> "Зарплата выплачена (${earning.amount} ₽)"
//                    else -> "Ожидает выплаты (${earning.amount} ₽)"
//                }
//
//                // Устанавливаем статусы
//                dialogView.findViewById<TextView>(R.id.tvCompletionStatus).text = completionStatus
//                dialogView.findViewById<TextView>(R.id.tvPaymentStatus).text = paymentStatus
//
//                // Цвета статусов
//                val completionColor = if (order.completed) R.color.green else R.color.orange
//                val paymentColor = when {
//                    !order.completed -> R.color.orange
//                    earning?.paid == true -> R.color.green
//                    else -> R.color.yellow
//                }
//
//                dialogView.findViewById<TextView>(R.id.tvCompletionStatus).setTextColor(
//                    ContextCompat.getColor(requireContext(), completionColor))
//                dialogView.findViewById<TextView>(R.id.tvPaymentStatus).setTextColor(
//                    ContextCompat.getColor(requireContext(), paymentColor))
//
//                AlertDialog.Builder(requireContext())
//                    .setTitle("Детали заказа")
//                    .setView(dialogView)
//                    .setPositiveButton("Закрыть", null)
//                    .show()
//
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
//            }
//        }
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
//    private fun showEmptyView(show: Boolean) {
//        emptyView.visibility = if (show) View.VISIBLE else View.GONE
//        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
//    }
//
//    inner class HistoryOrderAdapter(
//        private val onItemClick: (Order) -> Unit
//    ) : RecyclerView.Adapter<HistoryOrderAdapter.OrderViewHolder>() {
//
//        private val orders = mutableListOf<Order>()
//        private val earningsMap = mutableMapOf<Int, Earning>() // orderId to Earning
//
//        fun updateOrders(newOrders: List<Order>, earnings: List<Earning>) {
//            orders.clear()
//            earningsMap.clear()
//            orders.addAll(newOrders)
//            earnings.forEach { earning ->
//                earningsMap[earning.order.id] = earning
//            }
//            notifyDataSetChanged()
//        }
//
//        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//            val order = orders[position]
//            holder.date.text = formatDateShort(order.date)
//            holder.performance.text = order.performance.title
//
//            // Получаем информацию о выплате
//            val earning = earningsMap[order.id]
//
//            // Устанавливаем иконку в зависимости от статусов
//            val iconRes = R.drawable.ic_check_circle
//
//            val iconColor = when {
//                !order.completed -> R.color.orange
//                earning?.paid == true -> R.color.green
//                else -> R.color.yellow
//            }
//
//            val drawable = AppCompatResources.getDrawable(requireContext(), iconRes)?.mutate()
//            drawable?.setTint(ContextCompat.getColor(requireContext(), iconColor))
//            holder.statusIcon.setImageDrawable(drawable)
//            holder.statusIcon.contentDescription = when {
//                !order.completed -> "Заказ не выполнен"
//                earning?.paid == true -> "Зарплата выплачена"
//                else -> "Ожидает выплаты"
//            }
//
//            holder.itemView.setOnClickListener {
//                onItemClick(order)
//            }
//        }
//
//        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val date: TextView = itemView.findViewById(R.id.orderDate)
//            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//            val statusIcon: ImageView = itemView.findViewById(R.id.ivStatus)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_order_history, parent, false)
//            return OrderViewHolder(view)
//        }
//
//        override fun getItemCount() = orders.size
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
        adapter = HistoryOrderAdapter { order ->
            showOrderDetails(order)
        }
        binding.recyclerView.adapter = adapter

        setupViewModel()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        val earningRepository = EarningRepositoryImpl(EarningServiceImpl())

        val getArtistIdUseCase = GetArtistIdUseCase(artistRepository)
        val getCompletedOrdersUseCase = GetCompletedOrdersUseCase(earningRepository, 0) // artistId будет установлен позже
        val getOrderDetailsUseCase = GetOrderDetailsUseCase(earningRepository)

        val factory = ManagementArtistViewModelFactory(
            getArtistIdUseCase,
            getCompletedOrdersUseCase,
            getOrderDetailsUseCase,
            tokenManager
        )

        viewModel = ViewModelProvider(this, factory)[ManagementArtistViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = HistoryOrderAdapter { order ->
            showOrderDetails(order)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders.isEmpty()) {
                showEmptyView(true)
            } else {
                showEmptyView(false)
                val earningsList = viewModel.earningsMap.value?.values?.toList() ?: emptyList()
                adapter.updateOrders(orders, earningsList)
                //adapter.updateOrders(orders, viewModel.earningsMap.value ?: emptyMap())
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

//        viewModel.loading.observe(viewLifecycleOwner) { loading ->
//            // Можно показать/скрыть ProgressBar
//        }
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

//    inner class HistoryOrderAdapter(
//        private val onItemClick: (Order) -> Unit
//    ) : RecyclerView.Adapter<HistoryOrderAdapter.OrderViewHolder>() {
//
//        private val orders = mutableListOf<Order>()
//        private val earningsMap = mutableMapOf<Int, Earning>() // orderId to Earning
//
////        fun updateOrders(newOrders: List<Order>, earnings: List<Earning>) {
////            orders.clear()
////            earningsMap.clear()
////            orders.addAll(newOrders)
////            earnings.forEach { earning ->
////                earningsMap[earning.order.id] = earning
////            }
////            notifyDataSetChanged()
////        }
//        fun updateOrders(newOrders: List<Order>, earnings: Map<Int, Earning>) {
//            orders.clear()
//            earningsMap.clear()
//            orders.addAll(newOrders)
//            earningsMap.putAll(earnings)
//            notifyDataSetChanged()
//        }
//
//        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//            val order = orders[position]
//            holder.date.text = DateFormatter.formatShort(order.date)
//            holder.performance.text = order.performance.title
//
//            // Получаем информацию о выплате
//            val earning = earningsMap[order.id]
//
//            // Устанавливаем иконку в зависимости от статусов
//            val iconRes = R.drawable.ic_check_circle
//
//            val iconColor = when {
//                !order.completed -> R.color.orange
//                earning?.paid == true -> R.color.green
//                else -> R.color.yellow
//            }
//
//            val drawable = AppCompatResources.getDrawable(requireContext(), iconRes)?.mutate()
//            drawable?.setTint(ContextCompat.getColor(requireContext(), iconColor))
//            holder.statusIcon.setImageDrawable(drawable)
//            holder.statusIcon.contentDescription = when {
//                !order.completed -> "Заказ не выполнен"
//                earning?.paid == true -> "Зарплата выплачена"
//                else -> "Ожидает выплаты"
//            }
//
//            holder.itemView.setOnClickListener {
//                onItemClick(order)
//            }
//        }
//
//        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val date: TextView = itemView.findViewById(R.id.orderDate)
//            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//            val statusIcon: ImageView = itemView.findViewById(R.id.ivStatus)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_order_history, parent, false)
//            return OrderViewHolder(view)
//        }
//
//        override fun getItemCount() = orders.size
//
////        private fun formatDateShort(dateString: String): String {
////            return try {
////                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
////                val outputFormat = SimpleDateFormat("dd MMM", Locale("ru"))
////                val date = inputFormat.parse(dateString)
////                outputFormat.format(date ?: Date())
////            } catch (e: Exception) {
////                dateString
////            }
////        }
//
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


}
//object DateFormatter {
//    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    private val outputFormatLong = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
//    private val outputFormatShort = SimpleDateFormat("dd MMM", Locale("ru"))
//
//    fun formatLong(dateString: String): String {
//        return try {
//            val date = inputFormat.parse(dateString)
//            outputFormatLong.format(date ?: Date())
//        } catch (e: Exception) {
//            dateString
//        }
//    }
//
//    fun formatShort(dateString: String): String {
//        return try {
//            val date = inputFormat.parse(dateString)
//            outputFormatShort.format(date ?: Date())
//        } catch (e: Exception) {
//            dateString
//        }
//    }
//}