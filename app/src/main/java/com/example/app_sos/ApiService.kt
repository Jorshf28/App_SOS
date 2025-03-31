package com.example.app_sos

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ApiService(private val requestQueue: RequestQueue) {

    fun enviarDenuncia(nombre: String, locacion: String, descripcion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val url = "https://tu-servidor.com/api.php"

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                onSuccess()
            },
            { error ->
                onError(error.message ?: "Error al enviar la denuncia")
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombre"] = nombre
                params["locacion"] = locacion
                params["descripcion"] = descripcion
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}