package com.example.lumos.presentation.fragments.managers
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
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
import com.google.android.material.textfield.TextInputLayout
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
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_order, null)

        val tilDate = dialogView.findViewById<TextInputLayout>(R.id.tilDate)
        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
        val tilPerformance = dialogView.findViewById<TextInputLayout>(R.id.tilPerformance)
        val spinnerPerformance = dialogView.findViewById<Spinner>(R.id.spinnerPerformance)
        val tilLocation = dialogView.findViewById<TextInputLayout>(R.id.tilLocation)
        val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
        val tilAmount = dialogView.findViewById<TextInputLayout>(R.id.tilAmount)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
        val tilComment = dialogView.findViewById<TextInputLayout>(R.id.tilComment)
        val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
        val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)

        // Настройка выбора даты
        etDate.setOnClickListener {
            showDatePicker { date ->
                etDate.setText(date)
            }
        }

        // Настройка Spinner с номерами
        val performanceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Выберите номер") + performances.map { it.title }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerPerformance.adapter = performanceAdapter

        // Обработчик выбора номера
        spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val selectedPerformance = performances[position - 1] // -1 из-за подсказки
                    etAmount.setText(selectedPerformance.cost.toString())
                    updateArtistsSelection(artistsContainer, selectedPerformance)
                } else {
                    etAmount.text?.clear()
                    artistsContainer.removeAllViews()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Новый заказ")
            .setView(dialogView)
            .setPositiveButton("Создать", null) // Обработчик будет установлен позже
            .setNegativeButton("Отмена", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                var isValid = true

                // Проверка выбора номера
                if (spinnerPerformance.selectedItemPosition == 0) {
                    (spinnerPerformance.selectedView as? TextView)?.error = "Выберите номер"
                    isValid = false
                }

                // Проверка даты
                if (etDate.text.isNullOrEmpty()) {
                    tilDate.error = "Укажите дату"
                    isValid = false
                }

                // Проверка места
                if (etLocation.text.isNullOrEmpty()) {
                    tilLocation.error = "Укажите место"
                    isValid = false
                }

                // Проверка суммы
                val amount = try {
                    etAmount.text.toString().toDouble()
                } catch (e: NumberFormatException) {
                    tilAmount.error = "Некорректная сумма"
                    isValid = false
                    0.0
                }

                // Проверка комментария
                if (etComment.text.isNullOrEmpty()) {
                    tilComment.error = "Укажите комментарий"
                    isValid = false
                }

                // Проверка артистов
                val selectedArtists = getSelectedArtists(artistsContainer)
                if (selectedArtists.size != performances[spinnerPerformance.selectedItemPosition-1].cntArtists) {
                    Toast.makeText(requireContext(),
                        "Для этого номера требуется ${performances[spinnerPerformance.selectedItemPosition-1].cntArtists} артистов",
                        Toast.LENGTH_LONG).show()
                    isValid = false
                }

                if (isValid) {
                    createNewOrder(
                        etDate.text.toString(),
                        performances[spinnerPerformance.selectedItemPosition-1].title,
                        etLocation.text.toString(),
                        amount.toString(),
                        etComment.text.toString(),
                        artistsContainer
                    )
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun getSelectedArtists(container: LinearLayout): List<Artist> {
        val selectedArtists = mutableListOf<Artist>()
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is CheckBox && view.isChecked) {
                val artistId = view.tag as Int
                allArtists.firstOrNull { it.id == artistId }?.let {
                    selectedArtists.add(it)
                }
            }
        }
        return selectedArtists
    }

    private fun updateArtistsSelection(container: LinearLayout, performance: Performance, selectedArtists: List<Artist> = emptyList()) {
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
                isChecked = selectedArtists.any { it.id == artist.id }
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
        val performance = performances.first { it.title == performanceTitle }
        val amount = amountStr.toDouble()
        val selectedArtists = getSelectedArtists(artistsContainer)

        lifecycleScope.launch {
            try {
                // Создаем новый заказ
                val newOrder = OrderCreateUpdateSerializer(
                    date = date,
                    location = location,
                    performance = performance.id,
                    amount = amount,
                    comment = comment,
                    completed = false
                )

                val createdOrder = orderService.createOrder(newOrder)

                // Находим только что созданный заказ в списке
                val orders = orderService.getOrders()
                lateinit var addedOrder: Order
                for (order in orders) {
                    if (order.date == createdOrder.date &&
                        order.location == createdOrder.location &&
                        order.amount == createdOrder.amount &&
                        order.comment == createdOrder.comment &&
                        order.performance.id == createdOrder.performance) {
                        addedOrder = order
                    }
                }

                // Создаем записи о заработке для выбранных артистов
                selectedArtists.forEach { artist ->
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

                loadOrders()
                Toast.makeText(requireContext(), "Заказ создан", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка создания заказа", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(
            onItemClick = { order ->
                showOrderDetails(order)
            },
            onDeleteClick = { order ->
                showDeleteConfirmationDialog(order)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(order: Order) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление заказа")
            .setMessage("Вы уверены, что хотите удалить этот заказ?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteOrder(order)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteOrder(order: Order) {
        lifecycleScope.launch {
            try {
                // Удаляем сам заказ (связанные записи о заработке удалятся каскадно)
                orderService.deleteOrder(order.id)
                loadOrders()
                Toast.makeText(requireContext(), "Заказ удален", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка удаления заказа", Toast.LENGTH_SHORT).show()
            }
        }
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
                // Получаем текущих артистов, связанных с этим заказом
                val earnings = earningService.getEarnings()
                    .filter { it.order.id == order.id }

                val currentArtists = earnings.map { it.artist }

                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_edit_order_details, null)

                // Получаем ссылки на элементы
                val tilDate = dialogView.findViewById<TextInputLayout>(R.id.tilDate)
                val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
                val tilPerformance = dialogView.findViewById<TextInputLayout>(R.id.tilPerformance)
                val spinnerPerformance = dialogView.findViewById<Spinner>(R.id.spinnerPerformance)
                val tilLocation = dialogView.findViewById<TextInputLayout>(R.id.tilLocation)
                val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
                val tilAmount = dialogView.findViewById<TextInputLayout>(R.id.tilAmount)
                val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
                val tilComment = dialogView.findViewById<TextInputLayout>(R.id.tilComment)
                val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
                val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)
                val switchCompleted = dialogView.findViewById<SwitchCompat>(R.id.switchCompleted)

                // Заполняем текущие данные
                etDate.setText(order.date)
                etLocation.setText(order.location)
                etAmount.setText(order.amount.toString())
                etComment.setText(order.comment)
                switchCompleted.isChecked = order.completed

                // Настройка выбора даты
                etDate.setOnClickListener {
                    showDatePicker { date ->
                        etDate.setText(date)
                    }
                }

                // Настройка Spinner с номерами
                val performanceAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listOf("Выберите номер") + performances.map { it.title }
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                spinnerPerformance.adapter = performanceAdapter

                // Выбираем текущий номер
                val currentPerformanceIndex = performances.indexOfFirst { it.id == order.performance.id }
                if (currentPerformanceIndex >= 0) {
                    spinnerPerformance.setSelection(currentPerformanceIndex + 1) // +1 из-за подсказки
                }

                // Обработчик выбора номера
                spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (position > 0) {
                            val selectedPerformance = performances[position - 1]
                            etAmount.setText(selectedPerformance.cost.toString())
                            updateArtistsSelection(artistsContainer, selectedPerformance, currentArtists)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                // Инициализация списка артистов для текущего номера
                updateArtistsSelection(artistsContainer, order.performance, currentArtists)

                // Валидация в реальном времени
                setupValidation(tilDate, etDate, tilLocation, etLocation, tilAmount, etAmount, tilComment, etComment)

                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Редактирование заказа")
                    .setView(dialogView)
                    .setPositiveButton("Сохранить") { _, _ ->
                        updateOrderStatus(order, switchCompleted.isChecked, artistsContainer)
                    }
                    .setNegativeButton("Отмена", null)
                    .create()

                dialog.setOnShowListener {
                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setOnClickListener {
                        if (validateInputs(
                                tilDate, etDate,
                                tilLocation, etLocation,
                                tilAmount, etAmount,
                                spinnerPerformance,
                                artistsContainer,
                                tilComment,
                                etComment
                            )) {
                            updateOrder(
                                order,
                                etDate.text.toString(),
                                spinnerPerformance.selectedItemPosition,
                                etLocation.text.toString(),
                                etAmount.text.toString(),
                                etComment.text.toString(),
                                switchCompleted.isChecked,
                                artistsContainer
                            )
                            dialog.dismiss()
                        }
                    }
                }

                dialog.show()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupValidation(
        tilDate: TextInputLayout, etDate: TextInputEditText,
        tilLocation: TextInputLayout, etLocation: TextInputEditText,
        tilAmount: TextInputLayout, etAmount: TextInputEditText,
        tilComment: TextInputLayout, etComment: TextInputEditText
    ) {
        etDate.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && etDate.text.isNullOrEmpty()) {
                tilDate.error = "Укажите дату"
            } else {
                tilDate.error = null
            }
        }

        etLocation.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && etLocation.text.isNullOrEmpty()) {
                tilLocation.error = "Укажите место"
            } else {
                tilLocation.error = null
            }
        }

        etAmount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                try {
                    etAmount.text.toString().toDouble()
                    tilAmount.error = null
                } catch (e: NumberFormatException) {
                    tilAmount.error = "Некорректная сумма"
                }
            }
        }

        etComment.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && etComment.text.isNullOrEmpty()) {
                tilComment.error = "Укажите комментарий"
            } else {
                tilComment.error = null
            }
        }
    }

    private fun validateInputs(
        tilDate: TextInputLayout, etDate: TextInputEditText,
        tilLocation: TextInputLayout, etLocation: TextInputEditText,
        tilAmount: TextInputLayout, etAmount: TextInputEditText,
        spinnerPerformance: Spinner,
        artistsContainer: LinearLayout,
        tilComment: TextInputLayout, etComment: TextInputEditText
    ): Boolean {
        var isValid = true

        // Проверка даты
        if (etDate.text.isNullOrEmpty()) {
            tilDate.error = "Укажите дату"
            isValid = false
        }

        // Проверка места
        if (etLocation.text.isNullOrEmpty()) {
            tilLocation.error = "Укажите место"
            isValid = false
        }

        // Проверка суммы
        try {
            etAmount.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            tilAmount.error = "Некорректная сумма"
            isValid = false
        }

        // Проверка выбора номера
        if (spinnerPerformance.selectedItemPosition == 0) {
            (spinnerPerformance.selectedView as? TextView)?.error = "Выберите номер"
            isValid = false
        } else {
            // Проверка количества артистов
            val selectedPerformance = performances[spinnerPerformance.selectedItemPosition - 1]
            val selectedArtistsCount = getSelectedArtists(artistsContainer).size

            if (selectedArtistsCount != selectedPerformance.cntArtists) {
                Toast.makeText(
                    requireContext(),
                    "Для этого номера требуется ${selectedPerformance.cntArtists} артистов",
                    Toast.LENGTH_LONG
                ).show()
                isValid = false
            }
        }

        // Проверка комментария
        if (etComment.text.isNullOrEmpty()) {
            tilComment.error = "Укажите комментарий"
            isValid = false
        }

        return isValid
    }

    private fun updateOrder(
        originalOrder: Order,
        date: String,
        performancePosition: Int,
        location: String,
        amountStr: String,
        comment: String,
        isCompleted: Boolean,
        artistsContainer: LinearLayout
    ) {
        // Проверяем, что выбран номер (не подсказка)
        if (performancePosition == 0) {
            Toast.makeText(requireContext(), "Выберите номер", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPerformance = performances[performancePosition - 1] // -1 из-за подсказки
        val amount = amountStr.toDouble()
        val selectedArtists = getSelectedArtists(artistsContainer)

        // Проверка количества артистов
        if (selectedArtists.size != selectedPerformance.cntArtists) {
            Toast.makeText(
                requireContext(),
                "Для этого номера требуется ${selectedPerformance.cntArtists} артистов",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        lifecycleScope.launch {
            try {
                // 1. Обновляем сам заказ
                val updatedOrder = originalOrder.copy(
                    date = date,
                    location = location,
                    performance = selectedPerformance,
                    amount = amount,
                    comment = comment,
                    completed = isCompleted
                )

                orderService.updateOrder(originalOrder.id, OrderCreateUpdateSerializer.fromOrder(updatedOrder))

                // 2. Получаем текущие записи о заработке
                val currentEarnings = earningService.getEarnings()
                    .filter { it.order.id == originalOrder.id }
                println(currentEarnings)

                // 3. Определяем, изменился ли номер или список артистов
                val performanceChanged = originalOrder.performance.id != selectedPerformance.id
                val artistsChanged = currentEarnings.map { it.artist.id }.sorted() !=
                        selectedArtists.map { it.id }.sorted()

                // 4. Если номер или артисты изменились - удаляем старые записи и создаем новые
                if (performanceChanged || artistsChanged) {
                    // Удаляем старые записи о заработке
                    currentEarnings.forEach { earning ->
                        earningService.deleteEarningsByOrder(originalOrder.id)
                    }

                    // Создаем новые записи о заработке для выбранных артистов
                    selectedArtists.forEach { artist ->
                        val artistPerformance = artistPerformances.firstOrNull {
                            it.artist.id == artist.id && it.performance.id == selectedPerformance.id
                        }

                        artistPerformance?.let {
                            val earning = EarningCreateUpdateSerializer(
                                order = originalOrder.id,
                                artist = artist.id,
                                amount = it.rate.rate,
                                paid = false
                            )
                            earningService.createEarning(earning)
                        }
                    }
                }

                // 5. Если заказ выполнен, начисляем зарплату
                if (isCompleted) {
                    calculateAndAddSalaries(updatedOrder, selectedArtists)
                }

                // 6. Обновляем список заказов
                loadOrders()
                Toast.makeText(requireContext(), "Заказ обновлен", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка обновления заказа", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOrderStatus(order: Order, isCompleted: Boolean, artistsContainer: LinearLayout) {
        lifecycleScope.launch {
            try {
                // 1. Обновляем статус заказа
                val updatedOrder = order.copy(completed = isCompleted)
                orderService.updateOrder(order.id, OrderCreateUpdateSerializer.fromOrder(updatedOrder))

                val selectedArtists = getSelectedArtists(artistsContainer)

                // 2. Если заказ выполнен, начисляем зарплату
                if (isCompleted) {
                    calculateAndAddSalaries(updatedOrder, selectedArtists)
                }

                // 3. Обновляем UI
                adapter.updateOrderStatus(order.id, isCompleted)
                Toast.makeText(requireContext(), "Статус обновлен", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun calculateAndAddSalaries(order: Order, artists: List<Artist>) {
        try {
            for (artist in artists) {
                // Получаем информацию о заработке для этого заказа и артиста
                val earning = earningService.getEarnings()
                    .firstOrNull { it.order.id == order.id && it.artist.id == artist.id }

                earning?.let {
                    // Если зарплата еще не выплачена
                    if (!it.paid) {
                        // Обновляем баланс артиста
                        val newBalance = artist.balance + it.amount
                        val updatedArtist = artist.copy(balance = newBalance)
                        artistService.updateArtist(artist.id, updatedArtist)

                        Toast.makeText(
                            requireContext(),
                            "Зарплата ${it.amount} ₽ начислена артисту ${artist.firstName} ${artist.lastName}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Ошибка начисления зарплаты",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (e: Exception) {
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
        private val onItemClick: (Order) -> Unit,
        private val onDeleteClick: (Order) -> Unit
    ) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

        private val orders = mutableListOf<Order>()

        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val date: TextView = itemView.findViewById(R.id.orderDate)
            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
            val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_manager, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            holder.date.text = formatDateShort(order.date)
            holder.performance.text = order.performance.title

            holder.itemView.setOnClickListener {
                onItemClick(order)
            }

            holder.deleteButton.setOnClickListener {
                onDeleteClick(order)
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
























//
//
//class ScheduleFragmentManager : Fragment() {
//    private lateinit var tokenManager: TokenManager
//    private lateinit var orderService: OrderServiceImpl
//    private lateinit var earningService: EarningServiceImpl
//    private lateinit var artistService: ArtistServiceImpl
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: OrderAdapter
//    private lateinit var emptyView: TextView
//    private lateinit var performanceService: PerformanceServiceImpl
//    private lateinit var artistPerformanceService: ArtistPerformanceServiceImpl
//    private lateinit var performances: List<Performance>
//    private lateinit var artistPerformances: List<ArtistPerformance>
//    private lateinit var allArtists: List<Artist>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        tokenManager = TokenManager(requireContext())
//        orderService = OrderServiceImpl()
//        earningService = EarningServiceImpl()
//        artistService = ArtistServiceImpl()
//        performanceService = PerformanceServiceImpl()
//        artistPerformanceService = ArtistPerformanceServiceImpl()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_schedule_managers, container, false)
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
//
//        view.findViewById<FloatingActionButton>(R.id.fabAddOrder).setOnClickListener {
//            showCreateOrderDialog()
//        }
//
//        loadPerformancesAndArtists()
//    }
//    private fun loadPerformancesAndArtists() {
//        lifecycleScope.launch {
//            try {
//                performances = performanceService.getPerformances()
//                artistPerformances = artistPerformanceService.getArtistPerformances()
//                allArtists = artistService.getArtists()
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
////    private fun showCreateOrderDialog() {
////        if (performances.isEmpty()) {
////            Toast.makeText(requireContext(), "Загрузка номеров...", Toast.LENGTH_SHORT).show()
////            loadPerformancesAndArtists()
////            return
////        }
////
////        val dialogView = LayoutInflater.from(requireContext())
////            .inflate(R.layout.dialog_create_order, null)
////
////        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
////        //val actvPerformance = dialogView.findViewById<AutoCompleteTextView>(R.id.actvPerformance)
////        val spinnerPerformance = dialogView.findViewById<Spinner>(R.id.spinnerPerformance)
////        val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
////        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
////        val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
////        val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)
////
////        // Настройка выбора даты
////        etDate.setOnClickListener {
////            showDatePicker { date ->
////                etDate.setText(date)
////            }
////        }
////
////        // Настройка Spinner
////        val performanceAdapter = ArrayAdapter(
////            requireContext(),
////            android.R.layout.simple_spinner_item,
////            performances.map { it.title }
////        ).apply {
////            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        }
////
////        spinnerPerformance.adapter = performanceAdapter
////
////        // Обработчик выбора номера
////        spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
////                val selectedPerformance = performances[position]
////                etAmount.setText(selectedPerformance.cost.toString())
////                updateArtistsSelection(artistsContainer, selectedPerformance)
////            }
////
////            override fun onNothingSelected(parent: AdapterView<*>?) {}
////        }
////
////        // Выбираем первый элемент по умолчанию (если есть)
////        if (performances.isNotEmpty()) {
////            val firstPerformance = performances[0]
////            etAmount.setText(firstPerformance.cost.toString())
////            updateArtistsSelection(artistsContainer, firstPerformance)
////        }
////
////        AlertDialog.Builder(requireContext())
////            .setTitle("Новый заказ")
////            .setView(dialogView)
////            .setPositiveButton("Создать") { _, _ ->
////                createNewOrder(
////                    etDate.text.toString(),
////                    //actvPerformance.text.toString(),
////                    performances[spinnerPerformance.selectedItemPosition].title,
////                    etLocation.text.toString(),
////                    etAmount.text.toString(),
////                    etComment.text.toString(),
////                    artistsContainer
////                )
////            }
////            .setNegativeButton("Отмена", null)
////            .show()
////    }
//
//    private fun showCreateOrderDialog() {
//        val dialogView = LayoutInflater.from(requireContext())
//            .inflate(R.layout.dialog_create_order, null)
//
//        val tilDate = dialogView.findViewById<TextInputLayout>(R.id.tilDate)
//        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
//        val tilPerformance = dialogView.findViewById<TextInputLayout>(R.id.tilPerformance)
//        val spinnerPerformance = dialogView.findViewById<Spinner>(R.id.spinnerPerformance)
//        val tilLocation = dialogView.findViewById<TextInputLayout>(R.id.tilLocation)
//        val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
//        val tilAmount = dialogView.findViewById<TextInputLayout>(R.id.tilAmount)
//        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
//        val tilComment = dialogView.findViewById<TextInputLayout>(R.id.tilComment)
//        val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
//        val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)
//
//        // Настройка выбора даты
//        etDate.setOnClickListener {
//            showDatePicker { date ->
//                etDate.setText(date)
//            }
//        }
//
//        // Настройка Spinner
////        val performanceAdapter = ArrayAdapter(
////            requireContext(),
////            android.R.layout.simple_spinner_item,
////            performances.map { it.title }
////        ).apply {
////            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        }
//        // Настройка Spinner
//        val performanceAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            // Добавляем пустой элемент в начало списка
//            listOf("Выберите номер") + performances.map { it.title }
//        ).apply {
//            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        }
//
//        spinnerPerformance.adapter = performanceAdapter
//
//        // Обработчик выбора номера
////        spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
////                val selectedPerformance = performances[position]
////                etAmount.setText(selectedPerformance.cost.toString())
////                updateArtistsSelection(artistsContainer, selectedPerformance)
////            }
////
////            override fun onNothingSelected(parent: AdapterView<*>?) {}
////        }
//        // Обработчик выбора номера
//        spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                // Позиция 0 - это наша подсказка "Выберите номер"
//                if (position > 0) {
//                    val selectedPerformance = performances[position - 1] // -1 потому что добавили подсказку
//                    etAmount.setText(selectedPerformance.cost.toString())
//                    updateArtistsSelection(artistsContainer, selectedPerformance)
//                } else {
//                    // Очищаем поля, если выбрана подсказка
//                    etAmount.text?.clear()
//                    artistsContainer.removeAllViews()
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//
//        // Выбираем первый элемент по умолчанию (если есть)
////        if (performances.isNotEmpty()) {
////            val firstPerformance = performances[0]
////            etAmount.setText(firstPerformance.cost.toString())
////            updateArtistsSelection(artistsContainer, firstPerformance)
////        }
//
//
//        // Валидация в реальном времени
//        etDate.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus && etDate.text.isNullOrEmpty()) {
//                tilDate.error = "Укажите дату"
//            } else {
//                tilDate.error = null
//            }
//        }
//
//        etLocation.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus && etLocation.text.isNullOrEmpty()) {
//                tilLocation.error = "Укажите место"
//            } else {
//                tilLocation.error = null
//            }
//        }
//
//        etAmount.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) {
//                try {
//                    etAmount.text.toString().toDouble()
//                    tilAmount.error = null
//                } catch (e: NumberFormatException) {
//                    tilAmount.error = "Некорректная сумма"
//                }
//            }
//        }
//
//        etComment.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus && etComment.text.isNullOrEmpty()) {
//                tilComment.error = "Укажите комментарий"
//            } else {
//                tilComment.error = null
//            }
//        }
//
//        val dialog = AlertDialog.Builder(requireContext())
//            .setTitle("Новый заказ")
//            .setView(dialogView)
//            .setPositiveButton("Создать", null) // Обработчик будет установлен позже
//            .setNegativeButton("Отмена", null)
//            .create()
//
//        dialog.setOnShowListener {
//            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//            positiveButton.setOnClickListener {
//                var isValid = true
//                // Проверка выбора номера
//                if (spinnerPerformance.selectedItemPosition == 0) {
//                    (spinnerPerformance.selectedView as? TextView)?.error = "Выберите номер"
//                    isValid = false
//                }
//
//                // Проверка даты
//                if (etDate.text.isNullOrEmpty()) {
//                    tilDate.error = "Укажите дату"
//                    isValid = false
//                }
//
//                // Проверка места
//                if (etLocation.text.isNullOrEmpty()) {
//                    tilLocation.error = "Укажите место"
//                    isValid = false
//                }
//
//                // Проверка суммы
//                val amount = try {
//                    etAmount.text.toString().toDouble()
//                } catch (e: NumberFormatException) {
//                    tilAmount.error = "Некорректная сумма"
//                    isValid = false
//                    0.0
//                }
//
//                // Проверка артистов
//                val selectedArtists = getSelectedArtists(artistsContainer)
//                if (selectedArtists.size != performances[spinnerPerformance.selectedItemPosition-1].cntArtists) {
//                    Toast.makeText(requireContext(),
//                        "Для этого номера требуется ${performances[spinnerPerformance.selectedItemPosition-1].cntArtists} артистов",
//                        Toast.LENGTH_LONG).show()
//                    isValid = false
//                }
//
//                if (isValid) {
//                    createNewOrder(
//                        etDate.text.toString(),
//                        performances[spinnerPerformance.selectedItemPosition-1].title,
//                        etLocation.text.toString(),
//                        amount.toString(),
//                        etComment.text.toString(),
//                        artistsContainer
//                    )
//                    dialog.dismiss()
//                }
//            }
//        }
//
//        dialog.show()
//    }
//
//    private fun getSelectedArtists(container: LinearLayout): List<Artist> {
//        val selectedArtists = mutableListOf<Artist>()
//        for (i in 0 until container.childCount) {
//            val view = container.getChildAt(i)
//            if (view is CheckBox && view.isChecked) {
//                val artistId = view.tag as Int
//                allArtists.firstOrNull { it.id == artistId }?.let {
//                    selectedArtists.add(it)
//                }
//            }
//        }
//        return selectedArtists
//    }
//
////    private fun updateArtistsSelection(container: LinearLayout, performance: Performance) {
////        container.removeAllViews()
////
////        // Получаем артистов для этого номера
////        val artistsForPerformance = artistPerformances
////            .filter { it.performance.id == performance.id }
////            .map { it.artist }
////
////        // Создаем чекбоксы для выбора артистов
////        artistsForPerformance.forEach { artist ->
////            val checkBox = CheckBox(requireContext()).apply {
////                text = "${artist.firstName} ${artist.lastName}"
////                tag = artist.id
////            }
////            container.addView(checkBox)
////        }
//    private fun updateArtistsSelection(container: LinearLayout, performance: Performance, selectedArtists: List<Artist> = emptyList()) {
//        container.removeAllViews()
//
//        // Получаем артистов для этого номера
//        val artistsForPerformance = artistPerformances
//            .filter { it.performance.id == performance.id }
//            .map { it.artist }
//
//        // Создаем чекбоксы для выбора артистов
//        artistsForPerformance.forEach { artist ->
//            val checkBox = CheckBox(requireContext()).apply {
//                text = "${artist.firstName} ${artist.lastName}"
//                tag = artist.id
//                isChecked = selectedArtists.any { it.id == artist.id }
//            }
//            container.addView(checkBox)
//        }
//
//        // Проверяем, что выбрано нужное количество артистов
//        if (artistsForPerformance.size < performance.cntArtists) {
//            Toast.makeText(
//                requireContext(),
//                "Внимание: для этого номера требуется ${performance.cntArtists} артистов, а доступно только ${artistsForPerformance.size}",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//    private fun showDatePicker(onDateSelected: (String) -> Unit) {
//        val calendar = Calendar.getInstance()
//        DatePickerDialog(
//            requireContext(),
//            { _, year, month, day ->
//                // Форматируем дату без использования java.time
//                val formattedDate = String.format(
//                    Locale.getDefault(),
//                    "%04d-%02d-%02d",
//                    year,
//                    month + 1,
//                    day
//                )
//                onDateSelected(formattedDate)
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        ).show()
//    }
//
////    private fun createNewOrder(
////        date: String,
////        performanceTitle: String,
////        location: String,
////        amountStr: String,
////        comment: String,
////        artistsContainer: LinearLayout
////    ) {
////        if (date.isEmpty() || performanceTitle.isEmpty() || location.isEmpty() || amountStr.isEmpty()) {
////            Toast.makeText(requireContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        val performance = performances.firstOrNull { it.title == performanceTitle }
////        if (performance == null) {
////            Toast.makeText(requireContext(), "Номер не найден", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        val amount = try {
////            amountStr.toDouble()
////        } catch (e: NumberFormatException) {
////            Toast.makeText(requireContext(), "Некорректная сумма", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        // Получаем выбранных артистов
////        val selectedArtists = mutableListOf<Artist>()
////        for (i in 0 until artistsContainer.childCount) {
////            val view = artistsContainer.getChildAt(i)
////            if (view is CheckBox && view.isChecked) {
////                val artistId = view.tag as Int
////                allArtists.firstOrNull { it.id == artistId }?.let {
////                    selectedArtists.add(it)
////                }
////            }
////        }
////
////        // Проверяем количество артистов
////        if (selectedArtists.size != performance.cntArtists) {
////            Toast.makeText(
////                requireContext(),
////                "Для этого номера требуется выбрать ${performance.cntArtists} артистов",
////                Toast.LENGTH_LONG
////            ).show()
////            return
////        }
////
////        lifecycleScope.launch {
////            try {
////                // Создаем заказ
////                val newOrder = OrderCreateUpdateSerializer(
////                    date = date,
////                    location = location,
////                    performance = performance.id,
////                    amount = amount,
////                    comment = comment,
////                    completed = false
////                )
////
////                val createdOrder = orderService.createOrder(newOrder)
////                val orders = orderService.getOrders()
////                lateinit var addedOrder: Order
////                for (order in orders){
////                    if (order.date == createdOrder.date && order.location == createdOrder.location && order.amount == createdOrder.amount && order.comment == createdOrder.comment && order.performance.id == createdOrder.performance){
////                        addedOrder = order
////                    }
////                }
////
////                // Создаем записи о заработке для артистов
////                selectedArtists.forEach { artist ->
////                    // Находим ставку артиста для этого номера
////                    val artistPerformance = artistPerformances.firstOrNull {
////                        it.artist.id == artist.id && it.performance.id == performance.id
////                    }
////
////                    if (artistPerformance != null) {
////                        val earning = EarningCreateUpdateSerializer(
////                            order = addedOrder.id,
////                            artist = artist.id,
////                            amount = artistPerformance.rate.rate,
////                            paid = false
////                        )
////                        earningService.createEarning(earning)
////                    }
////                }
////
////                // Обновляем список заказов
////                loadOrders()
////                Toast.makeText(requireContext(), "Заказ создан", Toast.LENGTH_SHORT).show()
////            } catch (e: Exception) {
////                Toast.makeText(requireContext(), "Ошибка создания заказа", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
//    private fun createNewOrder(
//        date: String,
//        performanceTitle: String,
//        location: String,
//        amountStr: String,
//        comment: String,
//        artistsContainer: LinearLayout
//    ) {
//        val performance = performances.first { it.title == performanceTitle }
//        val amount = amountStr.toDouble()
//        val selectedArtists = getSelectedArtists(artistsContainer)
//
//        lifecycleScope.launch {
//            try {
//                val newOrder = OrderCreateUpdateSerializer(
//                    date = date,
//                    location = location,
//                    performance = performance.id,
//                    amount = amount,
//                    comment = comment,
//                    completed = false
//                )
//
//                val createdOrder = orderService.createOrder(newOrder)
//                val orders = orderService.getOrders()
//                lateinit var addedOrder: Order
//                for (order in orders) {
//                    if (order.date == createdOrder.date &&
//                        order.location == createdOrder.location &&
//                        order.amount == createdOrder.amount &&
//                        order.comment == createdOrder.comment &&
//                        order.performance.id == createdOrder.performance) {
//                        addedOrder = order
//                    }
//                }
//
//                selectedArtists.forEach { artist ->
//                    val artistPerformance = artistPerformances.firstOrNull {
//                        it.artist.id == artist.id && it.performance.id == performance.id
//                    }
//
//                    if (artistPerformance != null) {
//                        val earning = EarningCreateUpdateSerializer(
//                            order = addedOrder.id,
//                            artist = artist.id,
//                            amount = artistPerformance.rate.rate,
//                            paid = false
//                        )
//                        earningService.createEarning(earning)
//                    }
//                }
//
//                loadOrders()
//                Toast.makeText(requireContext(), "Заказ создан", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка создания заказа", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun setupRecyclerView() {
//        adapter = OrderAdapter(
//            onItemClick = { order ->
//                showOrderDetails(order)
//            },
//            onDeleteClick = { order ->
//                showDeleteConfirmationDialog(order)
//            }
//        )
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//    }
//
//    private fun showDeleteConfirmationDialog(order: Order) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Удаление заказа")
//            .setMessage("Вы уверены, что хотите удалить этот заказ?")
//            .setPositiveButton("Удалить") { _, _ ->
//                deleteOrder(order)
//            }
//            .setNegativeButton("Отмена", null)
//            .show()
//    }
//
//    private fun deleteOrder(order: Order) {
//        lifecycleScope.launch {
//            try {
//                // Удаляем связанные записи о заработке
////                val earnings = earningService.getEarnings()
////                    .filter { it.order.id == order.id }
////
////                earnings.forEach { earning ->
////                    earningService.deleteEarning(earning.order.id)
////                }
//
//                // Удаляем сам заказ
//                orderService.deleteOrder(order.id)
//
//                // Обновляем список
//                loadOrders()
//                Toast.makeText(requireContext(), "Заказ удален", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка удаления заказа", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun loadOrders() {
//        lifecycleScope.launch {
//            try {
//                val today = Calendar.getInstance().apply {
//                    set(Calendar.HOUR_OF_DAY, 0)
//                    set(Calendar.MINUTE, 0)
//                    set(Calendar.SECOND, 0)
//                    set(Calendar.MILLISECOND, 0)
//                }
//
//                val allOrders = orderService.getOrders()
//                val currentAndFutureOrders = allOrders
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
//
////    private fun showOrderDetails(order: Order) {
////        lifecycleScope.launch {
////            try {
////                // Получаем всех артистов, связанных с этим заказом
////                val earnings = earningService.getEarnings()
////                    .filter { it.order.id == order.id }
////
////                val artists = earnings.map { it.artist }
////
////                val dialogView = LayoutInflater.from(requireContext())
////                    .inflate(R.layout.dialog_order_details, null)
////
////                // Заполняем основные данные
////                dialogView.findViewById<TextView>(R.id.tvDate).text = formatDate(order.date)
////                dialogView.findViewById<TextView>(R.id.tvPerformance).text = order.performance.title
////                dialogView.findViewById<TextView>(R.id.tvLocation).text = order.location
////                dialogView.findViewById<TextView>(R.id.tvComment).text = order.comment
////                dialogView.findViewById<TextView>(R.id.tvAmount).text =
////                    "%,.2f ₽".format(order.amount)
////
////
////                val switchCompleted = dialogView.findViewById<SwitchCompat>(R.id.switchCompleted)
////                switchCompleted.isChecked = order.completed
////
////                AlertDialog.Builder(requireContext())
////                    .setTitle("Детали заказа")
////                    .setView(dialogView)
////                    .setPositiveButton("Сохранить") { _, _ ->
////                        updateOrderStatus(order, switchCompleted.isChecked, earnings)
////                    }
////                    .setNegativeButton("Закрыть", null)
////                    .show()
////
////            } catch (e: Exception) {
////                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
//
//    private fun showOrderDetails(order: Order) {
//        lifecycleScope.launch {
//            try {
//                // Получаем всех артистов, связанных с этим заказом
//                val earnings = earningService.getEarnings()
//                    .filter { it.order.id == order.id }
//
//                val currentArtists = earnings.map { it.artist }
//
//                val dialogView = LayoutInflater.from(requireContext())
//                    .inflate(R.layout.dialog_edit_order_details, null)
//
//                // Получаем ссылки на элементы
//                val tilDate = dialogView.findViewById<TextInputLayout>(R.id.tilDate)
//                val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
//                val tilPerformance = dialogView.findViewById<TextInputLayout>(R.id.tilPerformance)
//                val spinnerPerformance = dialogView.findViewById<Spinner>(R.id.spinnerPerformance)
//                val tilLocation = dialogView.findViewById<TextInputLayout>(R.id.tilLocation)
//                val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
//                val tilAmount = dialogView.findViewById<TextInputLayout>(R.id.tilAmount)
//                val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
//                val tilComment = dialogView.findViewById<TextInputLayout>(R.id.tilComment)
//                val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
//                val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)
//                val switchCompleted = dialogView.findViewById<SwitchCompat>(R.id.switchCompleted)
//
//                // Заполняем текущие данные
//                etDate.setText(order.date)
//                etLocation.setText(order.location)
//                etAmount.setText(order.amount.toString())
//                etComment.setText(order.comment)
//                switchCompleted.isChecked = order.completed
//
//                // Настройка выбора даты
//                etDate.setOnClickListener {
//                    showDatePicker { date ->
//                        etDate.setText(date)
//                    }
//                }
//
//                // Настройка Spinner с номерами
//                val performanceAdapter = ArrayAdapter(
//                    requireContext(),
//                    android.R.layout.simple_spinner_item,
//                    listOf("Выберите номер") + performances.map { it.title }
//                ).apply {
//                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                }
//
//                spinnerPerformance.adapter = performanceAdapter
//
//                // Выбираем текущий номер
//                val currentPerformanceIndex = performances.indexOfFirst { it.id == order.performance.id }
//                if (currentPerformanceIndex >= 0) {
//                    spinnerPerformance.setSelection(currentPerformanceIndex + 1) // +1 из-за подсказки
//                }
//
//                // Обработчик выбора номера
//                spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        if (position > 0) {
//                            val selectedPerformance = performances[position - 1]
//                            etAmount.setText(selectedPerformance.cost.toString())
//                            updateArtistsSelection(artistsContainer, selectedPerformance, currentArtists)
//                        }
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>?) {}
//                }
//
//                // Инициализация списка артистов для текущего номера
//                updateArtistsSelection(artistsContainer, order.performance, currentArtists)
//
//                // Валидация в реальном времени
//                setupValidation(tilDate, etDate, tilLocation, etLocation, tilAmount, etAmount)
//
//                val dialog = AlertDialog.Builder(requireContext())
//                    .setTitle("Редактирование заказа")
//                    .setView(dialogView)
//                    .setPositiveButton("Сохранить") { _, _ ->
//                        updateOrderStatus(order, switchCompleted.isChecked, artistsContainer)
//                    }
//                    .setNegativeButton("Отмена", null)
//                    .create()
//
////                dialog.setOnShowListener {
////                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
////                    positiveButton.setOnClickListener {
////                        if (validateInputs(tilDate, etDate, tilLocation, etLocation, tilAmount, etAmount, spinnerPerformance)) {
////                            updateOrder(
////                                order,
////                                etDate.text.toString(),
////                                spinnerPerformance.selectedItemPosition,
////                                etLocation.text.toString(),
////                                etAmount.text.toString(),
////                                etComment.text.toString(),
////                                switchCompleted.isChecked,
////                                artistsContainer
////                            )
////                            dialog.dismiss()
////                        }
////                    }
////                }
//
//                dialog.setOnShowListener {
//                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//                    positiveButton.setOnClickListener {
//                        if (validateInputs(
//                                tilDate, etDate,
//                                tilLocation, etLocation,
//                                tilAmount, etAmount,
//                                spinnerPerformance,
//                                artistsContainer
//                            )) {
//                            updateOrder(
//                                order,
//                                etDate.text.toString(),
//                                spinnerPerformance.selectedItemPosition,
//                                etLocation.text.toString(),
//                                etAmount.text.toString(),
//                                etComment.text.toString(),
//                                switchCompleted.isChecked,
//                                artistsContainer
//                            )
//                            dialog.dismiss()
//                        }
//                    }
//                }
//
//                dialog.show()
//
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun setupValidation(
//        tilDate: TextInputLayout, etDate: TextInputEditText,
//        tilLocation: TextInputLayout, etLocation: TextInputEditText,
//        tilAmount: TextInputLayout, etAmount: TextInputEditText
//    ) {
//        etDate.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus && etDate.text.isNullOrEmpty()) {
//                tilDate.error = "Укажите дату"
//            } else {
//                tilDate.error = null
//            }
//        }
//
//        etLocation.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus && etLocation.text.isNullOrEmpty()) {
//                tilLocation.error = "Укажите место"
//            } else {
//                tilLocation.error = null
//            }
//        }
//
//        etAmount.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) {
//                try {
//                    etAmount.text.toString().toDouble()
//                    tilAmount.error = null
//                } catch (e: NumberFormatException) {
//                    tilAmount.error = "Некорректная сумма"
//                }
//            }
//        }
//    }
//
////    private fun validateInputs(
////        tilDate: TextInputLayout, etDate: TextInputEditText,
////        tilLocation: TextInputLayout, etLocation: TextInputEditText,
////        tilAmount: TextInputLayout, etAmount: TextInputEditText,
////        spinnerPerformance: Spinner
////    ): Boolean {
////        var isValid = true
////
////        // Проверка даты
////        if (etDate.text.isNullOrEmpty()) {
////            tilDate.error = "Укажите дату"
////            isValid = false
////        }
////
////        // Проверка места
////        if (etLocation.text.isNullOrEmpty()) {
////            tilLocation.error = "Укажите место"
////            isValid = false
////        }
////
////        // Проверка суммы
////        try {
////            etAmount.text.toString().toDouble()
////        } catch (e: NumberFormatException) {
////            tilAmount.error = "Некорректная сумма"
////            isValid = false
////        }
////
////        // Проверка выбора номера
////        if (spinnerPerformance.selectedItemPosition == 0) {
////            (spinnerPerformance.selectedView as? TextView)?.error = "Выберите номер"
////            isValid = false
////        }
////
////        return isValid
////    }
//    private fun validateInputs(
//        tilDate: TextInputLayout, etDate: TextInputEditText,
//        tilLocation: TextInputLayout, etLocation: TextInputEditText,
//        tilAmount: TextInputLayout, etAmount: TextInputEditText,
//        spinnerPerformance: Spinner,
//        artistsContainer: LinearLayout
//    ): Boolean {
//        var isValid = true
//
//        // Проверка даты
//        if (etDate.text.isNullOrEmpty()) {
//            tilDate.error = "Укажите дату"
//            isValid = false
//        }
//
//        // Проверка места
//        if (etLocation.text.isNullOrEmpty()) {
//            tilLocation.error = "Укажите место"
//            isValid = false
//        }
//
//        // Проверка суммы
//        try {
//            etAmount.text.toString().toDouble()
//        } catch (e: NumberFormatException) {
//            tilAmount.error = "Некорректная сумма"
//            isValid = false
//        }
//
//        // Проверка выбора номера
//        if (spinnerPerformance.selectedItemPosition == 0) {
//            (spinnerPerformance.selectedView as? TextView)?.error = "Выберите номер"
//            isValid = false
//        } else {
//            // Проверка количества артистов
//            val selectedPerformance = performances[spinnerPerformance.selectedItemPosition - 1]
//            val selectedArtistsCount = getSelectedArtists(artistsContainer).size
//
//            if (selectedArtistsCount != selectedPerformance.cntArtists) {
//                Toast.makeText(
//                    requireContext(),
//                    "Для этого номера требуется ${selectedPerformance.cntArtists} артистов",
//                    Toast.LENGTH_LONG
//                ).show()
//                isValid = false
//            }
//        }
//
//        return isValid
//    }
//
////    private fun updateOrder(
////        originalOrder: Order,
////        date: String,
////        performancePosition: Int,
////        location: String,
////        amountStr: String,
////        comment: String,
////        isCompleted: Boolean,
////        artistsContainer: LinearLayout
////    ) {
////        val selectedPerformance = performances[performancePosition - 1] // -1 из-за подсказки
////        val amount = amountStr.toDouble()
////        val selectedArtists = getSelectedArtists(artistsContainer)
////
////        lifecycleScope.launch {
////            try {
////                // 1. Обновляем сам заказ
////                val updatedOrder = originalOrder.copy(
////                    date = date,
////                    location = location,
////                    performance = selectedPerformance,
////                    amount = amount,
////                    comment = comment,
////                    completed = isCompleted
////                )
////
////                orderService.updateOrder(originalOrder.id, OrderCreateUpdateSerializer.fromOrder(updatedOrder))
////
////                // 2. Обновляем артистов (удаляем старых и добавляем новых)
////                val currentEarnings = earningService.getEarnings()
////                    .filter { it.order.id == originalOrder.id }
////
////                // Удаляем старые записи о заработке
////                currentEarnings.forEach { earning ->
////                    earningService.deleteEarning(earning.order.id)
////                }
////
////                // Добавляем новые записи о заработке
////                selectedArtists.forEach { artist ->
////                    val artistPerformance = artistPerformances.firstOrNull {
////                        it.artist.id == artist.id && it.performance.id == selectedPerformance.id
////                    }
////
////                    if (artistPerformance != null) {
////                        val earning = EarningCreateUpdateSerializer(
////                            order = originalOrder.id,
////                            artist = artist.id,
////                            amount = artistPerformance.rate.rate,
////                            paid = false
////                        )
////                        earningService.createEarning(earning)
////                    }
////                }
////
////                // 3. Если заказ выполнен, начисляем зарплату
////                if (isCompleted) {
////                    calculateAndAddSalaries(selectedArtists.map { artist ->
////                        val artistPerformance = artistPerformances.firstOrNull {
////                            it.artist.id == artist.id && it.performance.id == selectedPerformance.id
////                        }
////                        Earning(
////                            order = updatedOrder,
////                            artist = artist,
////                            amount = artistPerformance?.rate?.rate ?: 0.0,
////                            paid = false
////                        )
////                    })
////                }
////
////                // 4. Обновляем список заказов
////                loadOrders()
////                Toast.makeText(requireContext(), "Заказ обновлен", Toast.LENGTH_SHORT).show()
////            } catch (e: Exception) {
////                Toast.makeText(requireContext(), "Ошибка обновления заказа", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
//    private fun updateOrder(
//        originalOrder: Order,
//        date: String,
//        performancePosition: Int,
//        location: String,
//        amountStr: String,
//        comment: String,
//        isCompleted: Boolean,
//        artistsContainer: LinearLayout
//    ) {
//        // Проверяем, что выбран номер (не подсказка)
//        if (performancePosition == 0) {
//            Toast.makeText(requireContext(), "Выберите номер", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val selectedPerformance = performances[performancePosition - 1] // -1 из-за подсказки
//        val amount = amountStr.toDouble()
//        val selectedArtists = getSelectedArtists(artistsContainer)
//
//        // Проверка количества артистов
//        if (selectedArtists.size != selectedPerformance.cntArtists) {
//            Toast.makeText(
//                requireContext(),
//                "Для этого номера требуется ${selectedPerformance.cntArtists} артистов",
//                Toast.LENGTH_LONG
//            ).show()
//            return
//        }
//
//        lifecycleScope.launch {
//            try {
//                // 1. Обновляем сам заказ
//                val updatedOrder = originalOrder.copy(
//                    date = date,
//                    location = location,
//                    performance = selectedPerformance,
//                    amount = amount,
//                    comment = comment,
//                    completed = isCompleted
//                )
//
//                orderService.updateOrder(originalOrder.id, OrderCreateUpdateSerializer.fromOrder(updatedOrder))
//
//                // 2. Получаем текущие записи о заработке
//                val currentEarnings = earningService.getEarnings()
//                    .filter { it.order.id == originalOrder.id }
//
//                // 3. Определяем, изменился ли номер или список артистов
//                val performanceChanged = originalOrder.performance.id != selectedPerformance.id
//                val artistsChanged = currentEarnings.map { it.artist.id }.sorted() !=
//                        selectedArtists.map { it.id }.sorted()
//
//                // 4. Если номер или артисты изменились - удаляем старые записи
//                if (performanceChanged || artistsChanged) {
//                    currentEarnings.forEach { earning ->
//                        earningService.deleteEarning(earning.id)
//                    }
//
//                    // Создаем новые записи о заработке
//                    selectedArtists.forEach { artist ->
//                        val artistPerformance = artistPerformances.firstOrNull {
//                            it.artist.id == artist.id && it.performance.id == selectedPerformance.id
//                        }
//
//                        artistPerformance?.let {
//                            val earning = EarningCreateUpdateSerializer(
//                                order = originalOrder.id,
//                                artist = artist.id,
//                                amount = it.rate.rate,
//                                paid = false
//                            )
//                            earningService.createEarning(earning)
//                        }
//                    }
//                }
//
//                // 5. Если заказ выполнен, начисляем зарплату
////                if (isCompleted) {
////                    val earningsToPay = if (performanceChanged || artistsChanged) {
////                        selectedArtists.map { artist ->
////                            val artistPerformance = artistPerformances.firstOrNull {
////                                it.artist.id == artist.id && it.performance.id == selectedPerformance.id
////                            }
////                            Earning(
////                                id = id,
////                                order = updatedOrder,
////                                artist = artist,
////                                amount = artistPerformance?.rate?.rate ?: 0.0,
////                                paid = false
////                            )
////                        }
////                    } else {
////                        currentEarnings
////                    }
////
////                    calculateAndAddSalaries(earningsToPay)
////                }
//
//                // 6. Обновляем список заказов
//                loadOrders()
//                Toast.makeText(requireContext(), "Заказ обновлен", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Ошибка обновления заказа", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun updateOrderStatus(order: Order, isCompleted: Boolean, artistsContainer: LinearLayout) {
//        lifecycleScope.launch {
//            try {
//                // 1. Обновляем статус заказа
//                val updatedOrder = order.copy(completed = isCompleted)
//                orderService.updateOrder(order.id, OrderCreateUpdateSerializer.fromOrder(updatedOrder))
//
//                val selectedArtists = getSelectedArtists(artistsContainer)
//                // 2. Если заказ выполнен, начисляем зарплату всем артистам
//                if (isCompleted) {
//                    calculateAndAddSalaries(order, selectedArtists)
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
//    private suspend fun calculateAndAddSalaries(order: Order, artists: List<Artist>) {
////        try {
////            for (earning in earnings) {
////                if (!earning.paid) {
////                    // Обновляем баланс артиста
////                    val artist = earning.artist
////                    val newBalance = artist.balance + earning.amount
////                    val updatedArtist = artist.copy(balance = newBalance)
////                    artistService.updateArtist(artist.id, updatedArtist)
////
////                    // Помечаем заработок как выплаченный
//////                    val updatedEarning = earning.copy(paid = true)
//////                    earningService.updateEarning(
//////                        earning.order.id,
//////                        EarningCreateUpdateSerializer.fromEarning(updatedEarning)
//////                    )
////                }
////            }
////            Toast.makeText(requireContext(), "Зарплаты начислены", Toast.LENGTH_SHORT).show()
////        } catch (e: Exception) {
////            Toast.makeText(requireContext(), "Ошибка начисления зарплат", Toast.LENGTH_SHORT).show()
////        }
//
//        try {
//            // 1. Получаем текущего артиста
////            val artist = artistService.getArtistByName(
////                tokenManager.getFirstName(),
////                tokenManager.getLastName()
////            )
//            for (artist in artists){
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
////                    // 5. Помечаем заработок как выплаченный
////                    val updatedEarning = it.copy(paid = true)
////                    earningService.updateEarning(
////                        it.order.id,
////                        EarningCreateUpdateSerializer.fromEarning(updatedEarning)
////                    )
////
////                    // 6. Обновляем локальные данные
////                    tokenManager.saveBalance(newBalance)
//                    //updateBalanceInUI(newBalance)
//
//                    Toast.makeText(
//                        requireContext(),
//                        "Зарплата ${it.amount} ₽ начислена",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }}
//        } catch (e: Exception) {
//            Toast.makeText(
//                requireContext(),
//                "Ошибка начисления зарплаты",
//                Toast.LENGTH_SHORT
//            ).show()
//        }    }
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
//    inner class OrderAdapter(
//        private val onItemClick: (Order) -> Unit,
//        private val onDeleteClick: (Order) -> Unit
//    ) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
//
//        private val orders = mutableListOf<Order>()
//
//        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val date: TextView = itemView.findViewById(R.id.orderDate)
//            val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//            val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
//            //val statusIcon: ImageView = itemView.findViewById(R.id.ivStatus)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_order_manager, parent, false)
//            return OrderViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//            val order = orders[position]
//            holder.date.text = formatDateShort(order.date)
//            holder.performance.text = order.performance.title
//
//            // Иконка статуса
////            holder.statusIcon.setImageResource(
////                if (order.completed) R.drawable.ic_check_circle else R.drawable.ic_pending
////            )
////            holder.statusIcon.setColorFilter(
////                ContextCompat.getColor(holder.itemView.context,
////                    if (order.completed) R.color.green else R.color.orange),
////                PorterDuff.Mode.SRC_IN
////            )
//
//            holder.itemView.setOnClickListener {
//                onItemClick(order)
//            }
//
//            holder.deleteButton.setOnClickListener {
//                onDeleteClick(order)
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