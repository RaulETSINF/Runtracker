package com.mastermovilesua.runtrackerraul.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entrenamientos")
data class Entrenamiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val tiempo: String,
    val distancia: Double,
    val ritmo: Double,
    val cadencia: Int,
    val fecha: Long
)



data class EntrenamientoFirebase    (
    val id: String = "",
    val tiempo: String = "",
    val distancia: Double = 0.0,
    val ritmo: Double = 0.0,
    val cadencia: Int = 0,
    val fecha: Long = 0L,
    val userId: String = ""
)

