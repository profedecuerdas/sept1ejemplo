package com.example.sept1ejemplo.util

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.sept1ejemplo.database.RegistroEntity
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Image
import com.itextpdf.io.image.ImageDataFactory
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {
    fun generatePdf(context: Context, registro: RegistroEntity, signatureBitmap: Bitmap): File {
        val fileName = "registro${registro.timestamp}.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        PdfWriter(file).use { writer ->
            PdfDocument(writer).use { pdf ->
                Document(pdf).use { document ->

                    // Cargar la imagen del membrete desde drawable usando el contexto
                    val membreteBitmap = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("membrete", "drawable", context.packageName))
                    val streamMembrete = ByteArrayOutputStream()
                    membreteBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamMembrete)
                    val membreteBytes = streamMembrete.toByteArray()

                    // Crear una imagen iText a partir de los datos del bitmap del membrete
                    val membreteImage = Image(ImageDataFactory.create(membreteBytes))
                    membreteImage.scaleToFit(500f, 150f) // Ajustar el tamaño del membrete según tus necesidades

                    // Añadir el membrete al documento en la parte superior
                    document.add(membreteImage)

                    // Añadir el texto "Registro #<id>"
                    document.add(Paragraph("Registro #${registro.id}"))

                    // Añadir nombres y apellidos
                    document.add(Paragraph("Nombres y Apellidos: ${registro.nombresApellidos}"))

                    // Añadir nota
                    document.add(Paragraph("Nota: ${registro.nota}"))

                    // Convertir la firma Bitmap a un arreglo de bytes
                    val streamFirma = ByteArrayOutputStream()
                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamFirma)
                    val signatureBytes = streamFirma.toByteArray()

                    // Crear imagen a partir del Bitmap de la firma
                    val signatureImage = Image(ImageDataFactory.create(signatureBytes))
                    signatureImage.scaleToFit(100f, 100f)

                    // Añadir la firma al documento
                    document.add(signatureImage)

                    // Añadir la fecha y hora debajo de la firma
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val date = Date(registro.timestamp)
                    document.add(Paragraph("Fecha y Hora: ${dateFormat.format(date)}"))
                }
            }
        }

        return file
    }
}
