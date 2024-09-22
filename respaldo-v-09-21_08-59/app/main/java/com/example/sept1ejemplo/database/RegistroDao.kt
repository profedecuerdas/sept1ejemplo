package com.example.sept1ejemplo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registros ORDER BY timestamp DESC")
    suspend fun getAllRegistros(): List<RegistroEntity>

    @Insert
    suspend fun insertRegistro(registro: RegistroEntity): Long // Devuelve el ID generado
}
