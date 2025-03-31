package com.example.app_sos.models

data class Denuncia(
    val id: String = "",
    val nombre: String = "",
    val locacion: String = "",
    val descripcion: String = "",
    val estado: String = "Pendiente", // Puede ser: Pendiente, En proceso, Resuelta
    val fechaCreacion: String = "",
    val fotos: List<String> = emptyList(), // Lista de URLs de fotos asociadas a la denuncia
    val videos: List<String> = emptyList(), // Lista de URLs de videos asociados a la denuncia
    val latitud: Double = 0.0, // Latitud de la denuncia
    val longitud: Double = 0.0 // Longitud de la denuncia
)