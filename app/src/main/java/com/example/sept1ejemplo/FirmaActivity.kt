package com.example.sept1ejemplo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.RegistroEntity
import com.example.sept1ejemplo.databinding.ActivityFirmaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import com.example.sept1ejemplo.util.PdfGenerator

class FirmaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirmaBinding
    private lateinit var database: AppDatabase
    private lateinit var signatureBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val nota = intent.getStringExtra("nota") ?: ""
        val asignatura = intent.getStringExtra("asignatura") ?: ""

        // Configurar el listener para el SignaturePad
        binding.signaturePad.setOnSignedListener(object : com.github.gcacace.signaturepad.views.SignaturePad.OnSignedListener {
            override fun onSigned() {
                binding.btnAceptar.isEnabled = true
                signatureBitmap = binding.signaturePad.signatureBitmap
            }

            override fun onClear() {
                binding.btnAceptar.isEnabled = false
            }

            override fun onStartSigning() {}
        })

        // Botón Aceptar
        binding.btnAceptar.setOnClickListener {
            if (binding.signaturePad.isEmpty) {
                Toast.makeText(this, "Por favor, firme antes de aceptar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Redimensionar y recortar la firma
            val resizedSignature = resizeBitmap(signatureBitmap, 600, 300)
            val croppedSignature = cropSignature(resizedSignature)

            // Obtener el nombre del docente desde la base de datos y generar PDF
            obtenerNombreDocente { nombreDocente ->
                if (nombreDocente != null) {
                    saveSignature(croppedSignature, nombreDocente, nombre, nota, asignatura)
                } else {
                    Toast.makeText(this, "Error: Docente no registrado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón Limpiar
        binding.btnLimpiar.setOnClickListener {
            binding.signaturePad.clear()
        }
    }

    private fun resizeBitmap(original: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = original.width
        val height = original.height
        val aspectRatio = width.toFloat() / height.toFloat()

        var newWidth = maxWidth
        var newHeight = maxHeight

        if (width > height) {
            newHeight = (newWidth / aspectRatio).toInt()
        } else {
            newWidth = (newHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    }

    private fun cropSignature(signatureBitmap: Bitmap): Bitmap {
        val width = signatureBitmap.width
        val height = signatureBitmap.height
        var minX = width
        var minY = height
        var maxX = -1
        var maxY = -1

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = signatureBitmap.getPixel(x, y)
                if (pixel != Color.WHITE) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }

        return if (maxX == -1 || maxY == -1) {
            signatureBitmap
        } else {
            Bitmap.createBitmap(signatureBitmap, minX, minY, maxX - minX + 1, maxY - minY + 1)
        }
    }

    private fun obtenerNombreDocente(callback: (String?) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val docente = database.registroDao().getDocente()
            withContext(Dispatchers.Main) {
                if (docente != null) {
                    callback(docente.nombreCompleto)
                } else {
                    callback(null)
                }
            }
        }
    }

    private fun saveSignature(signatureBitmap: Bitmap, nombreDocente: String, nombre: String, nota: String, asignatura: String) {
        val stream = ByteArrayOutputStream()
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val signatureBytes = stream.toByteArray()
        val signatureBase64 = Base64.encodeToString(signatureBytes, Base64.DEFAULT)

        val timestamp = System.currentTimeMillis()

        val registro = RegistroEntity(
            nombresApellidos = nombre,
            nota = nota,
            asignatura = asignatura,
            timestamp = timestamp,
            firmaBase64 = signatureBase64
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val registroId = database.registroDao().insertRegistro(registro)
            val updatedRegistro = registro.copy(id = registroId.toInt())

            // Generar el PDF pasando el nombre del docente y la asignatura
            val pdfFile = PdfGenerator.generatePdf(this@FirmaActivity, updatedRegistro, signatureBitmap, nombreDocente)

            launch(Dispatchers.Main) {
                val intent = Intent(this@FirmaActivity, MensajeExito::class.java).apply {
                    putExtra("NOMBRES_APELLIDOS", nombre)
                    putExtra("NOTA", nota)
                    putExtra("ASIGNATURA", asignatura)
                    putExtra("TIMESTAMP", timestamp)
                    putExtra("REGISTRO_ID", registroId.toInt())
                    putExtra("SIGNATURE_SIZE", signatureBase64.toByteArray().size)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}