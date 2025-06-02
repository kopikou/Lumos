package com.example.lumos.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.lumos.databinding.DialogAddArtistBinding

class AddArtistDialog : DialogFragment() {
    private var _binding: DialogAddArtistBinding? = null
    private val binding get() = _binding!!

    var onArtistCreated: ((firstName: String, lastName: String, phone: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onArtistCreated?.invoke(firstName, lastName, phone)
            dismiss()
        }
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
        const val TAG = "AddArtistDialog"
    }
}
