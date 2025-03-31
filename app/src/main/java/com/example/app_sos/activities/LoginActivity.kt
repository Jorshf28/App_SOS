package com.example.app_sos.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import android.view.View
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.app_sos.R
import com.example.app_sos.databinding.ActivityLoginBinding
import com.example.app_sos.activities.RegisterActivity
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val emailDomains = arrayOf("@gmail.com", "@hotmail.com")
    private var selectedDomain = "@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            emailDomains.map { it.substring(1) }
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.spinnerDomain.adapter = adapter

        binding.spinnerDomain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDomain = emailDomains[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedDomain = emailDomains[0]
            }
        }


        // Manejar inicio de sesión
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val dni = binding.etDNI.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val fullEmail ="$email$selectedDomain"

            if (email.isEmpty() || password.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de correo electrónico
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar DNI (8 dígitos)
            if (dni.length != 8 || !dni.all { it.isDigit() }) {
                Toast.makeText(this, "Ingresa un DNI válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ejecutar la tarea de inicio de sesión en el servidor
            LoginUserTask().execute(email, password, dni)
        }

        // Navegar a la pantalla de registro
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private inner class LoginUserTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            val email = params[0]
            val dni = params[1]
            val password = params[2]

            try {
                // URL del archivo PHP en tu servidor
                val url = URL("http://www.app-sos-reportes.site/login.php")
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true

                // Enviar datos al archivo PHP
                val postData = "email=$email&password=$password&dni=$dni"
                Log.d("LoginData", "Enviando datos: $postData")
                val outputStreamWriter = OutputStreamWriter(urlConnection.outputStream)
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()

                // Leer la respuesta del servidor
                val responseCode = urlConnection.responseCode
                return if (responseCode == HttpURLConnection.HTTP_OK) {
                    urlConnection.inputStream.bufferedReader().readText()
                } else {
                    "Error en la conexión: $responseCode"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return "Error: ${e.message}"
            }
        }

        override fun onPostExecute(result: String) {
            try {
                val jsonResponse = JSONObject(result)
                val success = jsonResponse.getBoolean("success")
                val message = jsonResponse.getString("message")

                // Mostrar el mensaje del servidor
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()

                if (success) {
                    // Redirigir al MainActivity si el login es exitoso
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




