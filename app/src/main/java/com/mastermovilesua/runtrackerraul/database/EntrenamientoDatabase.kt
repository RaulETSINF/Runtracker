package com.mastermovilesua.runtrackerraul.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mastermovilesua.runtrackerraul.models.Entrenamiento

@Database(entities = [Entrenamiento::class], version = 2)
abstract class EntrenamientoDatabase : RoomDatabase() {
    abstract fun entrenamientoDao(): EntrenamientoDao
}
