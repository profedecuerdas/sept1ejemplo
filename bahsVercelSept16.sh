#!/bin/bash

# Function to create or modify files
create_or_modify_file() {
    local file_path="$1"
    local content="$2"
    mkdir -p "$(dirname "$file_path")"
    echo "$content" > "$file_path"
    echo "Created/Modified: $file_path"
}

# Delete unnecessary files and directories
rm -rf app/src/main/java/com/example/sept1ejemplo/ui
echo "Deleted: app/src/main/java/com/example/sept1ejemplo/ui"

# Modify existing files

# MainActivity.kt
create_or_modify_file "app/src/main/java/com/example/sept1ejemplo/MainActivity.kt" '
package com.example.sept1ejemplo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextNota: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextNota = findViewById(R.id.editTextNota)

        val btnLimpiar: Button = findViewById(R.id.btnLimpiar)
        val btnFirmar: Button = findViewById(R.id.btnFirmar)

        btnLimpiar.setOnClickListener {
            editTextNombre.text.clear()
            editTextNota.text.clear()
        }

        btnFirmar.setOnClickListener {
            val intent = Intent(this, FirmaActivity::class.java)
            intent.putExtra("nombre", editTextNombre.text.toString())
            intent.putExtra("nota", editTextNota.text.toString())
            startActivity(intent)
        }
    }
}
'

# Create FirmaActivity.kt
create_or_modify_file "app/src/main/java/com/example/sept1ejemplo/FirmaActivity.kt" '
package com.example.sept1ejemplo

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.io.image.ImageDataFactory
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FirmaActivity : AppCompatActivity() {
    private lateinit var signaturePad: SignaturePad
    private lateinit var btnAceptar: Button
    private lateinit var btnLimpiar: Button
    private var nombre: String = ""
    private var nota: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firma)

        signaturePad = findViewById(R.id.signaturePad)
        btnAceptar = findViewById(R.id.btnAceptar)
        btnLimpiar = findViewById(R.id.btnLimpiar)

        nombre = intent.getStringExtra("nombre") ?: ""
        nota = intent.getStringExtra("nota") ?: ""

        btnLimpiar.setOnClickListener {
            signaturePad.clear()
        }

        btnAceptar.setOnClickListener {
            if (!signaturePad.isEmpty) {
                val bitmap = signaturePad.signatureBitmap
                val base64Signature = bitmapToBase64(bitmap)
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                
                // Guardar en la base de datos
                // TODO: Implementar la lógica para guardar en la base de datos

                // Generar PDF
                generatePDF(bitmap)

                Toast.makeText(this, "Firma guardada y PDF generado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, firme antes de aceptar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun generatePDF(signatureBitmap: Bitmap) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val fileName = "firma_$timestamp.pdf"

        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(pdfWriter)
                val document = Document(pdfDocument)

                document.add(Paragraph("Nombre: $nombre"))
                document.add(Paragraph("Nota: $nota"))

                val signatureImage = Image(ImageDataFactory.create(getBitmapAsByteArray(signatureBitmap)))
                signatureImage.scaleToFit(200f, 100f)
                document.add(signatureImage)

                val timestampStr = "Firmado el ${SimpleDateFormat("d 'de' MMMM 'de' yyyy, 'a las' HH:mm", Locale("es", "ES")).format(Date())}"
                document.add(Paragraph(timestampStr))

                document.close()

                Toast.makeText(this, "PDF guardado en Descargas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}
'

# Create RegistrosActivity.kt
create_or_modify_file "app/src/main/java/com/example/sept1ejemplo/RegistrosActivity.kt" '
package com.example.sept1ejemplo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.RegistroEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RegistrosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        recyclerView = findViewById(R.id.recyclerViewRegistros)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RegistrosAdapter(emptyList())
        recyclerView.adapter = adapter

        cargarRegistros()
    }

    private fun cargarRegistros() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(this@RegistrosActivity)
            val registros = database.registroDao().getAllRegistros()
            withContext(Dispatchers.Main) {
                adapter.actualizarRegistros(registros)
            }
        }
    }
}

class RegistrosAdapter(private var registros: List<RegistroEntity>) : RecyclerView.Adapter<RegistrosAdapter.RegistroViewHolder>() {
    class RegistroViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        // TODO: Definir los elementos de la vista para cada item
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RegistroViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        // TODO: Configurar los elementos de la vista con los datos del registro
    }

    override fun getItemCount() = registros.size

    fun actualizarRegistros(nuevosRegistros: List<RegistroEntity>) {
        registros = nuevosRegistros
        notifyDataSetChanged()
    }
}
'

# Modify activity_main.xml
create_or_modify_file "app/src/main/res/layout/activity_main.xml" '
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:title="@string/app_name"
            app:titleTextColor="@android:color/white" />

    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">

            <EditText
                android:id="@+id/editTextNombre"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Nombre completo" />

            <EditText
                android:id="@+id/editTextNota"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Nota"
                android:layout_marginTop="16dp" />

            <Button
                android:id="@+id/btnLimpiar"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Limpiar"
                android:layout_marginTop="16dp" />

            <Button
                android:id="@+id/btnFirmar"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Firmar"
                android:layout_marginTop="8dp" />

        </LinearLayout>

    </androidx.core.widget.NestedScrollView>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
'

# Create activity_firma.xml
create_or_modify_file "app/src/main/res/layout/activity_firma.xml" '
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <com.github.gcacace.signaturepad.views.SignaturePad
        android:id="@+id/signaturePad"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@android:color/white" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginTop="16dp">

        <Button
            android:id="@+id/btnLimpiar"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Limpiar" />

        <Button
            android:id="@+id/btnAceptar"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Aceptar" />

    </LinearLayout>

</LinearLayout>
'

# Create activity_registros.xml
create_or_modify_file "app/src/main/res/layout/activity_registros.xml" '
<?xml version="1.0" encoding="utf-8"?>
<androidx.recyclerview.widget.RecyclerView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/recyclerViewRegistros"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="8dp" />
'

# Create item_registro.xml
create_or_modify_file "app/src/main/res/layout/item_registro.xml" '
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/textViewIndice"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/textViewTimestamp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <TextView
        android:id="@+id/textViewNombre"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <TextView
        android:id="@+id/textViewNota"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</LinearLayout>
'

# Modify AndroidManifest.xml
create_or_modify_file "app/src/main/AndroidManifest.xml" '
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.sept1ejemplo">

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Sept1ejemplo">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity android:name=".FirmaActivity" />
        <activity android:name=".RegistrosActivity" />

    </application>

</manifest>
'

# Modify build.gradle (app-level)
create_or_modify_file "app/build.gradle.kts" '
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.sept1ejemplo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sept1ejemplo"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.github.gcacace:signature-pad:1.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
'

echo "Script completed successfully. Please review the changes and make any necessary adjustments."
