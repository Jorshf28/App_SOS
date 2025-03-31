package com.example.app_sos.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.app_sos.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Inicializa el fragmento del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Solicita permisos de ubicación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true

        // Obtiene la ubicación actual del dispositivo
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                    // Carga las denuncias cercanas desde el servidor
                    loadNearbyReports(location)
                }
            }
    }

    // Método para cargar denuncias cercanas desde el servidor MySQL
    private fun loadNearbyReports(currentLocation: Location) {
        FetchReportsTask().execute(currentLocation)
    }

    // AsyncTask para obtener los reportes desde el servidor mediante una solicitud HTTP
    private inner class FetchReportsTask : AsyncTask<Location, Void, String>() {

        override fun doInBackground(vararg params: Location): String? {
            val currentLocation = params[0]
            val urlString = "http://www.app-sos-reportes.site/get_reports.php" // Cambia esto por la URL de tu script PHP
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            return try {
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?

                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }

                    bufferedReader.close()
                    stringBuilder.toString()
                } else {
                    null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                connection.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                try {
                    val reportsArray = JSONArray(result)
                    for (i in 0 until reportsArray.length()) {
                        val report = reportsArray.getJSONObject(i)
                        val lat = report.getDouble("latitude")
                        val lng = report.getDouble("longitude")
                        val description = report.getString("description")

                        // Agregar marcador en el mapa
                        val reportLocation = LatLng(lat, lng)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(reportLocation)
                                .title(description)
                        )
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MapsActivity, "Error al parsear los reportes", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MapsActivity, "Error al cargar reportes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

