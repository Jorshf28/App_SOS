package com.example.app_sos.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.app_sos.R
import com.example.app_sos.fragments.HomeFragment
import com.example.app_sos.fragments.ReportFragment
import com.example.app_sos.fragments.ChatFragment
// import com.example.app_sos.fragments.ProfileFragment // Comentado
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

import com.example.app_sos.dialogs.TermsAndConditionsDialog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FirebaseApp.initializeApp(this)

        // Verificar si debemos mostrar los términos y condiciones
        if (TermsAndConditionsDialog.shouldShowTerms(this)) {
            val termsDialog = TermsAndConditionsDialog.newInstance()
            termsDialog.show(supportFragmentManager, TermsAndConditionsDialog.TAG)
        }

        // Verificar si la app tiene permiso de notificación (solo necesario para Android 13 y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Solicitar el permiso de notificaciones
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Establecer Home como el fragmento predeterminado al crear la actividad
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                /*R.id.nav_settings -> {
                    loadFragment(ReportFragment())
                    true
                }*/
                /*R.id.nav_chat -> {
                    loadFragment(ChatFragment())
                    true
                }*/
                /*R.id.nav_profile -> { // Comentado
                    loadFragment(ProfileFragment()) // Comentado
                    true // Comentado
                }*/
                else -> false
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    private fun setupBottomNavigation(savedInstanceState: Bundle?) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Establecer Home como el fragmento predeterminado
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                /*R.id.nav_profile -> { // Comentado
                    loadFragment(ProfileFragment()) // Comentado
                    true // Comentado
                }*/
                else -> false
            }
        }
    }

    // Función para cargar fragmentos en la actividad
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de notificación concedido, puedes continuar
                Log.d("MainActivity", "Permiso de notificación concedido")
            } else {
                // Permiso de notificación denegado, maneja el caso
                Log.d("MainActivity", "Permiso de notificación denegado")
            }
        }
    }
}