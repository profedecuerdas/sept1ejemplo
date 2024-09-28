package com.example.sept1ejemplo.database

import androidx.room.*

@Dao
interface RegistroDao {
    // Existing queries...

    @Query("SELECT * FROM registros ORDER BY timestamp DESC")
    suspend fun getAllRegistros(): List<RegistroEntity>

    @Insert
    suspend fun insertRegistro(registro: RegistroEntity): Long

    // Docente related queries
    @Query("SELECT * FROM docente LIMIT 1")
    suspend fun getDocente(): DocenteEntity?

    @Insert
    suspend fun insertDocente(docente: DocenteEntity): Long

    // Asignatura related queries
    @Insert
    suspend fun insertAsignatura(asignatura: AsignaturaEntity): Long

    @Query("SELECT * FROM asignaturas WHERE docenteId = :docenteId")
    suspend fun getAsignaturasForDocente(docenteId: Int): List<AsignaturaEntity>

    @Transaction
    @Query("SELECT * FROM docente")
    suspend fun getDocenteWithAsignaturas(): List<DocenteWithAsignaturas>
}