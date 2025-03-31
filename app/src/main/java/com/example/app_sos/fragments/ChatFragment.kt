package com.example.app_sos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.app_sos.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.URLEncoder

class ChatFragment : Fragment() {

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatTextView: TextView

    private val openAIChat = OpenAIChat() // Crear una instancia de OpenAIChat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Inicializar las vistas
        messageEditText = view.findViewById(R.id.etMessage)
        sendButton = view.findViewById(R.id.btnSend)
        chatTextView = view.findViewById(R.id.tvChat)

        // Configurar el botón de enviar
        sendButton.setOnClickListener {
            sendMessage()
        }

        return view
    }

    private fun sendMessage() {
        val message = messageEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            chatTextView.append("\nTú: $message") // Agrega el mensaje al chat
            messageEditText.text.clear() // Limpia el campo de entrada
            getChatbotResponse(message)
        }
    }

    private fun getChatbotResponse(message: String) {
        lifecycleScope.launch {
            val response = openAIChat.sendPrompt(message) // Llama a sendPrompt en la instancia de OpenAIChat
            chatTextView.append("\nChatbot: $response") // Muestra la respuesta del chatbot
        }
    }
}
