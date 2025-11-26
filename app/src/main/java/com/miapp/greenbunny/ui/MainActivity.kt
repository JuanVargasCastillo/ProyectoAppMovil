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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainActivity (Login)
 *
 * Flujo:
 * 1. Login con email y password (servicio público).
 * 2. Guardado temporal del token.
 * 3. Obtener datos de usuario con servicio privado (/auth/me).
 * 4. Guardar token y usuario en TokenManager.
 * 5. Ir a HomeActivity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Si ya hay sesión, ir directo a Home
        if (tokenManager.isLoggedIn()) {
            goToHome()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString()?.trim().orEmpty()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Completa email y password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progress.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false

            lifecycleScope.launch {
                try {
                    // 1️⃣ Login público
                    val loginResponse = withContext(Dispatchers.IO) {
                        RetrofitClient.createAuthService(this@MainActivity, requiresAuth = false)
                            .login(LoginRequest(email, password))
                    }

                    val authToken = loginResponse.authToken

                    // 2️⃣ Guardado temporal usando TokenManager para habilitar el interceptor
                    tokenManager.saveAuth(
                        token = authToken,
                        userName = "",
                        userEmail = "",
                        userRole = null,
                        userId = null
                    )

                    // 3️⃣ Servicio privado con token → obtener perfil básico
                    val privateAuthService = RetrofitClient.createAuthService(this@MainActivity, requiresAuth = true)
                    val me = withContext(Dispatchers.IO) { privateAuthService.getMe() }

                    Log.d("MainActivity", "JSON /auth/me: ${Gson().toJson(me)}")

                    // 4️⃣ Si faltan role/status, completar con GET /user/{id}
                    val userService = RetrofitClient.createUserService(this@MainActivity)
                    val fullUser = withContext(Dispatchers.IO) { userService.getUser(me.id) }

                    val role = fullUser.role ?: me.role
                    val status = fullUser.status?.lowercase()

                    // 5️⃣ Respetar cuenta bloqueada
                    val blockedStatuses = setOf("blocked", "bloqueado", "inactive", "inactivo")
                    if (status in blockedStatuses) {
                        tokenManager.clear()
                        Toast.makeText(this@MainActivity, "Tu cuenta está bloqueada. Contacta al administrador.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    // 6️⃣ Guardar sesión con role y status reales
                    tokenManager.saveAuth(
                        token = authToken,
                        userName = fullUser.name ?: me.name ?: "",
                        userEmail = fullUser.email ?: me.email ?: "",
                        userRole = role,
                        userId = fullUser.id,
                        userStatus = status
                    )

                    Toast.makeText(this@MainActivity, "¡Bienvenido, ${fullUser.name ?: me.name}!", Toast.LENGTH_SHORT).show()
                    goToHome()

                } catch (e: Exception) {
                    tokenManager.clear()
                    Toast.makeText(this@MainActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    binding.progress.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun goToHome() {
        val role = tokenManager.getUserRole()?.lowercase()
        val dest = if (role == "admin") HomeActivityAdmin::class.java else HomeActivityCliente::class.java
        startActivity(Intent(this, dest))
        finish()
    }
}
