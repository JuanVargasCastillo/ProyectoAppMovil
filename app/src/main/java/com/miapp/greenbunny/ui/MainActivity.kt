package com.miapp.greenbunny.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.api.TokenManager
import com.miapp.greenbunny.databinding.ActivityMainBinding
import com.miapp.greenbunny.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Si ya hay sesión activa, vamos directo a Home
        if (tokenManager.isLoggedIn()) {
            goToHome()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString()?.trim().orEmpty()

            // --- VALIDACIONES EN LA APP ---
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Correo inválido. Debe tener formato usuario@dominio.com", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(
                    this,
                    "Contraseña inválida: mínimo 8 caracteres, 1 mayúscula, 1 número y 1 carácter especial",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // --- MOSTRAR PROGRESO ---
            binding.progress.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false

            // --- LLAMADA AL BACKEND ---
            lifecycleScope.launch {
                try {
                    val publicAuthService = RetrofitClient.createAuthService(this@MainActivity)
                    val loginResponse = withContext(Dispatchers.IO) {
                        publicAuthService.login(LoginRequest(email = email, password = password))
                    }

                    val authToken = loginResponse.token   // ✅ cambio aquí
                    val userProfile = loginResponse.user  // ✅ cambio aquí

                    // Guardar token temporalmente
                    getSharedPreferences("session", Context.MODE_PRIVATE).edit().apply {
                        putString("jwt_token", authToken)
                        apply()
                    }

                    // Guardar usuario en TokenManager
                    tokenManager.saveAuth(
                        token = authToken,
                        userName = userProfile.name,
                        userEmail = userProfile.email
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "¡Bienvenido, ${userProfile.name}! 🎉",
                        Toast.LENGTH_SHORT
                    ).show()

                    goToHome()

                } catch (e: Exception) {
                    Log.e("MainActivity", "Login error", e)
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    tokenManager.clear()
                } finally {
                    binding.progress.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // --- FUNCIONES DE VALIDACIÓN ---
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        return emailPattern.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        val uppercase = Pattern.compile("[A-Z]")
        val number = Pattern.compile("[0-9]")
        val special = Pattern.compile("[^A-Za-z0-9]")
        return uppercase.matcher(password).find() &&
                number.matcher(password).find() &&
                special.matcher(password).find()
    }
}
