package com.example.sept1ejemplo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.sept1ejemplo.database.RegistroEntity
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.io.image.ImageDataFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    fun generatePdfFileName(registro: RegistroEntity): String {
        val words = registro.nombresApellidos.split(" ")
        val abbreviation = words.joinToString("_") { word ->
            word.take(3).capitalize(Locale.getDefault())
        }
        val dateFormat = SimpleDateFormat("dd-MMM-HH-mm", Locale.getDefault())
        val formattedTimestamp = dateFormat.format(Date(registro.timestamp))
        return "Reg${registro.id}_${abbreviation}_$formattedTimestamp.pdf"
    }

    fun generatePdf(context: Context, registro: RegistroEntity, signatureBitmap: Bitmap, nombreDocente: String): File {
        val fileName = generatePdfFileName(registro)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        PdfWriter(file).use { writer ->
            PdfDocument(writer).use { pdf ->
                Document(pdf).use { document ->

                    // Añadir membrete
                    val membreteBitmap = BitmapFactory.decodeResource(
                        context.resources,
                        context.resources.getIdentifier("membrete_optimizado", "drawable", context.packageName)
                    )
                    val streamMembrete = ByteArrayOutputStream()
                    membreteBitmap.compress(Bitmap.CompressFormat.JPEG, 85, streamMembrete)
                    val membreteBytes = streamMembrete.toByteArray()
                    val membreteImage = Image(ImageDataFactory.create(membreteBytes))
                    membreteImage.scaleToFit(500f, 150f)
                    document.add(membreteImage)

                    // Añadir datos al PDF
                    document.add(Paragraph("Registro #${registro.id}"))
                    document.add(Paragraph("Docente: $nombreDocente"))
                    document.add(Paragraph("Asignatura: ${registro.asignatura}"))

                    document.add(Paragraph("Nota: ${registro.nota}"))

                    // Convertir la firma a imagen y agregarla al PDF
                    val streamFirma = ByteArrayOutputStream()
                    signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, streamFirma)
                    val signatureBytes = streamFirma.toByteArray()
                    val signatureImage = Image(ImageDataFactory.create(signatureBytes))
                    signatureImage.scaleToFit(100f, 100f)
                    document.add(signatureImage)
                    document.add(Paragraph("Estudiante: ${registro.nombresApellidos}"))
                    // Añadir fecha y hora
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val date = Date(registro.timestamp)
                    document.add(Paragraph("Fecha y Hora: ${dateFormat.format(date)}"))
                }
            }
        }
        return file
    }
}