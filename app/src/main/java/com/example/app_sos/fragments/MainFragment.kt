package com.example.app_sos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app_sos.R
import com.example.app_sos.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Usar ViewBinding para inflar el layout
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        // Inicializa cualquier componente de la vista aquí
        setupUI()

        return view
    }

    private fun setupUI() {
        binding.btnRegUsuario.setOnClickListener {
            // Lógica para registrar una denuncia
        }

        binding.btnRegReporte.setOnClickListener {
            // Lógica para ver las denuncias
        }

        binding.btnMap.setOnClickListener {
            // Lógica para ver el mapa de denuncias
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}