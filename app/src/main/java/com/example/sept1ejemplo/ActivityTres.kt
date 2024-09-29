package com.example.sept1ejemplo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.databinding.ActivityTresBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ActivityTres : AppCompatActivity() {

    private lateinit var binding: ActivityTresBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        binding.buttonVolverMain.setOnClickListener {
            finish()
        }

        binding.buttonCerrar.setOnClickListener {
            finishAffinity()
        }

        // Cargar y mostrar los registros con sus índices
        cargarRegistros()

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

    private fun cargarRegistros() {
        lifecycleScope.launch {
            val registros = withContext(Dispatchers.IO) {
                database.registroDao().getAllRegistros()  // Obtener los registros de la base de datos
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            // Construir la cadena de texto que muestra cada registro con su ID (índice)
            val registrosText = registros.joinToString("\n\n") { registro ->
                "Registro #${registro.id}\n" +  // Mostrar el ID del registro
                        "Nombres y Apellidos: ${registro.nombresApellidos}\n" +
                        "Asignatura: ${registro.asignatura}\n" +
                        "Nota: ${registro.nota}\n" +
                        "Fecha: ${dateFormat.format(Date(registro.timestamp))}"
            }

            // Mostrar el resultado en el TextView
            binding.textViewRegistros.text = registrosText
        }
    }
}