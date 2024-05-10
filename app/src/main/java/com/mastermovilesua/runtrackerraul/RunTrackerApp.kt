package com.mastermovilesua.runtrackerraul

import android.app.Application
import androidx.room.Room
import com.mastermovilesua.runtrackerraul.database.EntrenamientoDatabase

class RunTrackerApp : Application() {

    companion object {
        lateinit var database: EntrenamientoDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            EntrenamientoDatabase::class.java, "entrenamiento-db"
        ).build()
    }
}