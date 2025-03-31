package com.example.app_sos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView

import android.widget.Spinner
import androidx.fragment.app.Fragment

class CreateReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_create_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usar findViewById para obtener los elementos del layout
        val spTipoDenuncia = view.findViewById<Spinner>(R.id.spTipoDenuncia)
        val tvDescripcionTipoDenuncia = view.findViewById<TextView>(R.id.tvDescripcionTipoDenuncia)
        val etLocacion = view.findViewById<EditText>(R.id.etLocation)
        val etDescripcion = view.findViewById<EditText>(R.id.spinnerDomain)
        val btnEnviar = view.findViewById<Button>(R.id.btnEnviar)

        val tiposDenuncias = arrayOf("Robo", "Violencia", "Daños a propiedad", "Otros")
        val descripciones = arrayOf(
            "Denuncias relacionadas con robos o asaltos.",
            "Reportes de actos de violencia física o verbal.",
            "Incidentes relacionados con daños a bienes.",
            "Otros tipos de denuncias no especificadas."
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tiposDenuncias
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoDenuncia.adapter = adapter

        spTipoDenuncia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tvDescripcionTipoDenuncia.text = descripciones[position]
                tvDescripcionTipoDenuncia.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                tvDescripcionTipoDenuncia.visibility = View.GONE
            }
        }

        btnEnviar.setOnClickListener {
            val locacion = etLocacion.text.toString()
            val descripcion = etDescripcion.text.toString()

            if (locacion.isNotEmpty() && descripcion.isNotEmpty()) {
                // Aquí va el código para enviar la denuncia
                Toast.makeText(requireContext(), "Denuncia enviada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
