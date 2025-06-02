package com.example.lumos.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.example.lumos.databinding.DialogAllArtistsBinding
import com.example.lumos.domain.entities.Artist
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
import com.example.lumos.presentation.adapters.ArtistsAdapter
import com.example.lumos.presentation.adapters.PerformancesSimpleAdapter
import com.example.lumos.presentation.viewModels.managers.ManagementManagerViewModel
import com.example.lumos.presentation.viewModels.managers.ManagementManagerViewModelFactory
import kotlinx.coroutines.launch

class ArtistsFragment : Fragment() {
    private var _binding: DialogAllArtistsBinding? = null
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

    private lateinit var adapter: ArtistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAllArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        viewModel.loadArtistsCount()

        binding.fabAddArtist.setOnClickListener {
            showAddArtistDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ArtistsAdapter(
            onItemClick = { artist ->
                viewModel.loadArtistDetails(artist.id)
                showArtistDetailsDialog(artist) },
            onDeleteClick = { artist -> showDeleteArtistDialog(artist) }
        )
        binding.artistsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.artistsRecycler.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allArtists.observe(viewLifecycleOwner) { artists ->
                    adapter.submitList(artists)
                }
            }
        }
    }

    private fun showAddArtistDialog() {
        val dialog = AddArtistDialog().apply {
            onArtistCreated = { firstName, lastName, phone ->
                viewModel.createNewArtist(firstName, lastName, phone)
            }
        }
        dialog.show(parentFragmentManager, AddArtistDialog.TAG)
    }

    private fun showDeleteArtistDialog(artist: Artist) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить артиста")
            .setMessage("Вы действительно хотите удалить ${artist.firstName} ${artist.lastName}?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteArtistById(artist.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showArtistDetailsDialog(artist: Artist) {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_artist_details)
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.8).toInt()
            )
        }

        val nameText = dialog.findViewById<TextView>(R.id.artist_name)
        val phoneText = dialog.findViewById<TextView>(R.id.artist_phone)
        val balanceText = dialog.findViewById<TextView>(R.id.artist_balance)
        val performancesRecycler = dialog.findViewById<RecyclerView>(R.id.performances_recycler)
        val addPerformanceBtn = dialog.findViewById<Button>(R.id.btn_add_performance)

        nameText.text = "${artist.firstName} ${artist.lastName}"
        phoneText.text = "Телефон: ${artist.phone}"
        balanceText.text = "Баланс: ${artist.balance} руб."

        val adapter = PerformancesSimpleAdapter()
        performancesRecycler.layoutManager = LinearLayoutManager(requireContext())
        performancesRecycler.adapter = adapter

        viewModel.artistDetails.observe(viewLifecycleOwner) { details ->
            if (details.artist.id == artist.id) {
                adapter.submitList(details.performances)
            }
        }

        addPerformanceBtn.setOnClickListener {
            showAddPerformanceDialog(artist.id)
        }

        dialog.show()
    }

    private fun showAddPerformanceDialog(artistId: Int) {
        val dialog = AddPerformanceToArtistDialog().apply {
            onPerformanceAdded = { performanceId, rateId ->
                viewModel.addPerformanceToArtist(artistId, performanceId, rateId)
            }
        }
        dialog.show(parentFragmentManager, AddPerformanceToArtistDialog.TAG)
    }

    companion object {
        fun newInstance() = ArtistsFragment()
    }
}