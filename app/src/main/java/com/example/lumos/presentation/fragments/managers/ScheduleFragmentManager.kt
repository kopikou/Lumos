package com.example.lumos.presentation.fragments.managers
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.EarningCreateUpdateSerializer
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateSerializer
import com.example.lumos.domain.entities.Performance
import com.example.lumos.retrofit.authentification.TokenManager
import com.example.lumos.retrofit.services.ArtistPerformanceServiceImpl
import com.example.lumos.retrofit.services.ArtistServiceImpl
import com.example.lumos.retrofit.services.EarningServiceImpl
import com.example.lumos.retrofit.services.OrderServiceImpl
import com.example.lumos.retrofit.services.PerformanceServiceImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ScheduleFragmentManager : Fragment() {
    private lateinit var tokenManager: TokenManager
    private lateinit var orderService: OrderServiceImpl
    private lateinit var earningService: EarningServiceImpl
    private lateinit var artistService: ArtistServiceImpl
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter
    private lateinit var emptyView: TextView
    private lateinit var performanceService: PerformanceServiceImpl
    private lateinit var artistPerformanceService: ArtistPerformanceServiceImpl
    private lateinit var performances: List<Performance>
    private lateinit var artistPerformances: List<ArtistPerformance>
    private lateinit var allArtists: List<Artist>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
        orderService = OrderServiceImpl()
        earningService = EarningServiceImpl()
        artistService = ArtistServiceImpl()
        performanceService = PerformanceServiceImpl()
        artistPerformanceService = ArtistPerformanceServiceImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_managers, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadOrders()

        view.findViewById<FloatingActionButton>(R.id.fabAddOrder).setOnClickListener {
            showCreateOrderDialog()
        }

        loadPerformancesAndArtists()
    }
    private fun loadPerformancesAndArtists() {
        lifecycleScope.launch {
            try {
                performances = performanceService.getPerformances()
                artistPerformances = artistPerformanceService.getArtistPerformances()
                allArtists = artistService.getArtists()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCreateOrderDialog() {
        if (performances.isEmpty()) {
            Toast.makeText(requireContext(), "Загрузка номеров...", Toast.LENGTH_SHORT).show()
            loadPerformancesAndArtists()
            return
        }

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_order, null)

        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
        val actvPerformance = dialogView.findViewById<AutoCompleteTextView>(R.id.actvPerformance)
        val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
        val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
        val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)

        // Настройка выбора даты
        etDate.setOnClickListener {
            showDatePicker { date ->
                etDate.setText(date)
            }
        }

        // Настройка выпадающего списка номеров
        val performanceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            performances.map { it.title }
        )
        actvPerformance.setAdapter(performanceAdapter)
        actvPerformance.threshold = 1
        actvPerformance.setOnItemClickListener { _, _, position, _ ->
            val selectedPerformance = performances[position]
            etAmount.setText(selectedPerformance.cost.toString())
            updateArtistsSelection(artistsContainer, selectedPerformance)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Новый заказ")
            .setView(dialogView)
            .setPositiveButton("Создать") { _, _ ->
                createNewOrder(
                    etDate.text.toString(),
                    actvPerformance.text.toString(),
                    etLocation.text.toString(),
                    etAmount.text.toString(),
                    etComment.text.toString(),
                    artistsContainer
                )
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun updateArtistsSelection(container: LinearLayout, performance: Performance) {
        container.removeAllViews()

        // Получаем артистов для этого номера
        val artistsForPerformance = artistPerformances
            .filter { it.performance.id == performance.id }
            .map { it.artist }

        // Создаем чекбоксы для выбора артистов
        artistsForPerformance.forEach { artist ->
            val checkBox = CheckBox(requireContext()).apply {
                text = "${artist.firstName} ${artist.lastName}"
                tag = artist.id
            }
            container.addView(checkBox)
        }

        // Проверяем, что выбрано нужное количество артистов
        if (artistsForPerformance.size < performance.cntArtists) {
            Toast.makeText(
                requireContext(),
                "Внимание: для этого номера требуется ${performance.cntArtists} артистов, а доступно только ${artistsForPerformance.size}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                // Форматируем дату без использования java.time
                val formattedDate = String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    day
                )
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun createNewOrder(
        date: String,
        performanceTitle: String,
        location: String,
        amountStr: String,
        comment: String,
        artistsContainer: LinearLayout
    ) {
        if (date.isEmpty() || performanceTitle.isEmpty() || location.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            return
        }

        val performance = performances.firstOrNull { it.title == performanceTitle }
        if (performance == null) {
            Toast.makeText(requireContext(), "Номер не найден", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = try {
            amountStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Некорректная сумма", Toast.LENGTH_SHORT).show()
            return
        }

        // Получаем выбранных артистов
        val selectedArtists = mutableListOf<Artist>()
        for (i in 0 until artistsContainer.childCount) {
            val view = artistsContainer.getChildAt(i)
            if (view is CheckBox && view.isChecked) {
                val artistId = view.tag as Int
                allArtists.firstOrNull { it.id == artistId }?.let {
                    selectedArtists.add(it)
                }
            }
        }

        // Проверяем количество артистов
        if (selectedArtists.size != performance.cntArtists) {
            Toast.makeText(
                requireContext(),
                "Для этого номера требуется выбрать ${performance.cntArtists} артистов",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Создаем заказ
                val newOrder = OrderCreateUpdateSerializer(
                    date = date,
                    location = location,
                    performance = performance.id,
                    amount = amount,
                    comment = comment,
                    completed = false
                )

                val createdOrder = orderService.createOrder(newOrder)
                val orders = orderService.getOrders()
                lateinit var addedOrder: Order
                for (order in orders){
                    if (order.date == createdOrder.date && order.location == createdOrder.location && order.amount == createdOrder.amount && order.comment == createdOrder.comment && order.performance.id == createdOrder.performance){
                        addedOrder = order
                    }
                }

                // Создаем записи о заработке для артистов
                selectedArtists.forEach { artist ->
                    // Находим ставку артиста для этого номера
                    val artistPerformance = artistPerformances.firstOrNull {
                        it.artist.id == artist.id && it.performance.id == performance.id
                    }

                    if (artistPerformance != null) {
                        val earning = EarningCreateUpdateSerializer(
                            order = addedOrder.id,
                            artist = artist.id,
                            amount = artistPerformance.rate.rate,
                            paid = false
                        )
                        earningService.createEarning(earning)
                    }
                }

                // Обновляем список заказов
                loadOrders()
                Toast.makeText(requireContext(), "Заказ создан", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка создания заказа", Toast.LENGTH_SHORT).show()
            }
        }
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
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val allOrders = orderService.getOrders()
                val currentAndFutureOrders = allOrders
                    .filter { order ->
                        val orderDate = parseDate(order.date)
                        !order.completed && !orderDate.before(today.time)
                    }
                    .sortedBy { it.date }

                if (currentAndFutureOrders.isEmpty()) {
                    showEmptyView(true)
                } else {
                    showEmptyView(false)
                    adapter.updateOrders(currentAndFutureOrders)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки заказов", Toast.LENGTH_SHORT).show()
                showEmptyView(true)
            }
        }
    }

    private fun showOrderDetails(order: Order) {
        lifecycleScope.launch {
            try {
                // Получаем всех артистов, связанных с этим заказом
                val earnings = earningService.getEarnings()
                    .filter { it.order.id == order.id }

                val artists = earnings.map { it.artist }

                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_order_details, null)

                // Заполняем основные данные
                dialogView.findViewById<TextView>(R.id.tvDate).text = formatDate(order.date)
                dialogView.findViewById<TextView>(R.id.tvPerformance).text = order.performance.title
                dialogView.findViewById<TextView>(R.id.tvLocation).text = order.location
                dialogView.findViewById<TextView>(R.id.tvComment).text = order.comment
                dialogView.findViewById<TextView>(R.id.tvAmount).text =
                    "%,.2f ₽".format(order.amount)

//                // Список артистов
//                val artistsList = dialogView.findViewById<TextView>(R.id.tvArtists)
//                artistsList.text = artists.joinToString("\n") {
//                    "${it.firstName} ${it.lastName}"
//                }
//
//                // Суммы к выплате
//                val paymentsList = dialogView.findViewById<TextView>(R.id.tvPayments)
//                paymentsList.text = earnings.joinToString("\n") {
//                    "${it.artist.firstName} ${it.artist.lastName}: ${it.amount} ₽"
//                }

                val switchCompleted = dialogView.findViewById<SwitchCompat>(R.id.switchCompleted)
                switchCompleted.isChecked = order.completed

                AlertDialog.Builder(requireContext())
                    .setTitle("Детали заказа")
                    .setView(dialogView)
                    .setPositiveButton("Сохранить") { _, _ ->
                        updateOrderStatus(order, switchCompleted.isChecked, earnings)
                    }
                    .setNegativeButton("Закрыть", null)
                    .show()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOrderStatus(order: Order, isCompleted: Boolean, earnings: List<Earning>) {
        lifecycleScope.launch {
            try {
                // 1. Обновляем статус заказа
                val updatedOrder = order.copy(completed = isCompleted)
                orderService.updateOrder(order.id, OrderCreateUpdateSerializer.fromOrder(updatedOrder))

                // 2. Если заказ выполнен, начисляем зарплату всем артистам
                if (isCompleted) {
                    calculateAndAddSalaries(earnings)
                }

                // 3. Обновляем UI
                adapter.updateOrderStatus(order.id, isCompleted)
                Toast.makeText(requireContext(), "Статус обновлен", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun calculateAndAddSalaries(earnings: List<Earning>) {
        try {
            for (earning in earnings) {
                if (!earning.paid) {
                    // Обновляем баланс артиста
                    val artist = earning.artist
                    val newBalance = artist.balance + earning.amount
                    val updatedArtist = artist.copy(balance = newBalance)
                    artistService.updateArtist(artist.id, updatedArtist)

                    // Помечаем заработок как выплаченный
//                    val updatedEarning = earning.copy(paid = true)
//                    earningService.updateEarning(
//                        earning.order.id,
//                        EarningCreateUpdateSerializer.fromEarning(updatedEarning)
//                    )
                }
            }
            Toast.makeText(requireContext(), "Зарплаты начислены", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка начисления зарплат", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (e: ParseException) {
            Date()
        }
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

    private fun showEmptyView(show: Boolean) {
        emptyView.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    inner class OrderAdapter(
        private val onItemClick: (Order) -> Unit
    ) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

        private val orders = mutableListOf<Order>()

        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val date: TextView = itemView.findViewById(R.id.orderDate)
            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
            //val statusIcon: ImageView = itemView.findViewById(R.id.ivStatus)
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

            // Иконка статуса
//            holder.statusIcon.setImageResource(
//                if (order.completed) R.drawable.ic_check_circle else R.drawable.ic_pending
//            )
//            holder.statusIcon.setColorFilter(
//                ContextCompat.getColor(holder.itemView.context,
//                    if (order.completed) R.color.green else R.color.orange),
//                PorterDuff.Mode.SRC_IN
//            )

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