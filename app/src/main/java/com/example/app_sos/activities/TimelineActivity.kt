package com.example.app_sos.activities

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_sos.R
import com.example.app_sos.adapters.TimelineAdapter
import com.example.app_sos.models.TimelineEvent
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TimelineActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimelineAdapter
    private val timelineEventsList = mutableListOf<TimelineEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        recyclerView = findViewById(R.id.recyclerViewTimeline)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TimelineAdapter(timelineEventsList)
        recyclerView.adapter = adapter

        loadTimelineEvents()

        // Otras funcionalidades relacionadas con la línea de tiempo
    }

    // Método para cargar eventos de la línea de tiempo desde el servidor MySQL
    private fun loadTimelineEvents() {
        FetchTimelineTask().execute()
    }

    // AsyncTask para obtener los eventos desde el servidor mediante una solicitud HTTP
    private inner class FetchTimelineTask : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val urlString = "http://www.app-sos-reportes.site/get_timeline.php" // Cambia esto por la URL de tu script PHP
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
                    val eventsArray = JSONArray(result)
                    for (i in 0 until eventsArray.length()) {
                        val eventObject = eventsArray.getJSONObject(i)
                        val date = eventObject.getString("date")
                        val event = eventObject.getString("event")
                        val description = eventObject.getString("description")

                        // Crear y agregar un evento de línea de tiempo a la lista
                        val timelineEvent = TimelineEvent(date, event, description)
                        timelineEventsList.add(timelineEvent)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(this@TimelineActivity, "Error al parsear los eventos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@TimelineActivity, "Error al cargar los eventos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
