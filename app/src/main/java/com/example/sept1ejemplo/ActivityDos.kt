package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sept1ejemplo.databinding.ActivityDosBinding
import java.text.SimpleDateFormat
import java.util.*

class ActivityDos : AppCompatActivity() {

    private lateinit var binding: ActivityDosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener los datos del Intent
        val nombresApellidos = intent.getStringExtra("NOMBRES_APELLIDOS") ?: ""
        val nota = intent.getStringExtra("NOTA") ?: ""
        val timestamp = intent.getLongExtra("TIMESTAMP", 0L)
        val registroId = intent.getIntExtra("REGISTRO_ID", -1) // Recibir el ID del registro

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(timestamp)

        // Mostrar el mensaje con el ID del registro
        binding.textViewMensaje.text = "Se registró correctamente en la base de datos, y se creó el documento .pdf del registro correspondiente (Registro #$registroId)."

        binding.buttonVolverMain.setOnClickListener {
            finish()
        }

        binding.buttonIrTres.setOnClickListener {
            startActivity(Intent(this, ActivityTres::class.java))
        }
    }
}
