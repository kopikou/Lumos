package com.example.lumos.presentation.fragments.artists
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateSerializer
import com.example.lumos.retrofit.authentification.TokenManager
import com.example.lumos.retrofit.services.ArtistServiceImpl
import com.example.lumos.retrofit.services.EarningServiceImpl
import com.example.lumos.retrofit.services.OrderServiceImpl
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

//class ScheduleFragmentArtist : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_schedule_artists, container, false)
//    }
//}
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
//        orderService = OrderServiceImpl()
//        //tokenManager.getArtistId()
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
//        adapter = OrderAdapter(emptyList())
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//    }
//
//    private fun loadOrders() {
//        lifecycleScope.launch {
//            artistId = artistService.getArtistByName(tokenManager.getFirstName(),tokenManager.getLastName()).id
//            try {
//                // Получаем все earnings для текущего артиста
//                val earnings = earningService.getEarnings()
//                    .filter { it.artist.id == artistId }
//
//                // Получаем заказы из earnings и фильтруем по дате
//                val currentAndFutureOrders = earnings
//                    .map { it.order }
//                    .filter { order ->
//                        val orderDate = LocalDate.parse(order.date)
//                        val today = LocalDate.now()
//                        !order.completed && !orderDate.isBefore(today)
//                    }
//                    .sortedBy { it.date }
//
//                if (currentAndFutureOrders.isEmpty()) {
//                    showEmptyView(true)
//                } else {
//                    showEmptyView(false)
//                    adapter.updateOrders(currentAndFutureOrders)
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка загрузки заказов", Toast.LENGTH_SHORT).show()
//                showEmptyView(true)
//            }
//        }
//    }
//
//    private fun showEmptyView(show: Boolean) {
//        emptyView.visibility = if (show) View.VISIBLE else View.GONE
//        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
//    }
//
//    inner class OrderAdapter(private var orders: List<Order>) :
//        RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
//
//        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val date: TextView = itemView.findViewById(R.id.orderDate)
//            val location: TextView = itemView.findViewById(R.id.orderLocation)
//            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//            val amount: TextView = itemView.findViewById(R.id.orderAmount)
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
//            holder.date.text = formatDate(order.date)
//            holder.location.text = order.location
//            holder.performance.text = order.performance.title
//            holder.amount.text = "%,.2f ₽".format(order.amount).replace(',', ' ')
//        }
//
//        override fun getItemCount() = orders.size
//
//        fun updateOrders(newOrders: List<Order>) {
//            orders = newOrders
//            notifyDataSetChanged()
//        }
//
//        private fun formatDate(dateString: String): String {
//            return try {
//                val inputFormat = DateTimeFormatter.ISO_LOCAL_DATE
//                val outputFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))
//                LocalDate.parse(dateString, inputFormat).format(outputFormat)
//            } catch (e: Exception) {
//                dateString
//            }
//        }
//    }
//}

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
//        adapter = OrderAdapter(emptyList())
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//    }
//
//    private fun loadOrders() {
//        lifecycleScope.launch {
//            try {
//                // Получаем ID артиста по имени
//                artistId = artistService.getArtistByName(
//                    tokenManager.getFirstName(),
//                    tokenManager.getLastName()
//                ).id
//
//                // Получаем все earnings для текущего артиста
//                val earnings = earningService.getEarnings()
//                    .filter { it.artist.id == artistId }
//
//                // Получаем текущую дату
//                val today = Calendar.getInstance()
//                today.set(Calendar.HOUR_OF_DAY, 0)
//                today.set(Calendar.MINUTE, 0)
//                today.set(Calendar.SECOND, 0)
//                today.set(Calendar.MILLISECOND, 0)
//
//                // Фильтруем заказы
//                val currentAndFutureOrders = earnings
//                    .map { it.order }
//                    .filter { order ->
//                        val orderDate = parseDate(order.date)
//                        !order.completed && !orderDate.before(today.time)
//                    }
//                    .sortedBy { it.date }
//
//                if (currentAndFutureOrders.isEmpty()) {
//                    showEmptyView(true)
//                } else {
//                    showEmptyView(false)
//                    adapter.updateOrders(currentAndFutureOrders)
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка загрузки заказов", Toast.LENGTH_SHORT).show()
//                showEmptyView(true)
//            }
//        }
//    }
class ScheduleFragmentArtist : Fragment() {
    private lateinit var tokenManager: TokenManager
    private lateinit var earningService: EarningServiceImpl
    private lateinit var artistService: ArtistServiceImpl
    private lateinit var orderService: OrderServiceImpl
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter
    private lateinit var emptyView: TextView
    private var artistId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
        earningService = EarningServiceImpl()
        artistService = ArtistServiceImpl()
        orderService = OrderServiceImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_artists, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter { order ->
            showOrderDetails(order)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            try {
                artistId = artistService.getArtistByName(
                    tokenManager.getFirstName(),
                    tokenManager.getLastName()
                ).id

                val earnings = earningService.getEarnings()
                    .filter { it.artist.id == artistId }

                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val orders = earnings
                    .map { it.order }
                    .filter { order ->
                        val orderDate = parseDate(order.date)
                        !order.completed && !orderDate.before(today.time)
                    }
                    .sortedBy { it.date }

                if (orders.isEmpty()) {
                    showEmptyView(true)
                } else {
                    showEmptyView(false)
                    adapter.updateOrders(orders)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки заказов", Toast.LENGTH_SHORT).show()
                showEmptyView(true)
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
                updateOrderStatus(order, switchCompleted.isChecked)
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    private fun updateOrderStatus(order: Order, isCompleted: Boolean) {
        lifecycleScope.launch {
            try {
                val updatedOrder = order.copy(completed = isCompleted)
                orderService.updateOrder(order.id, OrderCreateUpdateSerializer.fromOrder(updatedOrder))
                adapter.updateOrderStatus(order.id, isCompleted)
                Toast.makeText(requireContext(), "Статус обновлен", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (e: ParseException) {
            Date()
        }
    }

    private fun showEmptyView(show: Boolean) {
        emptyView.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
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

//    inner class OrderAdapter(private var orders: List<Order>) :
//        RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
//
//        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val date: TextView = itemView.findViewById(R.id.orderDate)
//            val location: TextView = itemView.findViewById(R.id.orderLocation)
//            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//            val amount: TextView = itemView.findViewById(R.id.orderAmount)
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
//            holder.date.text = formatDate(order.date)
//            holder.location.text = order.location
//            holder.performance.text = order.performance.title
//            holder.amount.text = "%,.2f ₽".format(order.amount)//.replace(',', ' ')
//        }
//
//        override fun getItemCount() = orders.size
//
//        fun updateOrders(newOrders: List<Order>) {
//            orders = newOrders
//            notifyDataSetChanged()
//        }
//
//        private fun formatDate(dateString: String): String {
//            return try {
//                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
//                val date = inputFormat.parse(dateString)
//                outputFormat.format(date ?: Date())
//            } catch (e: Exception) {
//                dateString
//            }
//        }
//    }
    inner class OrderAdapter(
        private val onItemClick: (Order) -> Unit
    ) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

        private val orders = mutableListOf<Order>()

        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val date: TextView = itemView.findViewById(R.id.orderDate)
            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            holder.date.text = formatDateShort(order.date)
            holder.performance.text = order.performance.title

            holder.itemView.setOnClickListener {
                onItemClick(order)
            }
        }

        override fun getItemCount() = orders.size

        fun updateOrders(newOrders: List<Order>) {
            orders.clear()
            orders.addAll(newOrders)
            notifyDataSetChanged()
        }

        fun updateOrderStatus(orderId: Int, isCompleted: Boolean) {
            orders.find { it.id == orderId }?.completed = isCompleted
            notifyDataSetChanged()
        }

        private fun formatDateShort(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM", Locale("ru"))
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString
            }
        }
    }
}