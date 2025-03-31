package com.example.app_sos.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app_sos.R

import com.example.app_sos.databinding.FragmentHomeBinding
import com.example.app_sos.activities.MapsActivity
import com.example.app_sos.activities.RegisterActivity
import com.example.app_sos.activities.DenunciaActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Usar ViewBinding para inflar el layout
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Configurar la interfaz de usuario
        setupUI()

        return binding.root
    }

    private fun setupUI() {
        // Setear el texto de bienvenida
        //binding.tvWelcome.text = getString(R.string.welcome_message)

        // Lógica para registrar una denuncia
        /*binding.btnRegUsuario.setOnClickListener {
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
        }*/

        // Lógica para ver las denuncias
        binding.btnRegReporte.setOnClickListener {
            val intent = Intent(requireContext(), DenunciaActivity::class.java)
            startActivity(intent)
        }

        // Lógica para ver el mapa de denuncias
        /*binding.btnMap.setOnClickListener {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            startActivity(intent)
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

