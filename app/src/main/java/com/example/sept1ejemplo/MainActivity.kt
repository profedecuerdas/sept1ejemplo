package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.AsignaturaEntity
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var editTextNombre: EditText
    private lateinit var editTextNota: EditText
    private lateinit var spinnerAsignaturas: Spinner
    private lateinit var asignaturas: List<AsignaturaEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        Log.d("MainActivity", "onCreate: Actividad principal iniciada")
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextNota = findViewById(R.id.editTextNota)
        spinnerAsignaturas = findViewById(R.id.spinnerAsignaturas)

        val btnLimpiar: Button = findViewById(R.id.btnLimpiar)
        val btnFirmar: Button = findViewById(R.id.btnFirmar)
        val btnIrActivityTres: Button = findViewById(R.id.btnIrActivityTres)

        lifecycleScope.launch {
            checkDocenteAndLoadAsignaturas()
        }

        btnLimpiar.setOnClickListener {
            Log.d("MainActivity", "btnLimpiar: Limpiando campos")
            editTextNombre.text.clear()
            editTextNota.text.clear()
            spinnerAsignaturas.setSelection(0)
        }

        btnFirmar.setOnClickListener {
            Log.d("MainActivity", "btnFirmar: Iniciando FirmaActivity")
            val intent = Intent(this, FirmaActivity::class.java)
            intent.putExtra("nombre", editTextNombre.text.toString())
            intent.putExtra("nota", editTextNota.text.toString())
            intent.putExtra("asignatura", spinnerAsignaturas.selectedItem.toString())
            startActivity(intent)
        }

        btnIrActivityTres.setOnClickListener {
            Log.d("MainActivity", "btnIrActivityTres: Iniciando ActivityTres")
            val intent = Intent(this, ActivityTres::class.java)
            startActivity(intent)
        }
    }

    private suspend fun checkDocenteAndLoadAsignaturas() {
        val docenteExists = withContext(Dispatchers.IO) {
            database.registroDao().getDocente()
        }
        if (docenteExists == null) {
            // Si no existe, redirigir a la actividad RegistroDocente
            val intent = Intent(this@MainActivity, RegistroDocente::class.java)
            startActivity(intent)
            finish()
        } else {
            // Si existe, cargar las asignaturas
            loadAsignaturas(docenteExists.id)
        }
    }

    private suspend fun loadAsignaturas(docenteId: Int) {
        asignaturas = withContext(Dispatchers.IO) {
            database.registroDao().getAsignaturasForDocente(docenteId)
        }
        val asignaturasNombres = asignaturas.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, asignaturasNombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        withContext(Dispatchers.Main) {
            spinnerAsignaturas.adapter = adapter
        }
    }
}