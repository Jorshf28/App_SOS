package com.example.app_sos.fragments

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder

class OpenAIChat {

    private val client = OkHttpClient()

    // Cambia este valor por la URL de tu servidor o la API de OpenAI
    private val apiUrl = "https://api.openai.com/v1/chat/completions"

    suspend fun sendPrompt(message: String): String {
        // Define el prompt a enviar
        val prompt = message

        // Reemplaza esto por una variable de entorno o mecanismo seguro de almacenamiento
        val apiKey = "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"

        // Configuración de la solicitud POST en formato JSON
        val jsonPayload = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [{"role": "user", "content": "$prompt"}]
            }
        """.trimIndent()

        // Define el tipo de contenido JSON para la solicitud
        val requestBody = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())

        // Construye la solicitud POST
        val request = Request.Builder()
            .url(apiUrl)
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        try {
            // Ejecuta la solicitud
            val response: Response = client.newCall(request).execute()
            val responseData = response.body?.string()

            // Imprimir la respuesta completa para depuración
            println("Response Data: $responseData")

            return if (response.isSuccessful) {
                parseResponse(responseData)
            } else {
                "HTTP Error: ${response.code} - ${response.message}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Request failed: ${e.message ?: "Unknown error"}"
        }
    }

    private fun parseResponse(responseData: String?): String {
        return if (responseData != null) {
            // Intentar analizar como JSON
            try {
                val data = Gson().fromJson(responseData, Map::class.java)
                val choices = data["choices"] as? List<Map<String, Any>>
                choices?.get(0)?.get("message")?.toString() ?: "No response"
            } catch (e: Exception) {
                "Error parsing response: ${e.message}"
            }
        } else {
            "Empty response"
        }
    }

    // Función para codificar una cadena como URL
    private fun encodeURIComponent(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
    }
}