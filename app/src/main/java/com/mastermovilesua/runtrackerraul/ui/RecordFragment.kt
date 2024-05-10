package com.mastermovilesua.runtrackerraul.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.RunTrackerApp
import com.mastermovilesua.runtrackerraul.adapter.TrainingAdapter
import com.mastermovilesua.runtrackerraul.databinding.FragmentRecordBinding
import com.mastermovilesua.runtrackerraul.models.Entrenamiento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding
    private lateinit var trainingAdapter: TrainingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainingAdapter = TrainingAdapter(emptyList())
        binding.recyclerViewTraining.apply {
            adapter = trainingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Cargar los entrenamientos al RecyclerView
        loadTrainings()
    }

    private fun loadTrainings() {
        CoroutineScope(Dispatchers.IO).launch {
            val trainings = RunTrackerApp.database.entrenamientoDao().getAllEntrenamientos()
            withContext(Dispatchers.Main) {
                updateTrainingList(trainings)
            }
        }
    }

    private fun updateTrainingList(trainings: List<Entrenamiento>) {
        trainingAdapter.trainingList = trainings
        trainingAdapter.notifyDataSetChanged()
    }

}