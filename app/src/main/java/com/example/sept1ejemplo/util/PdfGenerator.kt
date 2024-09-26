package com.example.sept1ejemplo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import com.example.sept1ejemplo.database.RegistroEntity
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Image
import com.itextpdf.io.image.ImageDataFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    // Función para recortar la firma eliminando los bordes vacíos
    fun cropSignature(signatureBitmap: Bitmap): Bitmap {
        val width = signatureBitmap.width
        val height = signatureBitmap.height
        var minX = width
        var minY = height
        var maxX = -1
        var maxY = -1

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = signatureBitmap.getPixel(x, y)
                if (pixel != Color.WHITE) {  // Asumiendo que los bordes vacíos son blancos
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }

        if (maxX == -1 || maxY == -1) {
            return signatureBitmap
        }

        return Bitmap.createBitmap(signatureBitmap, minX, minY, maxX - minX + 1, maxY - minY + 1)
    }

    // Función para redimensionar la firma a una resolución óptima antes del crop
    fun resizeBitmap(original: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
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

    // Función para generar el nombre del archivo basado en el nombre completo y timestamp legible
    fun generatePdfFileName(nombresApellidos: String, timestamp: Long): String {
        // Dividir el nombre completo en palabras y tomar los primeros 3 caracteres de cada palabra
        val words = nombresApellidos.split(" ")
        val abbreviation = words.joinToString("_") { word ->
            word.take(3).capitalize(Locale.getDefault())
        }

        // Formatear el timestamp a formato legible: dd-MMM-HH-mm
        val dateFormat = SimpleDateFormat("dd-MMM-HH-mm", Locale.getDefault())
        val date = Date(timestamp)
        val formattedTimestamp = dateFormat.format(date)

        // Construir el nombre del archivo: "Reg_" seguido de abbreviation y del timestamp
        return "Reg_${abbreviation}_$formattedTimestamp.pdf"
    }

    fun generatePdf(context: Context, registro: RegistroEntity, signatureBitmap: Bitmap): File {
        // Generar el nombre del archivo basado en el nombre completo y el timestamp
        val fileName = generatePdfFileName(registro.nombresApellidos, registro.timestamp)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        PdfWriter(file).use { writer ->
            PdfDocument(writer).use { pdf ->
                Document(pdf).use { document ->

                    // Cargar la imagen del membrete desde drawable usando el contexto
                    val membreteBitmap = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("membrete", "drawable", context.packageName))

                    // Convertir el membrete a JPG y reducir su tamaño
                    val streamMembrete = ByteArrayOutputStream()
                    membreteBitmap.compress(Bitmap.CompressFormat.JPEG, 85, streamMembrete) // Usar JPEG con calidad 85%
                    val membreteBytes = streamMembrete.toByteArray()

                    val membreteImage = Image(ImageDataFactory.create(membreteBytes))
                    membreteImage.scaleToFit(500f, 150f) // Ajustar el tamaño del membrete

                    // Añadir el membrete al documento
                    document.add(membreteImage)

                    // Añadir el texto "Registro #<id>"
                    document.add(Paragraph("Registro #${registro.id}"))

                    // Añadir nombres y apellidos
                    document.add(Paragraph("Nombres y Apellidos: ${registro.nombresApellidos}"))

                    // Añadir nota
                    document.add(Paragraph("Nota: ${registro.nota}"))

                    // Redimensionar la firma a una resolución óptima (600x300)
                    val resizedSignature = resizeBitmap(signatureBitmap, 600, 300)

                    // Recortar la firma después de redimensionarla
                    val croppedSignature = cropSignature(resizedSignature)

                    // Convertir la firma recortada a JPG
                    val streamFirma = ByteArrayOutputStream()
                    croppedSignature.compress(Bitmap.CompressFormat.JPEG, 85, streamFirma) // Usar JPEG con calidad 85%
                    val signatureBytes = streamFirma.toByteArray()

                    // Crear una imagen iText a partir de los datos del bitmap de la firma en formato JPG
                    val signatureImage = Image(ImageDataFactory.create(signatureBytes))
                    signatureImage.scaleToFit(100f, 100f) // Ajustar el tamaño de la firma

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
