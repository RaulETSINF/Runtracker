package com.mastermovilesua.runtrackerraul.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mastermovilesua.runtrackerraul.models.Entrenamiento

@Dao
interface EntrenamientoDao {
    @Insert
    suspend fun insert(entrenamiento: Entrenamiento)

    @Query("SELECT * FROM entrenamientos")
    suspend fun getAllEntrenamientos(): List<Entrenamiento>
}
