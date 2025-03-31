package com.example.app_sos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_sos.R
import com.example.app_sos.models.Report

class ReportAdapter(private val reportList: List<Report>) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.bind(report)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reportNameTextView: TextView = itemView.findViewById(R.id.reportName)
        private val reportLocationTextView: TextView = itemView.findViewById(R.id.reportLocation)
        private val reportDescriptionTextView: TextView = itemView.findViewById(R.id.reportDescription)
        private val reportStatusTextView: TextView = itemView.findViewById(R.id.reportStatus)

        fun bind(report: Report) {
            reportNameTextView.text = report.nombre
            reportLocationTextView.text = report.locacion
            reportDescriptionTextView.text = report.descripcion
            reportStatusTextView.text = report.estado
        }
    }
}

