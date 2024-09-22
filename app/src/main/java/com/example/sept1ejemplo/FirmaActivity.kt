package com.example.sept1ejemplo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.RegistroEntity
import com.example.sept1ejemplo.databinding.ActivityFirmaBinding
import com.example.sept1ejemplo.util.PdfGenerator
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import android.util.Base64
import java.io.ByteArrayOutputStream

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

        // Configurar el listener para el SignaturePad
        binding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onSigned() {
                // Habilitar el botón Aceptar cuando hay una firma
                binding.btnAceptar.isEnabled = true
                signatureBitmap = binding.signaturePad.signatureBitmap
            }

            override fun onStartSigning() {
                // Opcional: manejar cuando se comienza a firmar
            }

            override fun onClear() {
                // Deshabilitar el botón Aceptar si la firma se borra
                binding.btnAceptar.isEnabled = false
            }
        })

        // Botón Limpiar
        binding.btnLimpiar.setOnClickListener {
            binding.signaturePad.clear()
        }

        // Botón Aceptar
        binding.btnAceptar.setOnClickListener {
            if (binding.signaturePad.isEmpty) {
                Toast.makeText(this, "Por favor, firme antes de aceptar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convertir a bitmap monocromático
            val monoBitmap = convertToMonochrome(signatureBitmap)

            // Guardar la firma en la base de datos y generar PDF
            val stream = ByteArrayOutputStream()
            monoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val signatureBytes = stream.toByteArray()
            val signatureBase64 = Base64.encodeToString(signatureBytes, Base64.DEFAULT)
            val timestamp = System.currentTimeMillis()
            val registro = RegistroEntity(
                nombresApellidos = nombre,
                nota = nota,
                timestamp = timestamp,
                firmaBase64 = signatureBase64
            )

            lifecycleScope.launch(Dispatchers.IO) {
                // Insertar el registro en la base de datos y obtener el ID generado
                val registroId = database.registroDao().insertRegistro(registro)

                // Generar el PDF con el ID correcto
                val updatedRegistro = registro.copy(id = registroId.toInt())  // Actualizar el ID
                val pdfFile = PdfGenerator.generatePdf(this@FirmaActivity, updatedRegistro, monoBitmap)

                launch(Dispatchers.Main) {
                    // Pasar el ID del registro a ActivityDos
                    val intent = Intent(this@FirmaActivity, ActivityDos::class.java).apply {
                        putExtra("NOMBRES_APELLIDOS", nombre)
                        putExtra("NOTA", nota)
                        putExtra("TIMESTAMP", timestamp)
                        putExtra("REGISTRO_ID", registroId.toInt()) // Pasar el ID del registro
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    // Función para convertir el bitmap a monocromático
    private fun convertToMonochrome(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val monochromeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = originalBitmap.getPixel(x, y)
                val avg = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                val monoColor = if (avg < 128) Color.BLACK else Color.WHITE
                monochromeBitmap.setPixel(x, y, monoColor)
            }
        }
        return monochromeBitmap
    }
}
