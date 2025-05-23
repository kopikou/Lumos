package com.example.lumos.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

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
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(this)
        val authRepository = AuthRepositoryImpl(AuthServiceImpl())
        val loginUseCase = LoginUseCase(authRepository, tokenManager)

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(loginUseCase)
        )[LoginViewModel::class.java]
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
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}