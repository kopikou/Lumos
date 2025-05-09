package com.example.lumos.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R

import com.example.lumos.retrofit.authentification.AuthRepository
import com.example.lumos.retrofit.authentification.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var authRepository: AuthRepository
    private lateinit var tokenManager: TokenManager

    private lateinit var loginButton: MaterialButton
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        authRepository = AuthRepository()
        tokenManager = TokenManager(this)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            lifecycleScope.launch {
                val tokens = authRepository.login(username, password)
                if (tokens != null) {
                    tokenManager.saveTokens(tokens.access, tokens.refresh)
                    tokenManager.saveAdminStatus(tokens.user.isAdmin)
                    tokenManager.saveUserNames(tokens.user.firstName, tokens.user.lastName)
                    tokenManager.saveUserId(tokens.user.id)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
//class LoginActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var authRepository: AuthRepository
//    private lateinit var tokenManager: TokenManager
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        authRepository = AuthRepository()
//        tokenManager = TokenManager(this)
//
//        binding.loginButton.setOnClickListener {
//            val username = binding.usernameEditText.text.toString()
//            val password = binding.passwordEditText.text.toString()
//
//            if (username.isEmpty() || password.isEmpty()) {
//                binding.usernameLayout.error = if (username.isEmpty()) "Введите имя пользователя" else null
//                binding.passwordLayout.error = if (password.isEmpty()) "Введите пароль" else null
//                return@setOnClickListener
//            }
//
//            lifecycleScope.launch {
//                try {
//                    binding.progressBar.visibility = View.VISIBLE
//                    binding.loginButton.isEnabled = false
//
//                    val tokens = authRepository.login(username, password)
//                    if (tokens != null) {
//                        tokenManager.saveTokens(tokens.access, tokens.refresh)
//                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                        finish()
//                    } else {
//                        showError("Неверный логин или пароль")
//                    }
//                } catch (e: Exception) {
//                    showError("Ошибка сети: ${e.message}")
//                } finally {
//                    binding.progressBar.visibility = View.GONE
//                    binding.loginButton.isEnabled = true
//                }
//            }
//        }
//    }
//
//    private fun showError(message: String) {
//        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
//    }
//}