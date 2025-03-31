package com.example.app_sos.activities

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_sos.R
import com.example.app_sos.adapters.ReportAdapter
import com.example.app_sos.models.Report
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private val reportsList = mutableListOf<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        recyclerView = findViewById(R.id.recyclerViewReports)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(reportsList)
        recyclerView.adapter = adapter

        // Cargar los reportes desde el servidor
        LoadReportsTask().execute()
    }

    // AsyncTask para cargar reportes desde el servidor
    private inner class LoadReportsTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            try {
                // URL del archivo PHP que obtiene los reportes
                val url = URL("http://www.app-sos-reportes.site/get_reports.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                // Leer la respuesta del servidor
                val responseCode = connection.responseCode
                return if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use {
                        it.readText()
                    }
                } else {
                    "Error de conexión: $responseCode"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return "Error: ${e.message}"
            }
        }

        override fun onPostExecute(result: String) {
            if (result.startsWith("Error")) {
                Toast.makeText(this@AdminPanelActivity, result, Toast.LENGTH_SHORT).show()
            } else {
                parseReports(result)
            }
        }
    }

    // Parsear la respuesta JSON desde el servidor
    private fun parseReports(jsonResponse: String) {
        try {
            val jsonArray = JSONArray(jsonResponse)
            for (i in 0 until jsonArray.length()) {
                val reportObject: JSONObject = jsonArray.getJSONObject(i)
                val nombre = reportObject.getString("nombre")
                val locacion = reportObject.getString("locacion")
                val descripcion = reportObject.getString("descripcion")
                val estado = reportObject.getString("estado")

                val report = Report(nombre, locacion, descripcion, estado)
                reportsList.add(report)
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al parsear los datos", Toast.LENGTH_SHORT).show()
        }
    }
}
