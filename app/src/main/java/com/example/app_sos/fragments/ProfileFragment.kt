package com.example.app_sos.fragments

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.app_sos.R
import com.example.app_sos.activities.LoginActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var reportsCountTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inicializar las vistas
        usernameTextView = view.findViewById(R.id.tvUsername)
        emailTextView = view.findViewById(R.id.tvEmail)
        reportsCountTextView = view.findViewById(R.id.tvReportsCount)
        logoutButton = view.findViewById(R.id.btnLogout)

        // Recuperar el email del usuario autenticado desde SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences?.getString("user_email", null)

        // Si el email está disponible, cargar el perfil del usuario
        if (userEmail != null) {
            loadUserProfile(userEmail)
        } else {
            Toast.makeText(context, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show()
        }

        // Manejar el botón de Cerrar Sesión
        logoutButton.setOnClickListener {
            performLogout()
        }

        return view
    }

    private fun loadUserProfile(email: String) {
        // Ejecutar una tarea en segundo plano para hacer la solicitud al servidor remoto
        FetchUserProfileTask().execute(email)
    }

    // Clase para manejar la solicitud HTTP en segundo plano
    inner class FetchUserProfileTask : AsyncTask<String, Void, String?>() {

        override fun doInBackground(vararg params: String?): String? {
            val email = params[0]
            // URL de tu servidor en InfinityFree, reemplaza con la URL de tu servidor
            val urlString = "http://www.app-sos-reportes.site/get_user_profile.php?email=$email"

            return try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()
                connection.disconnect()
                response.toString()

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                try {
                    // Parsear la respuesta JSON
                    val jsonObject = JSONObject(result)
                    val username = jsonObject.getString("username")
                    val email = jsonObject.getString("email")
                    val reportCount = jsonObject.getInt("report_count")

                    // Actualizar las vistas con los datos recibidos
                    usernameTextView.text = username
                    emailTextView.text = email
                    reportsCountTextView.text = "Reportes realizados: $reportCount"
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Error al obtener datos del servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogout() {
        // Limpiar las preferencias compartidas o cerrar sesión en el servidor remoto
        val sharedPreferences = activity?.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.clear()?.apply()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }
}







