package com.example.sept1ejemplo.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "asignaturas",
    foreignKeys = [ForeignKey(
        entity = DocenteEntity::class,
        parentColumns = ["id"],
        childColumns = ["docenteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AsignaturaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val docenteId: Int
)
