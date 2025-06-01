package com.example.lumos.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.lumos.R
import com.example.lumos.databinding.DialogEditProfileBinding
import com.example.lumos.domain.entities.Artist

class ProfileEditDialogFragment : DialogFragment() {
    private lateinit var binding: DialogEditProfileBinding

    // Сохраняем callback через SavedStateRegistry
    private var onSaveClicked: ((String, String, String) -> Unit)? = null

    companion object {
        private const val ARG_FIRST_NAME = "firstName"
        private const val ARG_LAST_NAME = "lastName"
        private const val ARG_PHONE = "phone"
        private const val SAVE_CALLBACK_KEY = "save_callback_key"

        fun newInstance(artist: Artist): ProfileEditDialogFragment {
            return ProfileEditDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FIRST_NAME, artist.firstName)
                    putString(ARG_LAST_NAME, artist.lastName)
                    putString(ARG_PHONE, artist.phone)
                }
            }
        }
    }

    // Устанавливаем callback из фрагмента
    fun setOnSaveListener(listener: (String, String, String) -> Unit) {
        onSaveClicked = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val firstName = requireArguments().getString(ARG_FIRST_NAME)!!
        val lastName = requireArguments().getString(ARG_LAST_NAME)!!
        val phone = requireArguments().getString(ARG_PHONE)!!

        binding = DialogEditProfileBinding.inflate(layoutInflater).apply {
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            etPhone.setText(phone)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_profile_title)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val newFirstName = binding.etFirstName.text.toString()
                val newLastName = binding.etLastName.text.toString()
                val newPhone = binding.etPhone.text.toString()

                when {
                    newFirstName.isBlank() -> binding.etFirstName.error = getString(R.string.name_required)
                    newLastName.isBlank() -> binding.etLastName.error = getString(R.string.lastname_required)
                    newPhone.isBlank() -> binding.etPhone.error = getString(R.string.phone_required)
                    else -> {
                        if (isAdded) {
                            onSaveClicked?.invoke(newFirstName, newLastName, newPhone)
                        }
                        dismiss()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем текущие значения полей
        outState.putString(ARG_FIRST_NAME, binding.etFirstName.text.toString())
        outState.putString(ARG_LAST_NAME, binding.etLastName.text.toString())
        outState.putString(ARG_PHONE, binding.etPhone.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            binding.etFirstName.setText(it.getString(ARG_FIRST_NAME))
            binding.etLastName.setText(it.getString(ARG_LAST_NAME))
            binding.etPhone.setText(it.getString(ARG_PHONE))
        }
    }
}