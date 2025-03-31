package com.example.app_sos.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.app_sos.R
import com.example.app_sos.databinding.ActivityDenunciaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.io.IOException

class DenunciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDenunciaBinding
    private var capturedPhotoUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_REQUEST_CODE = 1002



    // Agrega un array para las descripciones de denuncia
    private val tiposDenuncia = arrayOf("Robo", "Vandalismo", "Acoso", "Fraude")
    private val descripcionDenuncia = arrayOf(
        "Robo: Sustracción ilegal de bienes.",
        "Vandalismo: Destrucción o daño de propiedades.",
        "Acoso: Comportamiento repetitivo de hostigamiento.",
        "Fraude: Engaño para obtener beneficio economic."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDenunciaBinding.inflate(layoutInflater)
        setContentView(binding.root)




        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // Configurar los botones
        binding.btnCamera.setOnClickListener { takePhoto() }
        binding.btnLocation.setOnClickListener { fetchCurrentLocation() }
        binding.btnSubmitReport.setOnClickListener { submitReport() }

        // Configurar el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposDenuncia)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReportType.adapter = adapter

        // Manejar la selección del Spinner
        binding.spinnerReportType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Actualizar el TextView con la descripción correspondiente
                binding.tvDescription.text = descripcionDenuncia[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.tvDescription.text = "Selecciona un tipo de denuncia"
            }
        }

        // Verificar y solicitar permisos
        checkPermissions()

    }



    private fun takePhoto() {
        // Check camera permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            return
        }

        try {
            val photoFile: File? = createImageFile()
            photoFile?.let {
                capturedPhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
                capturedPhotoUri?.let { uri ->
                    val photoCaptureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                        if (success) {
                            Log.d("DenunciaActivity", "Photo captured successfully")
                            binding.ivPreviewImage.setImageURI(uri)
                        } else {
                            Log.e("DenunciaActivity", "Failed to capture photo")
                            Toast.makeText(this, "Error al capturar la foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                    photoCaptureLauncher.launch(uri)
                }
            }
        } catch (e: Exception) {
            Log.e("DenunciaActivity", "Error in takePhoto: ${e.message}", e)
            Toast.makeText(this, "Error al iniciar la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        // Crear un nombre de archivo único
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", // prefijo
            ".jpg",               // sufijo
            storageDir            // directorio
        ).apply {
            // Guardar un path para usar con ACTION_VIEW intents
            capturedPhotoUri = Uri.fromFile(this)
        }
    }

    private fun fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                binding.etLocation.setText("Lat: $latitude, Lon: $longitude")
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener la ubicación: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = "231149040340244280571x124148" // Reemplaza con tu clave de API
                val url = URL("https://geocode.xyz/$latitude,$longitude?geoit=xml&auth=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseStream = connection.inputStream
                    val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                    val builder = factory.newDocumentBuilder()
                    val document = builder.parse(responseStream)
                    document.documentElement.normalize()

                    val city = document.getElementsByTagName("city").item(0)?.textContent ?: "Unknown city"
                    val state = document.getElementsByTagName("state").item(0)?.textContent ?: "Unknown state"
                    val country = document.getElementsByTagName("country").item(0)?.textContent ?: "Unknown country"

                    "$city, $state, $country"
                } else {
                    "Error al obtener la dirección: ${connection.responseCode}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
        }
    }

    private fun submitReport() {
        val locacion = binding.etLocation.text.toString().trim()

        if (locacion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrl = capturedPhotoUri?.toString() ?: ""
        val tipoDenuncia = binding.spinnerReportType.selectedItem.toString()


        Log.d("DenunciaActivity", "Enviando datos: locacion=$locacion, tipoDenuncia=$tipoDenuncia, imageUrl=$imageUrl")

        lifecycleScope.launch {
            val response = saveDenuncia(locacion, tipoDenuncia, imageUrl)
            Toast.makeText(this@DenunciaActivity, response, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveDenuncia(locacion: String, tipoDenuncia: String, imageUrl: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://www.app-sos-reportes.site/save_denuncia.php") // URL de tu servidor
                val postData = "locacion=$locacion&tipoDenuncia=$tipoDenuncia&imageUrl=${Uri.encode(imageUrl)}"

                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                }

                connection.outputStream.use { it.write(postData.toByteArray()) }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Error del servidor: ${connection.responseCode}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
        }
    }

    private fun checkPermissions() {
        val permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!allPermissionsGranted) {
                Toast.makeText(this, "Se requieren permisos para continuar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}





