package com.example.lumos.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R

import com.example.lumos.data.repository.AuthRepository
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.AuthServiceImpl
import com.example.lumos.data.repository.AuthRepositoryImpl
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    //private lateinit var authRepository: AuthRepository
    private lateinit var authRepository:AuthRepositoryImpl
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

        //authRepository = AuthRepository()

        tokenManager = TokenManager(this)
        authRepository = AuthRepositoryImpl(AuthServiceImpl(),tokenManager)

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
//                    tokenManager.saveTokens(tokens.access, tokens.refresh)
//                    tokenManager.saveAdminStatus(tokens.user.isAdmin)
//                    tokenManager.saveUserNames(tokens.user.firstName, tokens.user.lastName)
//                    tokenManager.saveUserId(tokens.user.id)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}