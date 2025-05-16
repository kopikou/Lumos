package com.example.lumos.presentation.fragments.managers
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lumos.R
import com.example.lumos.domain.entities.Artist
import com.example.lumos.presentation.activities.LoginActivity
import com.example.lumos.retrofit.authentification.TokenManager
import com.example.lumos.retrofit.services.ArtistServiceImpl
import com.example.lumos.retrofit.services.UserServiceImpl
import com.google.android.material.button.MaterialButton

class ProfileFragmentManager : Fragment() {
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
        return inflater.inflate(R.layout.fragment_profile_managers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Находим все TextView
        val tvIncome = view.findViewById<TextView>(R.id.tvIncome)
        val tvExpense= view.findViewById<TextView>(R.id.tvExpense)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)


        // Обработчик кнопки выхода
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
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
        tokenManager.clearTokens()

        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }
}