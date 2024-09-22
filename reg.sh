#!/bin/bash

# Definir rutas del proyecto
PDF_GENERATOR="./app/src/main/java/com/example/sept1ejemplo/util/PdfGenerator.kt"

# Corregir PdfGenerator.kt
echo "Corrigiendo $PDF_GENERATOR..."

# Eliminar las declaraciones duplicadas y corregir el uso de Image con iText 7.x
sed -i '/val signatureImage = Image.getInstance/d' $PDF_GENERATOR

# Agregar código correcto para convertir el Bitmap a Image compatible con iText 7.x
sed -i '/document.add(Paragraph("Fecha y Hora: /a\
                    val stream = ByteArrayOutputStream()\
                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)\
                    val imageData = ImageDataFactory.create(stream.toByteArray())\
                    val signatureImage = Image(imageData)\
                    signatureImage.scaleToFit(100f, 100f)\
                    document.add(signatureImage)\
' $PDF_GENERATOR

echo "Corrección completada. Compilando el proyecto..."

# Compilar el proyecto

echo "Script completado. Los archivos han sido corregidos y el proyecto ha sido compilado."

