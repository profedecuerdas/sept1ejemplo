package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.AsignaturaEntity
import com.example.sept1ejemplo.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding  // Usamos View Binding
    private lateinit var database: AppDatabase
    private lateinit var asignaturas: List<AsignaturaEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        Log.d("MainActivity", "onCreate: Actividad principal iniciada")
        val toolbar: MaterialToolbar = binding.toolbar
        toolbar.title = getString(R.string.app_name)

        // Inicializar componentes con View Binding
        val btnLimpiar: Button = binding.btnLimpiar
        val btnFirmar: Button = binding.btnFirmar
        val btnIrActivityTres: Button = binding.btnIrActivityTres

        lifecycleScope.launch {
            checkDocenteAndLoadAsignaturas()
        }

        btnLimpiar.setOnClickListener {
            Log.d("MainActivity", "btnLimpiar: Limpiando campos")
            binding.editTextNombre.text.clear()
            binding.editTextNota.text.clear()
            binding.spinnerAsignaturas.setSelection(0)
        }

        btnFirmar.setOnClickListener {
            Log.d("MainActivity", "btnFirmar: Iniciando FirmaActivity")
            val intent = Intent(this, FirmaActivity::class.java)
            intent.putExtra("nombre", binding.editTextNombre.text.toString())
            intent.putExtra("nota", binding.editTextNota.text.toString())
            intent.putExtra("asignatura", binding.spinnerAsignaturas.selectedItem.toString())
            startActivity(intent)
        }

        btnIrActivityTres.setOnClickListener {
            Log.d("MainActivity", "btnIrActivityTres: Iniciando ActivityTres")
            val intent = Intent(this, ActivityMuestraRegistros::class.java)
            startActivity(intent)
        }
    }

    private suspend fun checkDocenteAndLoadAsignaturas() {
        val docente = withContext(Dispatchers.IO) {
            database.registroDao().getDocente()
        }
        if (docente == null) {
            val intent = Intent(this@MainActivity, RegistroDocente::class.java)
            startActivity(intent)
            finish()
        } else {
            withContext(Dispatchers.Main) {
                binding.textViewDocenteName.text = "Docente: ${docente.nombreCompleto}"
            }
            loadAsignaturas(docente.id)
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
            binding.spinnerAsignaturas.adapter = adapter
        }
    }
}
