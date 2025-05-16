package com.example.lumos.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var tokenManager: TokenManager
    private val viewModel: MainViewModel by viewModels()
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        // Проверка авторизации
        if (tokenManager.getAccessToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottom_navigation)
        setupBottomNavigation()

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_schedule
        }

        lifecycleScope.launch {
            viewModel.loadArtist(tokenManager.getFirstName(), tokenManager.getLastName())
        }
    }

    private fun setupBottomNavigation() {
        // Устанавливаем соответствующее меню в зависимости от роли
        bottomNav.menu.clear()
        bottomNav.inflateMenu(
            if (tokenManager.isAdmin()) R.menu.bottom_nav_menu_manager
            else R.menu.bottom_nav_menu_artist
        )

        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNav.isItemHorizontalTranslationEnabled = false

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_schedule -> {
                    replaceFragmentForCurrentUser(ScheduleFragmentManager(), ScheduleFragmentArtist())
                    true
                }
                R.id.nav_control -> {
                    // Только для админов
                    replaceFragment(ManagementFragmentManager())
                    true
                }
                R.id.nav_history -> {
                    // Только для обычных пользователей
                    replaceFragment(ManagementFragmentArtist())
                    true
                }
                R.id.nav_profile -> {
                    if (tokenManager.isAdmin()) {
                        // Для администратора
                        replaceFragment(ProfileFragmentManager())
                    } else {
                        // Для артиста
                        viewModel.artist.value?.let { artist ->
                            replaceFragment(ProfileFragmentArtist(artist))
                        }
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
        val fragment = if (tokenManager.isAdmin()) {
            adminFragment
        } else {
            userFragment
        }

        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}