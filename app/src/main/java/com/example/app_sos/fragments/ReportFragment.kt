package com.example.app_sos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_sos.R
import com.example.app_sos.adapters.ReportAdapter
import com.example.app_sos.models.Report

class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reportAdapter: ReportAdapter
    private var reportList: MutableList<Report> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_reports)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializa el adaptador
        reportAdapter = ReportAdapter(reportList)
        recyclerView.adapter = reportAdapter

        loadReports() // Cargar las denuncias (puedes sustituir esto por una llamada a Firestore)

        return view
    }

    private fun loadReports() {
        // Aquí puedes cargar las denuncias desde Firestore o una fuente de datos local
        // Para propósitos de ejemplo, se añaden denuncias simuladas
        reportList.add(Report("Reporte 1", "Ubicación 1", "Descripción del reporte 1", "Pendiente"))
        reportList.add(Report("Reporte 2", "Ubicación 2", "Descripción del reporte 2", "Resuelta"))

        reportAdapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }
}