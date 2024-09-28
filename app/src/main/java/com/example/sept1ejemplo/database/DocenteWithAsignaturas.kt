package com.example.sept1ejemplo.database

import androidx.room.Embedded
import androidx.room.Relation

data class DocenteWithAsignaturas(
    @Embedded val docente: DocenteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "docenteId"
    )
    val asignaturas: List<AsignaturaEntity>
)
