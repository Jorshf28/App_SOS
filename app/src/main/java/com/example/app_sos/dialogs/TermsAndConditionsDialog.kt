package com.example.app_sos.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.app_sos.R
import com.example.app_sos.activities.MainActivity
import com.google.android.material.button.MaterialButton

class TermsAndConditionsDialog : DialogFragment() {

    interface TermsAndConditionsListener {
        fun onTermsAccepted()
        fun onTermsDeclined()
    }

    private var listener: TermsAndConditionsListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_terms_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar el contenido de los términos y condiciones
        val termsText = view.findViewById<TextView>(R.id.tv_terms_conditions)
        termsText.text = loadTermsAndConditions()

        // Configurar botones
        view.findViewById<MaterialButton>(R.id.btn_accept).setOnClickListener {
            listener?.onTermsAccepted()
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.btn_decline).setOnClickListener {
            listener?.onTermsDeclined()
            dismiss()
        }

        // Prevenir que el diálogo se cierre al tocar fuera
        isCancelable = false
    }

    private fun loadTermsAndConditions(): String {
        // Aquí puedes cargar el texto desde un archivo de recursos
        return requireContext().resources.openRawResource(R.raw.terms_and_conditions)
            .bufferedReader().use { it.readText() }
    }

    fun setTermsAndConditionsListener(listener: TermsAndConditionsListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "TermsAndConditionsDialog"
        private const val PREFS_NAME = "AppPreferences"
        private const val KEY_ACCEPTED_TERMS = "AcceptedTerms"

        // Método para verificar si los términos deben mostrarse
        fun shouldShowTerms(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return !sharedPreferences.getBoolean(KEY_ACCEPTED_TERMS, false)
        }

        // Guardar que los términos fueron aceptados
        fun saveAcceptedTerms(context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean(KEY_ACCEPTED_TERMS, true)
                apply()
            }
        }

        // Método estático para crear una nueva instancia del diálogo
        fun newInstance(): TermsAndConditionsDialog {
            return TermsAndConditionsDialog()
        }
    }
}
