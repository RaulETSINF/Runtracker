package com.mastermovilesua.runtrackerraul.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.models.Entrenamiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrainingAdapter(var trainingList: List<Entrenamiento>) :
    RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val currentTraining = trainingList[position]

        // Convertir milisegundos a una fecha normal
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(currentTraining.fecha)
        val fechaNormal = dateFormat.format(date)

        // Convertir la distancia de metros a kilómetros
        val distanciaEnKilometros = String.format("%.2f", currentTraining.distancia / 1000.0)

        holder.textViewTrainingDate.text = fechaNormal
        holder.textViewTrainingTime.text = "${currentTraining.tiempo} ms"
        holder.textViewTrainingDistance.text = "$distanciaEnKilometros km"
        holder.textViewTrainingRhythm.text = "${currentTraining.ritmo} min/km"
        holder.textViewTrainingCadence.text = "${currentTraining.cadencia} spm"
    }

    override fun getItemCount() = trainingList.size

    inner class TrainingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTrainingDate: TextView = itemView.findViewById(R.id.textViewTrainingDate)
        val textViewTrainingTime: TextView = itemView.findViewById(R.id.textViewTrainingTime)
        val textViewTrainingDistance: TextView = itemView.findViewById(R.id.textViewTrainingDistance)
        val textViewTrainingRhythm: TextView = itemView.findViewById(R.id.textViewTrainingRhythm)
        val textViewTrainingCadence: TextView = itemView.findViewById(R.id.textViewTrainingCadence)
    }
}
