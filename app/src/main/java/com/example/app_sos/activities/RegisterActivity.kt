package com.example.app_sos.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_sos.activities.LoginActivity
import com.example.app_sos.activities.MainActivity
import com.example.app_sos.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var emailTextWatcher: TextWatcher
    private val emailDomains = arrayOf("@gmail.com", "@hotmail.com")
    private var currentEmailPrefix = ""
    private var profilePicUri: Uri? = null
    private var frontPicUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEmailDomainSpinner()
        setupEmailListeners()
        setupButtonListeners()
    }

    private fun setupEmailDomainSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            emailDomains.map { it.substring(1) }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDomain.adapter = adapter

        binding.spinnerDomain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateFullEmail()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupEmailListeners() {
        emailTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentEmailPrefix = s.toString().substringBefore("@")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etEmail.addTextChangedListener(emailTextWatcher)
    }

    private fun getFullEmail(): String {
        val selectedDomain = emailDomains[binding.spinnerDomain.selectedItemPosition]
        return "$currentEmailPrefix$selectedDomain"
    }


    private fun setupButtonListeners() {
        binding.btnRegister.setOnClickListener {
            val email = getFullEmail()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val dni = binding.etDNI.text.toString().trim()

            if (validateRegistrationInput(email, password, confirmPassword, dni)) {
                registerUser(email, password, dni)
            }
        }

        binding.btnAlreadyRegistered.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateRegistrationInput(
        email: String,
        password: String,
        confirmPassword: String,
        dni: String
    ): Boolean {

        when {
            email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dni.isEmpty() -> {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return false
            }
            password != confirmPassword -> {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return false
            }
            dni.length != 8 || !dni.all { it.isDigit() } -> {
                Toast.makeText(this, "Ingresa un DNI válido", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }



    private fun updateFullEmail() {
        val selectedDomain = emailDomains[binding.spinnerDomain.selectedItemPosition]
        val newEmail = if (currentEmailPrefix.isNotEmpty()) {
            "$currentEmailPrefix$selectedDomain"
        } else {
            ""
        }


        val currentText = binding.etEmail.text.toString()
        if (currentText != newEmail) {
            binding.etEmail.removeTextChangedListener(emailTextWatcher)
            binding.etEmail.setText(currentEmailPrefix)
            binding.etEmail.setSelection(currentEmailPrefix.length)
            binding.etEmail.addTextChangedListener(emailTextWatcher)
        }
    }

    private fun registerUser(email: String, password: String, dni: String) {
        lifecycleScope.launch {
            try {
                binding.btnRegister.isEnabled = false
                val result = withContext(Dispatchers.IO) {
                    registerUserInBackground(email, password, dni)
                }

                when {
                    result == "Registro exitoso" -> {
                        Toast.makeText(this@RegisterActivity, result, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }
                    else -> Toast.makeText(this@RegisterActivity, result, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnRegister.isEnabled = true
            }
        }
    }

    private suspend fun registerUserInBackground(email: String, password: String, dni: String): String {
        return withContext(Dispatchers.IO) {
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL("http://www.app-sos-reportes.site/register.php")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.apply {
                    requestMethod = "POST"
                    doOutput = true
                    connectTimeout = 10000  // Timeout de 10 segundos
                    readTimeout = 10000     // Timeout de lectura de 10 segundos
                }

                val postData = "email=$email&password=$password&dni=$dni"
                urlConnection.outputStream.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(postData)
                        writer.flush()
                    }
                }

                when (urlConnection.responseCode) {
                    HttpURLConnection.HTTP_OK -> urlConnection.inputStream.bufferedReader().readText()
                    else -> "Error en la conexión: ${urlConnection.responseCode}"
                }
            } catch (e: Exception) {
                "Error: ${e.message}"
            } finally {
                urlConnection?.disconnect()
            }
        }
    }

    companion object {
        const val REQUEST_PROFILE_PIC = 1
        const val REQUEST_FRONT_PIC = 2
    }
}




