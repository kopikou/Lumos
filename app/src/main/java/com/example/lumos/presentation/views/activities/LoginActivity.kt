package com.example.lumos.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R

import com.example.lumos.data.repository.AuthRepository
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.AuthServiceImpl
import com.example.lumos.data.repository.AuthRepositoryImpl
import com.example.lumos.databinding.ActivityLoginBinding
import com.example.lumos.domain.usecases.LoginUseCase
import com.example.lumos.presentation.viewModels.LoginState
import com.example.lumos.presentation.viewModels.LoginViewModel
import com.example.lumos.presentation.viewModels.LoginViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

//class LoginActivity : AppCompatActivity() {
//    //private lateinit var authRepository: AuthRepository
//    private lateinit var authRepository:AuthRepositoryImpl
//    private lateinit var tokenManager: TokenManager
//
//    private lateinit var loginButton: MaterialButton
//    private lateinit var usernameEditText: TextInputEditText
//    private lateinit var passwordEditText: TextInputEditText
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        loginButton = findViewById(R.id.loginButton)
//        usernameEditText = findViewById(R.id.usernameEditText)
//        passwordEditText = findViewById(R.id.passwordEditText)
//
//        //authRepository = AuthRepository()
//
//        tokenManager = TokenManager(this)
//        authRepository = AuthRepositoryImpl(AuthServiceImpl(),tokenManager)
//
//        loginButton.setOnClickListener {
//            val username = usernameEditText.text.toString()
//            val password = passwordEditText.text.toString()
//
//            if (username.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//
//            lifecycleScope.launch {
//                val tokens = authRepository.login(username, password)
//                if (tokens != null) {
//
//                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                    finish()
//                } else {
//                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }
//}

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
        val authRepository = AuthRepositoryImpl(AuthServiceImpl(), tokenManager)
        val loginUseCase = LoginUseCase(authRepository, tokenManager)

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(loginUseCase)
        )[LoginViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> showLoading(true)
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

    private fun showLoading(show: Boolean) {
        // Показать/скрыть индикатор загрузки
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}