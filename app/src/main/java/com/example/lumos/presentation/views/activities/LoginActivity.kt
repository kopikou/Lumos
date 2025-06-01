package com.example.lumos.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.AuthServiceImpl
import com.example.lumos.data.repository.AuthRepositoryImpl
import com.example.lumos.databinding.ActivityLoginBinding
import com.example.lumos.domain.usecases.LoginUseCase
import com.example.lumos.presentation.viewModels.LoginState
import com.example.lumos.presentation.viewModels.LoginViewModel
import com.example.lumos.presentation.viewModels.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels { createViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun createViewModelFactory(): LoginViewModelFactory {
        val tokenManager = TokenManager(this)
        val authService = AuthServiceImpl()
        val authRepository = AuthRepositoryImpl(authService)
        val loginUseCase = LoginUseCase(authRepository, tokenManager)
        return LoginViewModelFactory(loginUseCase)
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Success -> navigateToMain()
                is LoginState.Error -> showError(state.message)
            }
        }
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            when {
                username.isEmpty() -> {
                    binding.usernameLayout.error = "Username is required"
                    binding.usernameEditText.requestFocus()
                }
                password.isEmpty() -> {
                    binding.passwordLayout.error = "Password is required"
                    binding.passwordEditText.requestFocus()
                }
                else -> {
                    clearErrors()
                    viewModel.login(username, password)
                }
            }
        }

        binding.usernameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.usernameLayout.error = null
        }

        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.passwordLayout.error = null
        }
    }

    private fun clearErrors() {
        binding.usernameLayout.error = null
        binding.passwordLayout.error = null
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}