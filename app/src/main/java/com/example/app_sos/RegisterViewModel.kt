package com.example.app_sos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registro exitoso
                        // Puedes actualizar LiveData o hacer otra cosa aquí
                    } else {
                        // Maneja el error
                    }
                }
        } else {
            // Maneja el caso en que los campos están vacíos
        }
    }
}