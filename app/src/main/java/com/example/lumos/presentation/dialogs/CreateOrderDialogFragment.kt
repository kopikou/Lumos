package com.example.lumos.presentation.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.lumos.databinding.DialogCreateOrderBinding
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.Performance
import java.util.Calendar

class CreateOrderDialogFragment : DialogFragment() {
    private var _binding: DialogCreateOrderBinding? = null
    private val binding get() = _binding!!

    private var performances: List<Performance> = emptyList()
    private var artistPerformances: List<Pair<Int, Int>> = emptyList()
    private var allArtists: List<Artist> = emptyList()

    var onCreateOrderListener: ((
        date: String,
        performanceId: Int,
        location: String,
        amount: Double,
        comment: String,
        artistIds: List<Int>
    ) -> Unit)? = null

    fun setData(
        performances: List<Performance>,
        artistPerformances: List<Pair<Int, Int>>,
        allArtists: List<Artist>
    ) {
        this.performances = performances
        this.artistPerformances = artistPerformances
        this.allArtists = allArtists
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCreateOrderBinding.inflate(LayoutInflater.from(requireContext()))

        val performanceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Выберите номер") + performances.map { it.title }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerPerformance.adapter = performanceAdapter

        binding.etDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.etDate.setText(selectedDate)
                binding.tilDate.error = null
            }
        }

        binding.spinnerPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val selectedPerformance = performances[position - 1]
                    binding.etAmount.setText(selectedPerformance.cost.toString())
                    updateArtistsSelection(binding.artistsContainer, selectedPerformance)
                } else {
                    binding.artistsContainer.removeAllViews()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Новый заказ")
            .setView(binding.root)
            .setPositiveButton("Создать") { _, _ ->
                if (validateInputs()) {
                    val performancePosition = binding.spinnerPerformance.selectedItemPosition
                    val amount = binding.etAmount.text.toString().toDouble()
                    val selectedArtists = getSelectedArtists(binding.artistsContainer)

                    onCreateOrderListener?.invoke(
                        binding.etDate.text.toString(),
                        performances[performancePosition - 1].id,
                        binding.etLocation.text.toString(),
                        amount,
                        binding.etComment.text.toString(),
                        selectedArtists.map { it.id }
                    )
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (binding.etDate.text.isNullOrEmpty()) {
            binding.tilDate.error = "Укажите дату"
            isValid = false
        } else {
            binding.tilDate.error = null
        }

        if (binding.etLocation.text.isNullOrEmpty()) {
            binding.tilLocation.error = "Укажите место"
            isValid = false
        } else {
            binding.tilLocation.error = null
        }

        try {
            binding.etAmount.text.toString().toDouble()
            binding.tilAmount.error = null
        } catch (e: NumberFormatException) {
            binding.tilAmount.error = "Некорректная сумма"
            isValid = false
        }

        if (binding.spinnerPerformance.selectedItemPosition == 0) {
            (binding.spinnerPerformance.selectedView as? TextView)?.error = "Выберите номер"
            isValid = false
        }

        if (binding.etComment.text.isNullOrEmpty()) {
            binding.tilComment.error = "Укажите комментарий"
            isValid = false
        } else {
            binding.tilComment.error = null
        }

        val selectedArtists = getSelectedArtists(binding.artistsContainer)
        if (binding.spinnerPerformance.selectedItemPosition > 0) {
            val requiredCount = performances[binding.spinnerPerformance.selectedItemPosition - 1].cntArtists
            if (selectedArtists.size != requiredCount) {
                showToast("Для этого номера требуется $requiredCount артистов")
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
            .filter { it.first == performance.id }
            .mapNotNull { pair -> allArtists.firstOrNull { it.id == pair.second } }

        artistsForPerformance.forEach { artist ->
            CheckBox(requireContext()).apply {
                text = "${artist.firstName} ${artist.lastName}"
                tag = artist.id
                isChecked = selectedArtists.any { it.id == artist.id }
                container.addView(this)
            }
        }

        if (artistsForPerformance.size < performance.cntArtists) {
            showToast("Внимание: требуется ${performance.cntArtists} артистов, доступно ${artistsForPerformance.size}")
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

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}