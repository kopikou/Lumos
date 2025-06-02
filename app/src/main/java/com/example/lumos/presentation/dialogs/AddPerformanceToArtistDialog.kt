package com.example.lumos.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.lumos.data.remote.impl.ArtistPerformanceServiceImpl
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.remote.impl.OrderServiceImpl
import com.example.lumos.data.remote.impl.PerformanceServiceImpl
import com.example.lumos.data.remote.impl.ShowRateServiceImpl
import com.example.lumos.data.remote.impl.TypeServiceImpl
import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.data.repository.ShowRateRepositoryImpl
import com.example.lumos.data.repository.TypeRepositoryImpl
import com.example.lumos.databinding.DialogAddPerformanceToArtistBinding
import com.example.lumos.domain.entities.Performance
import com.example.lumos.domain.entities.ShowRate
import com.example.lumos.domain.usecases.AddPerformanceToArtistUseCase
import com.example.lumos.domain.usecases.CreateArtistUseCase
import com.example.lumos.domain.usecases.CreatePerformanceUseCase
import com.example.lumos.domain.usecases.DeleteArtistUseCase
import com.example.lumos.domain.usecases.DeletePerformanceUseCase
import com.example.lumos.domain.usecases.GetArtistDetailsUseCase
import com.example.lumos.domain.usecases.GetPerformanceArtistsUseCase
import com.example.lumos.domain.usecases.GetTypesUseCase
import com.example.lumos.domain.usecases.GetUnpaidArtistsUseCase
import com.example.lumos.domain.usecases.MarkEarningsAsPaidUseCase
import com.example.lumos.presentation.viewModels.managers.ManagementManagerViewModel
import com.example.lumos.presentation.viewModels.managers.ManagementManagerViewModelFactory

class AddPerformanceToArtistDialog : DialogFragment() {
    private var _binding: DialogAddPerformanceToArtistBinding? = null
    private val binding get() = _binding!!

    var onPerformanceAdded: ((performanceId: Int, rateId: Int) -> Unit)? = null
    private lateinit var performancesAdapter: ArrayAdapter<String>
    private lateinit var ratesAdapter: ArrayAdapter<String>
    private val performancesMap = mutableMapOf<String, Performance>()
    private val ratesMap = mutableMapOf<String, ShowRate>()
    private var currentPerformance: Performance? = null
    private var currentRate: ShowRate? = null

    val earningServiceImpl = EarningServiceImpl()
    val earningRepositoryImpl = EarningRepositoryImpl(earningServiceImpl)
    val orderServiceImpl = OrderServiceImpl()
    val orderRepositoryImpl = OrderRepositoryImpl(orderServiceImpl)
    val artistServiceImpl = ArtistServiceImpl()
    val artistRepositoryImpl = ArtistRepositoryImpl(artistServiceImpl)
    val artistPerformanceServiceImpl = ArtistPerformanceServiceImpl()
    val artistPerformanceRepositoryImpl = ArtistPerformanceRepositoryImpl(artistPerformanceServiceImpl)
    val performanceServiceImpl = PerformanceServiceImpl()
    val performanceRepositoryImpl = PerformanceRepositoryImpl(performanceServiceImpl)
    val typeRepositoryImpl = TypeRepositoryImpl(TypeServiceImpl())
    val showRateRepositoryImpl = ShowRateRepositoryImpl(ShowRateServiceImpl())

    private val viewModel: ManagementManagerViewModel by viewModels {
        ManagementManagerViewModelFactory(
            GetUnpaidArtistsUseCase(earningRepositoryImpl, orderRepositoryImpl),
            MarkEarningsAsPaidUseCase(artistRepositoryImpl, earningRepositoryImpl),
            GetArtistDetailsUseCase(artistRepositoryImpl, artistPerformanceRepositoryImpl, performanceRepositoryImpl),
            GetPerformanceArtistsUseCase(artistPerformanceRepositoryImpl,artistRepositoryImpl),
            artistRepositoryImpl,
            performanceRepositoryImpl,
            typeRepositoryImpl,
            CreateArtistUseCase(artistRepositoryImpl),
            DeleteArtistUseCase(artistRepositoryImpl),
            CreatePerformanceUseCase(performanceRepositoryImpl),
            DeletePerformanceUseCase(performanceRepositoryImpl),
            GetTypesUseCase(typeRepositoryImpl),
            AddPerformanceToArtistUseCase(artistPerformanceRepositoryImpl),
            showRateRepositoryImpl
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPerformanceToArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadTypes()

        // Инициализация адаптеров для строк
        performancesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        ratesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerPerformances.adapter = performancesAdapter
        binding.spinnerRates.adapter = ratesAdapter

        // Загрузка номеров
        viewModel.allPerformances.observe(viewLifecycleOwner) { performances ->
            performancesMap.clear()
            performancesAdapter.clear()

            performances.forEach { performance ->
                val title = performance.title
                performancesMap[title] = performance
                performancesAdapter.add(title)
            }

            performancesAdapter.notifyDataSetChanged()
        }

        // Наблюдаем за ставками
        viewModel.ratesForType.observe(viewLifecycleOwner) { rates ->
            ratesMap.clear()
            ratesAdapter.clear()

            rates.forEach { rate ->
                val rateValue = rate.rate.toString()
                ratesMap[rateValue] = rate
                ratesAdapter.add(rateValue)
            }

            ratesAdapter.notifyDataSetChanged()
        }

        // Обработчик выбора номера
        binding.spinnerPerformances.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTitle = performancesAdapter.getItem(position)
                currentPerformance = performancesMap[selectedTitle]
                currentPerformance?.let { performance ->
                    viewModel.allTypes.value?.find { it.id == performance.type.id }?.let { type ->
                        viewModel.loadRatesForType(type.id)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Обработчик выбора ставки
        binding.spinnerRates.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRate = ratesAdapter.getItem(position)
                currentRate = ratesMap[selectedRate]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnAdd.setOnClickListener {
            val performanceId = currentPerformance?.id ?: return@setOnClickListener
            val rateId = currentRate?.id ?: return@setOnClickListener
            onPerformanceAdded?.invoke(performanceId, rateId)
            dismiss()
        }

        // Загружаем номера при открытии диалога
        viewModel.loadPerformancesCount()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddPerformanceToArtistDialog"
    }
}