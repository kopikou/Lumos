package com.example.lumos.presentation.views.fragments.artists
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.UserUpdateRequest
import com.example.lumos.presentation.views.activities.LoginActivity
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.UserServiceImpl
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileFragmentArtist(private val artist: Artist) : Fragment() {
    private lateinit var tokenManager: TokenManager
    private lateinit var artistService: ArtistServiceImpl
    private lateinit var userService: UserServiceImpl
    private var userId: Int = 0
    private lateinit var currentArtist: Artist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
        artistService = ArtistServiceImpl()
        userService = UserServiceImpl()
        userId = tokenManager.getUserId()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadArtistData()

        // Находим все TextView
        val tvArtistName = view.findViewById<TextView>(R.id.tvArtistName)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
        val btnEditProfile = view.findViewById<MaterialButton>(R.id.btnEditProfile)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        // Устанавливаем данные артиста
        tvArtistName.text = "${artist.firstName} ${artist.lastName}"
        tvPhone.text = "+ ${artist.phone}"
        tvBalance.text = "${artist.balance} ₽"

        // Обработчик кнопки редактирования
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        // Обработчик кнопки выхода
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }
    private fun loadArtistData() {
        lifecycleScope.launch {
            try {
                currentArtist = artistService.getArtistByName(tokenManager.getFirstName(),tokenManager.getLastName())
                updateUI(currentArtist!!)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_profile, null)

        val etFirstName = dialogView.findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = dialogView.findViewById<TextInputEditText>(R.id.etLastName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)

        etFirstName.setText(currentArtist.firstName)
        etLastName.setText(currentArtist.lastName)
        etPhone.setText(currentArtist.phone)

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать профиль")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newFirstName = etFirstName.text.toString()
                val newLastName = etLastName.text.toString()
                val newPhone = etPhone.text.toString()

                if (validateInput(newFirstName, newLastName, newPhone)) {
                    lifecycleScope.launch {
                        try {
                            // Обновляем артиста
                            val updatedArtist = artist.copy(
                                firstName = newFirstName,
                                lastName = newLastName,
                                phone = newPhone
                            )
                            artistService.updateArtist(artist.id, updatedArtist)

                            // Обновляем пользователя
                            userService.updateUser(
                                userId,
                                UserUpdateRequest(newFirstName, newLastName)
                            )

                            // Обновляем локальные данные
                            currentArtist = updatedArtist
                            updateUI(updatedArtist)
                            showSuccessMessage()

                            // Обновляем TokenManager (если нужно)
                            tokenManager.saveUserNames(newFirstName, newLastName)

                        } catch (e: Exception) {
                            showErrorMessage()
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

    private fun updateUI(artist: Artist) {
        view?.findViewById<TextView>(R.id.tvArtistName)?.text = "${artist.firstName} ${artist.lastName}"
        view?.findViewById<TextView>(R.id.tvPhone)?.text = artist.phone
        view?.findViewById<TextView>(R.id.tvBalance)?.text = "%,.2f ₽".format(artist.balance)//.replace(',', ' ')
    }

    private fun showSuccessMessage() {
        Toast.makeText(requireContext(), "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage() {
        Toast.makeText(requireContext(), "Ошибка при обновлении профиля", Toast.LENGTH_SHORT).show()
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
        tokenManager.clearTokens()

        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }
}