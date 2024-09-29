#!/bin/bash

# Archivos esenciales
FILES=(
    "./app/src/main/AndroidManifest.xml"
    "./settings.gradle"
    "./build.gradle.kts"
    "./app/build.gradle.kts"
    "./app/src/main/java/com/example/sept1ejemplo/MainActivity.kt"
    "./app/src/main/java/com/example/sept1ejemplo/ActivityDos.kt"
    "./app/src/main/java/com/example/sept1ejemplo/ActivityTres.kt"
    "./app/src/main/java/com/example/sept1ejemplo/FirmaActivity.kt"
    "./app/src/main/java/com/example/sept1ejemplo/RegistrosActivity.kt"  # Nuevo archivo
    "./app/src/main/java/com/example/sept1ejemplo/RegistroDocente.kt"    # Nuevo archivo
    "./app/src/main/java/com/example/sept1ejemplo/database/AppDatabase.kt"
    "./app/src/main/java/com/example/sept1ejemplo/database/RegistroDao.kt"
    "./app/src/main/java/com/example/sept1ejemplo/database/RegistroEntity.kt"
    "./app/src/main/java/com/example/sept1ejemplo/database/AsignaturaEntity.kt"   # Nuevo archivo
    "./app/src/main/java/com/example/sept1ejemplo/database/DocenteEntity.kt"      # Nuevo archivo
    "./app/src/main/java/com/example/sept1ejemplo/database/DocenteWithAsignaturas.kt"  # Nuevo archivo
    "./app/src/main/java/com/example/sept1ejemplo/util/PdfGenerator.kt"
    "./app/src/main/res/layout/activity_main.xml"
    "./app/src/main/res/layout/activity_dos.xml"
    "./app/src/main/res/layout/activity_tres.xml"
    "./app/src/main/res/layout/activity_firma.xml"
    "./app/src/main/res/layout/item_registro.xml"      # Nuevo archivo
    "./app/src/main/res/layout/activity_registro_docente.xml"  # Nuevo archivo
    "./app/src/main/res/layout/activity_registros.xml"  # Nuevo archivo
    "./app/src/main/res/values/strings.xml"
    "./app/src/main/res/values/colors.xml"
    "./app/src/main/res/values/themes.xml"
)

# Archivo de salida
OUTPUT_FILE="estructura_proyecto.txt"
MAX_TOKENS=4000
CURRENT_TOKENS=0

# Función para contar los tokens (aproximado: 1 token ~ 0.75 palabras)
count_tokens() {
    wc -w "$1" | awk '{print int($1 / 0.75)}'
}

# Crear o limpiar el archivo de salida
> "$OUTPUT_FILE"

# Recorrer cada archivo y añadir su contenido si no se excede el límite de tokens
for FILE in "${FILES[@]}"; do
    if [ -f "$FILE" ]; then
        # Contar los tokens del archivo
        FILE_TOKENS=$(count_tokens "$FILE")
        
        # Verificar si al agregar el archivo se excede el límite de tokens
        if (( CURRENT_TOKENS + FILE_TOKENS > MAX_TOKENS )); then
            echo "Se ha alcanzado el límite de tokens. Archivo estructura_proyecto.txt completo."
            break
        fi

        # Añadir la ruta del archivo al archivo de salida
        echo "Archivo: $FILE" >> "$OUTPUT_FILE"
        echo "-----------------------------------------------------" >> "$OUTPUT_FILE"

        # Añadir el contenido del archivo al archivo de salida
        cat "$FILE" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        
        # Actualizar el contador de tokens
        CURRENT_TOKENS=$((CURRENT_TOKENS + FILE_TOKENS))
    fi
done

echo "Estructura del proyecto creada en $OUTPUT_FILE con un total de $CURRENT_TOKENS tokens."

