package com.example.sept1ejemplo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class RegistroEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombresApellidos: String,
    val nota: String,
    val timestamp: Long,
    val firmaBase64: String? = null,
)