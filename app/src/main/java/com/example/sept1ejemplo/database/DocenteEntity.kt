package com.example.sept1ejemplo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "docente")
data class DocenteEntity(
    @PrimaryKey val id: Int = 1,  // Solo un registro
    val nombreCompleto: String
)

