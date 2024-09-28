package com.example.sept1ejemplo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "docente")
data class DocenteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreCompleto: String
)