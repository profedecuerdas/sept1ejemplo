#!/bin/bash

# Nombre del archivo de salida
output_file="estructura_proyecto_completa.txt"

# Generar encabezado del archivo
echo "Estructura del proyecto:" > "$output_file"

# Iterar a través de los archivos y directorios
for item in $(find .); do
    # Agregar cada archivo o directorio al archivo de salida
    echo "$item" >> "$output_file"
done

echo "La estructura del proyecto se ha guardado en $output_file"

