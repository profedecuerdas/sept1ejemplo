package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.AsignaturaEntity
import com.example.sept1ejemplo.database.DocenteEntity
import com.example.sept1ejemplo.databinding.ActivityRegistroDocenteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroDocente : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroDocenteBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroDocenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        binding.btnRegistrar.setOnClickListener {
            val nombreCompleto = binding.etNombreCompleto.text.toString()
            val asignaturas = binding.etAsignaturas.text.toString().split(",").map { it.trim() }

            if (nombreCompleto.isNotEmpty() && asignaturas.isNotEmpty()) {
                registrarDocente(nombreCompleto, asignaturas)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarDocente(nombreCompleto: String, asignaturas: List<String>) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val docenteId = database.registroDao().insertDocente(DocenteEntity(nombreCompleto = nombreCompleto))

                    asignaturas.forEach { asignatura ->
                        database.registroDao().insertAsignatura(AsignaturaEntity(nombre = asignatura, docenteId = docenteId.toInt()))
                    }
                }

                Toast.makeText(this@RegistroDocente, "Docente y asignaturas registrados con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegistroDocente, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistroDocente, "Error al registrar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}