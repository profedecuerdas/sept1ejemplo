package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.databinding.ActivityDosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ActivityDos : AppCompatActivity() {

    private lateinit var binding: ActivityDosBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        // Obtener los datos del Intent
        val nombresApellidos = intent.getStringExtra("NOMBRES_APELLIDOS") ?: ""
        val nota = intent.getStringExtra("NOTA") ?: ""
        val asignatura = intent.getStringExtra("ASIGNATURA") ?: ""
        val timestamp = intent.getLongExtra("TIMESTAMP", 0L)
        val registroId = intent.getIntExtra("REGISTRO_ID", -1)
        val signatureSize = intent.getIntExtra("SIGNATURE_SIZE", 0)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(timestamp)

        // Mostrar el mensaje con el ID del registro, el tamaño de la firma y la asignatura
        binding.textViewMensaje.text = "Se registró correctamente en la base de datos, " +
                "se creó el documento .pdf del registro correspondiente (Registro #$registroId). " +
                "Tamaño de la firma: $signatureSize bytes. " +
                "Asignatura: $asignatura"

        binding.buttonVolverMain.setOnClickListener {
            finish()
        }

        binding.buttonIrTres.setOnClickListener {
            startActivity(Intent(this, ActivityTres::class.java))
        }

        // Cargar y mostrar el nombre del docente
        lifecycleScope.launch {
            val docente = withContext(Dispatchers.IO) {
                database.registroDao().getDocente()
            }
            docente?.let {
                binding.textViewDocenteName.text = "Docente: ${it.nombreCompleto}"
            }
        }
    }
}