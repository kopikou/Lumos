package com.example.lumos.presentation.views.fragments.managers
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
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
import com.example.lumos.presentation.adapters.UnpaidArtistsAdapter
import com.example.lumos.presentation.dialogs.ArtistsFragment
import com.example.lumos.presentation.dialogs.PerformancesFragment
import com.example.lumos.presentation.viewModels.managers.ManagementManagerViewModelFactory

class ManagementFragmentManager : Fragment() {
    private lateinit var unpaidEarningsCard: CardView
    private lateinit var unpaidCountTextView: TextView
    private lateinit var viewModel: ManagementManagerViewModel

    private lateinit var artistsCountCard: CardView
    private lateinit var artistsCountText: TextView

    private lateinit var performancesCountCard: CardView
    private lateinit var performancesCountText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_management_mangers, container, false)

        // Инициализация репозиториев и сервисов
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

        // Создание фабрики ViewModel
        val factory = ManagementManagerViewModelFactory(
            GetUnpaidArtistsUseCase(earningRepositoryImpl, orderRepositoryImpl),
            MarkEarningsAsPaidUseCase(artistRepositoryImpl, earningRepositoryImpl),
            GetArtistDetailsUseCase(artistRepositoryImpl, artistPerformanceRepositoryImpl, performanceRepositoryImpl),
            GetPerformanceArtistsUseCase(artistPerformanceRepositoryImpl, artistRepositoryImpl),
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

        viewModel = ViewModelProvider(this, factory).get(ManagementManagerViewModel::class.java)

        initViews(view)
        setupObservers()
        setupClickListeners()
        loadInitialData()

        return view
    }

    private fun initViews(view: View) {
        unpaidEarningsCard = view.findViewById(R.id.unpaid_earnings_card)
        unpaidCountTextView = view.findViewById(R.id.unpaid_count_text)
        artistsCountCard = view.findViewById(R.id.artists_count_card)
        artistsCountText = view.findViewById(R.id.artists_count_text)
        performancesCountCard = view.findViewById(R.id.performances_count_card)
        performancesCountText = view.findViewById(R.id.performances_count_text)
    }

    private fun setupObservers() {
        viewModel.unpaidEarningsCount.observe(viewLifecycleOwner) { count ->
            unpaidCountTextView.text = count.toString()
            unpaidEarningsCard.visibility = if (count > 0) View.VISIBLE else View.GONE
        }

        viewModel.artistsCount.observe(viewLifecycleOwner) { count ->
            artistsCountText.text = count.toString()
        }

        viewModel.performancesCount.observe(viewLifecycleOwner) { count ->
            performancesCountText.text = count.toString()
        }
    }

    private fun setupClickListeners() {
        unpaidEarningsCard.setOnClickListener { showUnpaidArtistsDialog() }
        artistsCountCard.setOnClickListener { showArtistsFragment() }
        performancesCountCard.setOnClickListener { showPerformancesFragment() }
    }

    private fun loadInitialData() {
        viewModel.loadUnpaidEarnings()
        viewModel.loadArtistsCount()
        viewModel.loadPerformancesCount()
        viewModel.loadTypes()
    }

    private fun showUnpaidArtistsDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_unpaid_artists)
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.7).toInt()
            )
        }

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.artists_recycler)
        val adapter = UnpaidArtistsAdapter { artistWithUnpaid ->
            viewModel.markAsPaid(artistWithUnpaid)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.unpaidArtists.observe(viewLifecycleOwner) { artists ->
            adapter.submitList(artists)
        }

        dialog.show()
    }

    private fun showArtistsFragment() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, ArtistsFragment.newInstance())
            addToBackStack(null)
        }
    }

    private fun showPerformancesFragment() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, PerformancesFragment.newInstance())
            addToBackStack(null)
        }
    }

    companion object {
        fun newInstance() = ManagementFragmentManager()
    }
}