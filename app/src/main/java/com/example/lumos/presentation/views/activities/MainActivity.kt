package com.example.lumos.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R
import com.example.lumos.presentation.views.fragments.artists.ManagementFragmentArtist
import com.example.lumos.presentation.views.fragments.artists.ProfileFragmentArtist
import com.example.lumos.presentation.views.fragments.artists.ScheduleFragmentArtist
import com.example.lumos.presentation.views.fragments.managers.ManagementFragmentManager
import com.example.lumos.presentation.views.fragments.managers.ProfileFragmentManager
import com.example.lumos.presentation.views.fragments.managers.ScheduleFragmentManager
import com.example.lumos.presentation.viewModels.MainViewModel
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.databinding.ActivityMainBinding
import com.example.lumos.domain.usecases.GetArtistByNameUseCase
import com.example.lumos.presentation.viewModels.MainViewModelFactory
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация зависимостей
        tokenManager = TokenManager(this)
        val artistService = ArtistServiceImpl()
        val artistRepository = ArtistRepositoryImpl(artistService)
        val getArtistUseCase = GetArtistByNameUseCase(artistRepository)

        // Создание ViewModel
        val viewModelFactory = MainViewModelFactory(getArtistUseCase)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        // Проверка авторизации
        if (tokenManager.getAccessToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupBottomNavigation()
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_schedule
        }

        lifecycleScope.launch {
            tokenManager.getFirstName()?.let { firstName ->
                tokenManager.getLastName()?.let { lastName ->
                    viewModel.loadArtist(firstName, lastName)
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.menu.clear()
        binding.bottomNavigation.inflateMenu(
            if (tokenManager.isAdmin()) R.menu.bottom_nav_menu_manager
            else R.menu.bottom_nav_menu_artist
        )

        binding.bottomNavigation.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        binding.bottomNavigation.isItemHorizontalTranslationEnabled = false

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_schedule -> {
                    replaceFragmentForCurrentUser(ScheduleFragmentManager(), ScheduleFragmentArtist())
                    true
                }
                R.id.nav_control -> {
                    replaceFragment(ManagementFragmentManager())
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(ManagementFragmentArtist())
                    true
                }
                R.id.nav_profile -> {
                    if (tokenManager.isAdmin()) {
                        replaceFragment(ProfileFragmentManager())
                    } else {
                        replaceFragment(ProfileFragmentArtist())
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragmentForCurrentUser(
        adminFragment: Fragment,
        userFragment: Fragment
    ) {
        val fragment = if (tokenManager.isAdmin()) adminFragment else userFragment
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}