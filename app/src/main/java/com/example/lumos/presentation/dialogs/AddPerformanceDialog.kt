package com.example.lumos.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.lumos.databinding.DialogAddPerformanceBinding
import com.example.lumos.domain.entities.Type

class AddPerformanceDialog : DialogFragment() {
    private var _binding: DialogAddPerformanceBinding? = null
    private val binding get() = _binding!!

    private var types: List<Type> = emptyList()
    private lateinit var typeAdapter: ArrayAdapter<String>

    var onPerformanceCreated: ((
        title: String,
        duration: Int,
        cost: Double,
        typeId: Int,
        cntArtists: Int
    ) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPerformanceBinding.inflate(inflater, container, false)

        typeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerType.adapter = typeAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (types.isNotEmpty()) {
            updateSpinner()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (types.isEmpty()) {
                Toast.makeText(requireContext(), "Типы ещё не загружены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = binding.etTitle.text.toString().trim()
            val duration = binding.etDuration.text.toString().toIntOrNull() ?: 0
            val cost = binding.etCost.text.toString().toDoubleOrNull() ?: 0.0
            val cntArtists = binding.etArtistsCount.text.toString().toIntOrNull() ?: 1
            val selectedType = types.getOrNull(binding.spinnerType.selectedItemPosition)

            if (title.isEmpty() || duration <= 0 || cost <= 0 || cntArtists <= 0 || selectedType == null) {
                Toast.makeText(requireContext(), "Необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onPerformanceCreated?.invoke(title, duration, cost, selectedType.id, cntArtists)
            dismiss()
        }
    }

    fun setTypes(types: List<Type>) {
        this.types = types
        if (_binding != null) {
            updateSpinner()
        }
    }

    private fun updateSpinner() {
        typeAdapter.clear()
        typeAdapter.addAll(types.map { it.showType })
        typeAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddPerformanceDialog"
    }
}