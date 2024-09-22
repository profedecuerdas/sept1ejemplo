#!/bin/bash

TIMESTAMP=$(date +"%m-%d_%H-%M")

# Nombre del directorio de respaldo usando la fecha y hora
BACKUP_DIR="respaldo-v-$TIMESTAMP"

# Verificar si la carpeta de respaldo ya existe
if [ ! -d "$BACKUP_DIR" ]; then
  echo "Creando carpeta de respaldo..."
  mkdir "$BACKUP_DIR"
fi

echo "Iniciando respaldo de archivos esenciales..."

# Archivos y carpetas esenciales
cp -r app/src "$BACKUP_DIR/app"       # Código fuente, layouts, recursos
cp app/build.gradle.kts "$BACKUP_DIR/app/build.gradle.kts"   # Configuración Gradle del módulo de la app
cp build.gradle.kts "$BACKUP_DIR/build.gradle.kts"           # Configuración Gradle a nivel del proyecto
cp settings.gradle.kts "$BACKUP_DIR/settings.gradle.kts"     # Configuración del proyecto
cp -r gradle/wrapper "$BACKUP_DIR/gradle"                    # Wrapper de Gradle
cp gradlew "$BACKUP_DIR/gradlew"                             # Script Gradle wrapper
cp gradlew.bat "$BACKUP_DIR/gradlew.bat"                     # Script Gradle wrapper para Windows
cp -r app/proguard-rules.pro "$BACKUP_DIR/app/proguard-rules.pro"   # Reglas ProGuard, si existen

# Contar el número de archivos copiados y calcular el tamaño total del respaldo
FILE_COUNT=$(find "$BACKUP_DIR" -type f | wc -l)
SIZE=$(du -sh "$BACKUP_DIR" | cut -f1)

# Mostrar información sobre el respaldo
echo "Respaldo completo en la carpeta $BACKUP_DIR."
echo "El respaldo contiene $FILE_COUNT archivos y ocupa un total de $SIZE."

