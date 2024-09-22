package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.example.sept1ejemplo.FirmaActivity

class MainActivity : AppCompatActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextNota: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate: Actividad principal iniciada")
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        // Remove this line: setSupportActionBar(toolbar)

        // Instead, set the title directly on the toolbar
        toolbar.title = getString(R.string.app_name)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextNota = findViewById(R.id.editTextNota)

        val btnLimpiar: Button = findViewById(R.id.btnLimpiar)
        val btnFirmar: Button = findViewById(R.id.btnFirmar)

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
    }
}