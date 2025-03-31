package com.example.app_sos.adapters

import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_sos.R
import com.example.app_sos.models.Report
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class AdminAdapter(
    private val reportList: List<Report>,
    private val onUpdateStatusClick: (report: Report) -> Unit
) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReportName: TextView = itemView.findViewById(R.id.tvReportName)
        val tvReportLocation: TextView = itemView.findViewById(R.id.tvReportLocation)
        val tvReportStatus: TextView = itemView.findViewById(R.id.tvReportStatus)
        val btnUpdateStatus: Button = itemView.findViewById(R.id.btnUpdateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_report, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val report = reportList[position]
        holder.tvReportName.text = report.nombre
        holder.tvReportLocation.text = report.locacion
        holder.tvReportStatus.text = report.estado

        // Actualización de estado al hacer clic en el botón
        holder.btnUpdateStatus.setOnClickListener {
            onUpdateStatusClick(report)
        }
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    // Actualización del estado en la base de datos MySQL a través de una solicitud HTTP
    fun updateReportStatus(reportId: String, newStatus: String) {
        UpdateReportStatusTask(reportId, newStatus).execute()
    }

    // AsyncTask para enviar una solicitud HTTP y actualizar el estado de la denuncia
    private inner class UpdateReportStatusTask(private val reportId: String, private val newStatus: String) :
        AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val urlString = "http://www.app-sos-reportes.site/update_report_status.php"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            return try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Crear un objeto JSON con los parámetros necesarios
                val jsonParam = JSONObject()
                jsonParam.put("reportId", reportId)
                jsonParam.put("newStatus", newStatus)

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(jsonParam.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()

                val responseCode = connection.responseCode
                responseCode == HttpURLConnection.HTTP_OK

            } catch (e: Exception) {
                e.printStackTrace()
                false
            } finally {
                connection.disconnect()
            }
        }

        override fun onPostExecute(success: Boolean) {
            if (success) {
                // La actualización fue exitosa, se puede notificar a la UI si es necesario
            } else {
                // Manejar el error en la UI si es necesario
            }
        }
    }
}
