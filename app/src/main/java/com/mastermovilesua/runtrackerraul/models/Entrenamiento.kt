package com.mastermovilesua.runtrackerraul.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entrenamientos")
data class Entrenamiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tiempo: Long, // en milisegundos
    val distancia: Double, // en metros
    val ritmo: Double, // minutos por kilómetro
    val cadencia: Int, // pasos por minuto
    val fecha: Long // timestamp del inicio del entrenamiento
)
