package com.example.app_sos.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener una instancia de SharedPreferences
        val sharedPref: SharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        // Verificar si el usuario ya ha iniciado sesión
        if (isLoggedIn) {
            // El usuario está autenticado, redirigir a la pantalla principal
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            // El usuario no está autenticado, redirigir a la pantalla de inicio de sesión
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        // Finalizar la actividad Splash
        finish()
    }
}
