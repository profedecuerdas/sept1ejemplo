package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.DocenteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroDocente : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var editTextNombreDocente: EditText
    private lateinit var btnRegistrarDocente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_docente)

        database = AppDatabase.getDatabase(this)

        editTextNombreDocente = findViewById(R.id.editTextNombreDocente)
        btnRegistrarDocente = findViewById(R.id.btnRegistrarDocente)

        btnRegistrarDocente.setOnClickListener {
            val nombreDocente = editTextNombreDocente.text.toString().trim()

            if (nombreDocente.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Registrar docente en la base de datos
            lifecycleScope.launch(Dispatchers.IO) {
                val docente = DocenteEntity(nombreCompleto = nombreDocente) // Aquí corriges el nombre del parámetro
                database.registroDao().insertDocente(docente)

                // Volver a la MainActivity tras el registro
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistroDocente, "Docente registrado correctamente", Toast.LENGTH_SHORT).show()

                    // Lanzar MainActivity y finalizar RegistroDocente
                    val intent = Intent(this@RegistroDocente, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Finaliza RegistroDocente pero no cierra la aplicación
                }
            }
        }
    }
}
