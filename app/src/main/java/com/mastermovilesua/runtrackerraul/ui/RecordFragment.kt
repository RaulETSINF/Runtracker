package com.mastermovilesua.runtrackerraul.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.RunTrackerApp
import com.mastermovilesua.runtrackerraul.adapter.TrainingAdapter
import com.mastermovilesua.runtrackerraul.databinding.FragmentRecordBinding
import com.mastermovilesua.runtrackerraul.models.Entrenamiento
import com.mastermovilesua.runtrackerraul.models.EntrenamientoFirebase
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

        trainingAdapter = TrainingAdapter(
            emptyList(),
            onTrainingClick = {
                val bundle = Bundle().apply {
                    putParcelable("entrenamiento", it)
                }
                Navigation.findNavController(requireView()).navigate(R.id.action_recordFragment_to_routeDetailFragment, bundle)
            }
        )
        binding.recyclerViewTraining.apply {
            adapter = trainingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Cargar los entrenamientos al RecyclerView
        //loadTrainings()
        getEntrenamientosFromFirestore()
    }

    /*    private fun loadTrainings() {
            CoroutineScope(Dispatchers.IO).launch {
                val trainings = RunTrackerApp.database.entrenamientoDao().getAllEntrenamientos()
                withContext(Dispatchers.Main) {
                    updateTrainingList(trainings)
                }
            }
        }*/

    private fun updateTrainingList(trainings: List<EntrenamientoFirebase>) {
        trainingAdapter.trainingList = trainings
        trainingAdapter.notifyDataSetChanged()
    }


    private fun getEntrenamientosFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            db.collection("entrenamientos")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->

                    val entrenamientos = documents.map { documentSnapshot ->
                        documentSnapshot.toObject(EntrenamientoFirebase::class.java)
                    }.toList()

                    updateTrainingList(entrenamientos)
                }
                .addOnFailureListener { e ->
                    Log.w("LoginActivity", "Error getting documents", e)
                }
        } else {
            Log.w("LoginActivity", "User not logged in")
        }
    }


}