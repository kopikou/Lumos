package com.example.lumos.presentation.fragments.managers
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.Performance
import com.example.lumos.presentation.adapters.ArtistsAdapter
import com.example.lumos.presentation.adapters.ArtistsSimpleAdapter
import com.example.lumos.presentation.adapters.PerformancesAdapter
import com.example.lumos.presentation.adapters.PerformancesSimpleAdapter
import com.example.lumos.presentation.viewModels.ManagementViewModel
import com.example.lumos.presentation.adapters.UnpaidArtistsAdapter

class ManagementFragmentManager : Fragment() {
    private lateinit var unpaidEarningsCard: CardView
    private lateinit var unpaidCountTextView: TextView
    private lateinit var viewModel: ManagementViewModel

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

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this).get(ManagementViewModel::class.java)

        // Находим элементы интерфейса
        unpaidEarningsCard = view.findViewById(R.id.unpaid_earnings_card)
        unpaidCountTextView = view.findViewById(R.id.unpaid_count_text)

        artistsCountCard = view.findViewById(R.id.artists_count_card)
        artistsCountText = view.findViewById(R.id.artists_count_text)

        performancesCountCard = view.findViewById(R.id.performances_count_card)
        performancesCountText = view.findViewById(R.id.performances_count_text)

        // Наблюдаем за изменениями количества невыплаченных зарплат
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

        // Обработка нажатия на карточку
        unpaidEarningsCard.setOnClickListener {
            showUnpaidArtistsDialog()
        }

        // Загружаем данные
        viewModel.loadUnpaidEarnings()

        // Обработка нажатий
        artistsCountCard.setOnClickListener {
            showAllArtistsDialog()
        }

        // Загрузка данных
        viewModel.loadArtistsCount()

        performancesCountCard.setOnClickListener {
            showAllPerformancesDialog()
        }

        // Загрузка данных
        viewModel.loadPerformancesCount()

        return view
    }


    private fun showUnpaidArtistsDialog() {
//        val dialog = Dialog(requireContext())
//        dialog.setContentView(R.layout.dialog_unpaid_artists)
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

    private fun showAllArtistsDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_all_artists)
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.7).toInt()
            )
        }

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.artists_recycler)
        val adapter = ArtistsAdapter { artist ->
            viewModel.loadArtistDetails(artist.id)
            showArtistDetailsDialog(artist)
            dialog.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.allArtists.observe(viewLifecycleOwner) { artists ->
            adapter.submitList(artists)
        }

        dialog.show()
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

        nameText.text = "${artist.firstName} ${artist.lastName}"
        phoneText.text = "Телефон: ${artist.phone}"
        balanceText.text = "Баланс: ${artist.balance} руб."

        val adapter = PerformancesAdapter()
        performancesRecycler.layoutManager = LinearLayoutManager(requireContext())
        performancesRecycler.adapter = adapter

        viewModel.artistDetails.observe(viewLifecycleOwner) { details ->
            if (details.artist.id == artist.id) {
                adapter.submitList(details.performances)
            }
        }

        dialog.show()
    }

    private fun showAllPerformancesDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_all_performances)
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.7).toInt()
            )
        }

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.performances_recycler)
        val adapter = PerformancesSimpleAdapter { performance ->
            viewModel.loadPerformanceDetails(performance.id)
            showPerformanceDetailsDialog(performance)
            dialog.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.allPerformances.observe(viewLifecycleOwner) { performances ->
            adapter.submitList(performances)
        }

        dialog.show()
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

        // Загружаем артистов для этого номера
        viewModel.loadArtistsForPerformance(performance)

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
}