package com.mastermovilesua.runtrackerraul.models

import android.os.Parcel
import android.os.Parcelable
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



data class EntrenamientoFirebase(
    val id: String = "",
    val tiempo: String = "",
    val distancia: Double = 0.0,
    val ritmo: Double = 0.0,
    val cadencia: Int = 0,
    val fecha: Long = 0L,
    val userId: String = "",
    val route: List<RoutePoint> = listOf()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString()!!,
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(tiempo)
        parcel.writeDouble(distancia)
        parcel.writeDouble(ritmo)
        parcel.writeInt(cadencia)
        parcel.writeLong(fecha)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EntrenamientoFirebase> {
        override fun createFromParcel(parcel: Parcel): EntrenamientoFirebase {
            return EntrenamientoFirebase(parcel)
        }

        override fun newArray(size: Int): Array<EntrenamientoFirebase?> {
            return arrayOfNulls(size)
        }
    }
}

