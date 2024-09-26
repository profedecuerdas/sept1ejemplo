package com.example.sept1ejemplo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroDao {
    // Consultas para la tabla de registros
    @Query("SELECT * FROM registros ORDER BY timestamp DESC")
    suspend fun getAllRegistros(): List<RegistroEntity>

    @Insert
    suspend fun insertRegistro(registro: RegistroEntity): Long

    // Consultas para la tabla de docente
    @Query("SELECT * FROM docente LIMIT 1")
    suspend fun getDocente(): DocenteEntity?

    @Insert
    suspend fun insertDocente(docente: DocenteEntity)
}
