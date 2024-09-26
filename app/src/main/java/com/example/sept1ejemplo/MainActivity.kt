package com.example.sept1ejemplo

import com.google.android.material.appbar.MaterialToolbar
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var editTextNombre: EditText
    private lateinit var editTextNota: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        Log.d("MainActivity", "onCreate: Actividad principal iniciada")
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextNota = findViewById(R.id.editTextNota)

        val btnLimpiar: Button = findViewById(R.id.btnLimpiar)
        val btnFirmar: Button = findViewById(R.id.btnFirmar)
        val btnIrActivityTres: Button = findViewById(R.id.btnIrActivityTres)

        // Verificar si existe un docente en la base de datos
        lifecycleScope.launch {
            val docenteExists = withContext(Dispatchers.IO) {
                database.registroDao().getDocente()
            }
            if (docenteExists == null) {
                // Si no existe, redirigir a la actividad RegistroDocente
                val intent = Intent(this@MainActivity, RegistroDocente::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnLimpiar.setOnClickListener {
            Log.d("MainActivity", "btnLimpiar: Limpiando campos")
            editTextNombre.text.clear()
            editTextNota.text.clear()
        }

        btnFirmar.setOnClickListener {
            Log.d("MainActivity", "btnFirmar: Iniciando FirmaActivity")
            val intent = Intent(this, FirmaActivity::class.java)
            intent.putExtra("nombre", editTextNombre.text.toString())
            intent.putExtra("nota", editTextNota.text.toString())
            startActivity(intent)
        }

        btnIrActivityTres.setOnClickListener {
            Log.d("MainActivity", "btnIrActivityTres: Iniciando ActivityTres")
            val intent = Intent(this, ActivityTres::class.java)
            startActivity(intent)
        }
    }
}
