package com.example.lumos.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
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
import com.example.lumos.databinding.DialogAllPerformancesBinding
import com.example.lumos.domain.entities.Performance
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
import com.example.lumos.presentation.adapters.ArtistsSimpleAdapter
import com.example.lumos.presentation.adapters.PerformancesAdapter
import com.example.lumos.presentation.viewModels.managers.ManagementManagerViewModel
import com.example.lumos.presentation.viewModels.managers.ManagementManagerViewModelFactory
import kotlinx.coroutines.launch

class PerformancesFragment : Fragment() {
    private var _binding: DialogAllPerformancesBinding? = null
    private val binding get() = _binding!!

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

    private lateinit var adapter: PerformancesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAllPerformancesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        viewModel.loadPerformancesCount()
        viewModel.loadTypes()

        binding.fabAddPerformance.setOnClickListener {
            showAddPerformanceDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = PerformancesAdapter(
            onItemClick = { performance ->
                viewModel.loadPerformanceDetails(performance.id)
                showPerformanceDetailsDialog(performance)},
            onDeleteClick = { performance -> showDeletePerformanceDialog(performance) }
        )
        binding.performancesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.performancesRecycler.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allPerformances.observe(viewLifecycleOwner) { performances ->
                    adapter.submitList(performances)
                }
            }
        }
    }

    private fun showAddPerformanceDialog() {
        val dialog = AddPerformanceDialog().apply {
            viewModel.allTypes.value?.let { setTypes(it) }
            onPerformanceCreated = { title, duration, cost, typeId, cntArtists ->
                viewModel.createNewPerformance(title, duration, cost, typeId, cntArtists)
            }
        }

        dialog.show(parentFragmentManager, AddPerformanceDialog.TAG)
    }

    private fun showDeletePerformanceDialog(performance: Performance) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить номер")
            .setMessage("Вы дейстивтельно хотите удалить ${performance.title}?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deletePerformanceById(performance.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPerformanceDetailsDialog(performance: Performance) {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_performance_details)
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.7).toInt()
            )
        }

        val titleText = dialog.findViewById<TextView>(R.id.performance_title)
        val durationText = dialog.findViewById<TextView>(R.id.performance_duration)
        val costText = dialog.findViewById<TextView>(R.id.performance_cost)
        val typeText = dialog.findViewById<TextView>(R.id.performance_type)
        val artistsCountText = dialog.findViewById<TextView>(R.id.performance_artists_count)

        val artistsRecycler = dialog.findViewById<RecyclerView>(R.id.performance_artists_recycler)
        val noArtistsText = dialog.findViewById<TextView>(R.id.no_artists_text)
        val artistsAdapter = ArtistsSimpleAdapter()

        titleText.text = performance.title
        durationText.text = "Продолжительность: ${performance.duration} мин"
        costText.text = "Стоимость: ${performance.cost} руб."
        typeText.text = "Тип: ${performance.type.showType}"
        artistsCountText.text = "Количество артистов: ${performance.cntArtists}"

        artistsRecycler.layoutManager = LinearLayoutManager(requireContext())
        artistsRecycler.adapter = artistsAdapter

        viewModel.loadArtistsForPerformance(performance.id)

        viewModel.performanceArtists.observe(viewLifecycleOwner) { artists ->
            if (artists.isEmpty()) {
                noArtistsText.visibility = View.VISIBLE
                artistsRecycler.visibility = View.GONE
            } else {
                noArtistsText.visibility = View.GONE
                artistsRecycler.visibility = View.VISIBLE
                artistsAdapter.submitList(artists)
            }
        }

        dialog.show()
    }

    companion object {
        fun newInstance() = PerformancesFragment()
    }
}