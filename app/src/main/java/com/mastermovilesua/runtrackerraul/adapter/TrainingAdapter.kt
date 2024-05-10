package com.mastermovilesua.runtrackerraul.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.models.Entrenamiento

class TrainingAdapter(var trainingList: List<Entrenamiento>) :
    RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val currentTraining = trainingList[position]
        holder.textViewTrainingDate.text = "Fecha: ${currentTraining.fecha}"
        holder.textViewTrainingTime.text = "Tiempo: ${currentTraining.tiempo} ms"
        holder.textViewTrainingDistance.text = "Distancia: ${currentTraining.distancia} m"
        holder.textViewTrainingRhythm.text = "Ritmo: ${currentTraining.ritmo} min/km"
        holder.textViewTrainingCadence.text = "Cadencia: ${currentTraining.cadencia} spm"
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
