package com.example.lumos.presentation.views.fragments.managers
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
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
import com.example.lumos.data.remote.impl.ArtistPerformanceServiceImpl
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.remote.impl.OrderServiceImpl
import com.example.lumos.data.remote.impl.PerformanceServiceImpl
import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.Performance
import com.example.lumos.databinding.FragmentScheduleManagersBinding
import com.example.lumos.domain.usecases.CreateOrderUseCase
import com.example.lumos.domain.usecases.DeleteOrderUseCase
import com.example.lumos.domain.usecases.GetOrdersUseCase
import com.example.lumos.domain.usecases.UpdateOrderUseCase
import com.example.lumos.presentation.adapters.OrderManagerAdapter
import com.example.lumos.presentation.viewModels.managers.ScheduleManagerViewModel
import com.example.lumos.presentation.viewModels.managers.ScheduleManagerViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleFragmentManager : Fragment() {
    private lateinit var viewModel: ScheduleManagerViewModel
    private lateinit var binding: FragmentScheduleManagersBinding
    private lateinit var adapter: OrderManagerAdapter
    private val performanceRepositoryImpl = PerformanceRepositoryImpl(PerformanceServiceImpl())
    private val artistPerformanceRepository =
        ArtistPerformanceRepositoryImpl(ArtistPerformanceServiceImpl())
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
        binding = FragmentScheduleManagersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        viewModel.loadOrders()

        binding.fabAddOrder.setOnClickListener {
            showCreateOrderDialog()
        }
        loadPerformancesAndArtists()
    }

    private fun setupViewModel() {
        val orderRepository = OrderRepositoryImpl(OrderServiceImpl())
        val getOrdersUseCase = GetOrdersUseCase(orderRepository)
        val createOrderUseCase =
            CreateOrderUseCase(orderRepository, earningRepositoryImpl, artistPerformanceRepository)
        val updateOrderUseCase = UpdateOrderUseCase(
            orderRepository,
            earningRepositoryImpl,
            artistPerformanceRepository,
            artistRepository
        )
        val deleteOrderUseCase = DeleteOrderUseCase(orderRepository)

        viewModel = ViewModelProvider(
            this,
            ScheduleManagerViewModelFactory(
                getOrdersUseCase,
                createOrderUseCase,
                updateOrderUseCase,
                deleteOrderUseCase
            )
        )[ScheduleManagerViewModel::class.java]
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
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showCreateOrderDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_order, null)

        // Инициализация элементов UI
        val tilDate = dialogView.findViewById<TextInputLayout>(R.id.tilDate)
        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
        val spinnerPerformance = dialogView.findViewById<Spinner>(R.id.spinnerPerformance)
        val tilLocation = dialogView.findViewById<TextInputLayout>(R.id.tilLocation)
        val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
        val tilAmount = dialogView.findViewById<TextInputLayout>(R.id.tilAmount)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
        val tilComment = dialogView.findViewById<TextInputLayout>(R.id.tilComment)
        val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
        val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)

        // Загрузка данных для спиннера
        val performanceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Выберите номер") + performances.map { it.title }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerPerformance.adapter = performanceAdapter

        // Обработчик выбора даты
        etDate.setOnClickListener {
            showDatePicker { selectedDate ->
                etDate.setText(selectedDate)
                tilDate.error = null
            }
        }

        // Обработчик выбора номера
        spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    val selectedPerformance = performances[position - 1]
                    etAmount.setText(selectedPerformance.cost.toString())
                    updateArtistsSelection(artistsContainer, selectedPerformance)
                } else {
                    artistsContainer.removeAllViews()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Создание диалога
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Новый заказ")
            .setView(dialogView)
            .setPositiveButton("Создать", null)
            .setNegativeButton("Отмена", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (validateOrderInputs(
                        tilDate, etDate,
                        tilLocation, etLocation,
                        tilAmount, etAmount,
                        spinnerPerformance,
                        tilComment, etComment,
                        artistsContainer
                    )
                ) {
                    val performancePosition = spinnerPerformance.selectedItemPosition
                    val amount = etAmount.text.toString().toDouble()
                    val selectedArtists = getSelectedArtists(artistsContainer)

                    viewModel.createOrder(
                        date = etDate.text.toString(),
                        performanceId = performances[performancePosition - 1].id,
                        location = etLocation.text.toString(),
                        amount = amount,
                        comment = etComment.text.toString(),
                        artistIds = selectedArtists.map { it.id }
                    ) {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Заказ создан", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showEditOrderDialog(order: Order) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_order_details, null)

        // Инициализация элементов UI
        val tilDate = dialogView.findViewById<TextInputLayout>(R.id.tilDate)
        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
        val spinnerPerformance = dialogView.findViewById<Spinner>(R.id.spinnerPerformance)
        val tilLocation = dialogView.findViewById<TextInputLayout>(R.id.tilLocation)
        val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
        val tilAmount = dialogView.findViewById<TextInputLayout>(R.id.tilAmount)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
        val tilComment = dialogView.findViewById<TextInputLayout>(R.id.tilComment)
        val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
        val artistsContainer = dialogView.findViewById<LinearLayout>(R.id.artistsContainer)
        val switchCompleted = dialogView.findViewById<SwitchCompat>(R.id.switchCompleted)

        // Заполнение текущими данными
        etDate.setText(order.date)
        etLocation.setText(order.location)
        etAmount.setText(order.amount.toString())
        etComment.setText(order.comment)
        switchCompleted.isChecked = order.completed

        // Загрузка данных для спиннера
        val performances = performances
        val performanceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Выберите номер") + performances.map { it.title }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerPerformance.adapter = performanceAdapter

        // Установка текущего выбранного номера
        val currentPerformanceIndex = performances.indexOfFirst { it.id == order.performance.id }
        if (currentPerformanceIndex >= 0) {
            spinnerPerformance.setSelection(currentPerformanceIndex + 1)
        }
        lifecycleScope.launch {

            // Загрузка текущих артистов
            val currentArtists = earningRepositoryImpl.getEarnings()
                .filter { it.order.id == order.id }
                .map { it.artist }

            updateArtistsSelection(artistsContainer, order.performance, currentArtists)

            // Обработчики
            etDate.setOnClickListener {
                showDatePicker { selectedDate ->
                    etDate.setText(selectedDate)
                    tilDate.error = null
                }
            }

            spinnerPerformance.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position > 0) {
                            val selectedPerformance = performances[position - 1]
                            etAmount.setText(selectedPerformance.cost.toString())
                            updateArtistsSelection(
                                artistsContainer,
                                selectedPerformance,
                                currentArtists
                            )
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }

        // Создание диалога
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Редактирование заказа")
            .setView(dialogView)
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (validateOrderInputs(
                        tilDate, etDate,
                        tilLocation, etLocation,
                        tilAmount, etAmount,
                        spinnerPerformance,
                        tilComment, etComment,
                        artistsContainer
                    )
                ) {
                    val performancePosition = spinnerPerformance.selectedItemPosition
                    val amount = etAmount.text.toString().toDouble()
                    val selectedArtists = getSelectedArtists(artistsContainer)

                    viewModel.updateOrder(
                        orderId = order.id,
                        date = etDate.text.toString(),
                        performanceId = performances[performancePosition - 1].id,
                        location = etLocation.text.toString(),
                        amount = amount,
                        comment = etComment.text.toString(),
                        isCompleted = switchCompleted.isChecked,
                        artistIds = selectedArtists.map { it.id }
                    ) {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Заказ обновлен", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(order: Order) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление заказа")
            .setMessage("Вы уверены, что хотите удалить этот заказ?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteOrder(order.id) {
                    Toast.makeText(requireContext(), "Заказ удален", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun validateOrderInputs(
        tilDate: TextInputLayout, etDate: TextInputEditText,
        tilLocation: TextInputLayout, etLocation: TextInputEditText,
        tilAmount: TextInputLayout, etAmount: TextInputEditText,
        spinnerPerformance: Spinner,
        tilComment: TextInputLayout, etComment: TextInputEditText,
        artistsContainer: LinearLayout
    ): Boolean {
        var isValid = true

        // Проверка даты
        if (etDate.text.isNullOrEmpty()) {
            tilDate.error = "Укажите дату"
            isValid = false
        } else {
            tilDate.error = null
        }

        // Проверка места
        if (etLocation.text.isNullOrEmpty()) {
            tilLocation.error = "Укажите место"
            isValid = false
        } else {
            tilLocation.error = null
        }

        // Проверка суммы
        try {
            etAmount.text.toString().toDouble()
            tilAmount.error = null
        } catch (e: NumberFormatException) {
            tilAmount.error = "Некорректная сумма"
            isValid = false
        }

        // Проверка выбора номера
        if (spinnerPerformance.selectedItemPosition == 0) {
            (spinnerPerformance.selectedView as? TextView)?.error = "Выберите номер"
            isValid = false
        }

        // Проверка комментария
        if (etComment.text.isNullOrEmpty()) {
            tilComment.error = "Укажите комментарий"
            isValid = false
        } else {
            tilComment.error = null
        }

        // Проверка артистов
        val selectedArtists = getSelectedArtists(artistsContainer)
        if (spinnerPerformance.selectedItemPosition > 0) {
            val requiredCount = performances[spinnerPerformance.selectedItemPosition - 1].cntArtists
            if (selectedArtists.size != requiredCount) {
                Toast.makeText(
                    requireContext(),
                    "Для этого номера требуется $requiredCount артистов",
                    Toast.LENGTH_LONG
                ).show()
                isValid = false
            }
        }

        return isValid
    }

    private fun updateArtistsSelection(
        container: LinearLayout,
        performance: Performance,
        selectedArtists: List<Artist> = emptyList()
    ) {
        container.removeAllViews()

        val artistsForPerformance = artistPerformances
            .filter { it.performance.id == performance.id }
            .map { it.artist }

        artistsForPerformance.forEach { artist ->
            CheckBox(requireContext()).apply {
                text = "${artist.firstName} ${artist.lastName}"
                tag = artist.id
                isChecked = selectedArtists.any { it.id == artist.id }
                container.addView(this)
            }
        }

        if (artistsForPerformance.size < performance.cntArtists) {
            Toast.makeText(
                requireContext(),
                "Внимание: требуется ${performance.cntArtists} артистов, доступно ${artistsForPerformance.size}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getSelectedArtists(container: LinearLayout): List<Artist> {
        return (0 until container.childCount)
            .map { container.getChildAt(it) }
            .filterIsInstance<CheckBox>()
            .filter { it.isChecked }
            .mapNotNull { checkbox ->
                allArtists.firstOrNull { it.id == checkbox.tag as Int }
            }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val formattedDate = "%04d-%02d-%02d".format(year, month + 1, day)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}