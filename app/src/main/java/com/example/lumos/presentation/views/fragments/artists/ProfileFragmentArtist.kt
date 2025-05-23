package com.example.lumos.presentation.views.fragments.artists
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R
import com.example.lumos.domain.entities.Artist
import com.example.lumos.presentation.views.activities.LoginActivity
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.UserServiceImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.UserRepositoryImpl
import com.example.lumos.databinding.FragmentProfileArtistsBinding
import com.example.lumos.domain.usecases.GetArtistByNameUseCase
import com.example.lumos.domain.usecases.LogoutUseCase
import com.example.lumos.domain.usecases.UpdateArtistUseCase
import com.example.lumos.domain.usecases.UpdateUserUseCase
import com.example.lumos.presentation.viewModels.artists.ProfileArtistViewModel
import com.example.lumos.presentation.viewModels.artists.ProfileArtistViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileFragmentArtist() : Fragment() {
    private lateinit var binding: FragmentProfileArtistsBinding
    private lateinit var viewModel: ProfileArtistViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        setupListeners()

        val firstName = arguments?.getString("firstName") ?: tokenManager.getFirstName()
        val lastName = arguments?.getString("lastName") ?: tokenManager.getLastName()

        if (firstName != null && lastName != null) {
            viewModel.loadArtist(firstName, lastName)
        }
    }

    private fun setupViewModel() {
        tokenManager = TokenManager(requireContext())
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        val userRepository = UserRepositoryImpl(UserServiceImpl())

        val getArtistUseCase = GetArtistByNameUseCase(artistRepository)
        val updateArtistUseCase = UpdateArtistUseCase(artistRepository)
        val updateUserUseCase = UpdateUserUseCase(userRepository)
        val logoutUseCase = LogoutUseCase(tokenManager)

        val factory = ProfileArtistViewModelFactory(
            getArtistUseCase,
            updateArtistUseCase,
            updateUserUseCase,
            logoutUseCase,
            tokenManager
        )

        viewModel = ViewModelProvider(this, factory)[ProfileArtistViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            artist?.let {
                updateUI(it)
            } ?: run {
                // Обработка случая, когда artist = null
                Toast.makeText(requireContext(), "Данные артиста не загружены", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun updateUI(artist: Artist) {
        binding.tvArtistName.text = "${artist.firstName} ${artist.lastName}"
        binding.tvPhone.text = artist.phone
        binding.tvBalance.text = "%,.2f ₽".format(artist.balance)
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_profile, null)

        val etFirstName = dialogView.findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = dialogView.findViewById<TextInputEditText>(R.id.etLastName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)

        viewModel.artist.value?.let { artist ->
            etFirstName.setText(artist.firstName)
            etLastName.setText(artist.lastName)
            etPhone.setText(artist.phone)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать профиль")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newFirstName = etFirstName.text.toString()
                val newLastName = etLastName.text.toString()
                val newPhone = etPhone.text.toString()

                if (validateInput(newFirstName, newLastName, newPhone)) {
                    lifecycleScope.launch {
                        val success = viewModel.updateProfile(
                            artistId = viewModel.artist.value?.id ?: return@launch,
                            userId = tokenManager.getUserId() ?: return@launch,
                            firstName = newFirstName,
                            lastName = newLastName,
                            phone = newPhone
                        )

                        if (success) {
                            Toast.makeText(
                                requireContext(),
                                "Профиль успешно обновлен",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun validateInput(
        firstName: String,
        lastName: String,
        phone: String
    ): Boolean {
        return when {
            firstName.isBlank() -> {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show()
                false
            }
            lastName.isBlank() -> {
                Toast.makeText(requireContext(), "Введите фамилию", Toast.LENGTH_SHORT).show()
                false
            }
            phone.isBlank() -> {
                Toast.makeText(requireContext(), "Введите телефон", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти из профиля?")
            .setPositiveButton("Да") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun performLogout() {
        // Очищаем токены и перенаправляем на экран входа
        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }
}