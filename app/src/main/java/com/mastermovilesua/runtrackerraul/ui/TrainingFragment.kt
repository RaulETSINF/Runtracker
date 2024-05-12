package com.mastermovilesua.runtrackerraul.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.RunTrackerApp
import com.mastermovilesua.runtrackerraul.databinding.FragmentTrainingBinding
import com.mastermovilesua.runtrackerraul.models.Entrenamiento
import com.mastermovilesua.runtrackerraul.utils.distanceBetween
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingFragment : Fragment(), OnMapReadyCallback, SensorEventListener {

    private var isRunning = false
    private var isPaused = true

    private lateinit var binding: FragmentTrainingBinding
    private lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private var lastLocation: LatLng? = null

    private val routePoints = mutableListOf<LatLng>()
    private val routePausedPoints = mutableListOf<LatLng>()
    private var routePolyline: Polyline? = null

    private var totalDistance = 0.0
    private var cadence = 0
    private var rhythm = 0
    private var totalTrainingTime = 0

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var sharedPreferences: SharedPreferences




    //SHARED PREFERENCE

    //AUTOPAUSE
    private var isAutoPauseEnable = false
    private val AUTOPAUSE_THRESHOLD_METERS = 10
    private val LAST_LOCATIONS_TO_CHECK = 5
    private val lastLocations = mutableListOf<LatLng>()






    private val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            super.onLocationResult(locationResult)
            Log.d("My Location", "Location Updated")
            currentLocation = LatLng(locationResult.lastLocation!!.latitude, locationResult.lastLocation!!.longitude)
            currentLocation?.let {

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))

                if (isRunning){

                    if (!isPaused){

                        routePausedPoints.clear()
                        routePoints.add(it)
                        updateRoute()

                        if (lastLocation != null) {
                            totalDistance += distanceBetween(lastLocation!!, it)
                            binding.textViewDistance.text = "${String.format("%.2f", totalDistance/1000)} km"
                        }

                    }else{
                        routePoints.clear()
                        routePausedPoints.add(it)
                        updatePausedRoute()
                    }

                    println("Last Location Update")
                    lastLocation = it
                    if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("autopause", false)){
                        if (!isPaused){
                            checkAutopause(it)
                        }
                    }
                }
            }
        }
    }

    private fun checkAutopause(newLocation: LatLng) {
        lastLocations.add(newLocation)
        if (lastLocations.size > LAST_LOCATIONS_TO_CHECK) {
            lastLocations.removeAt(0)
        }
        if (lastLocations.size == LAST_LOCATIONS_TO_CHECK) {
            if (isUserStopped(lastLocations)) {
                stopTraining()
                lastLocations.clear()
                showToast("Autopause activado: Actividad detenida")
            }
        }
    }

    private fun isUserStopped(locations: List<LatLng>): Boolean {
        val distance = distanceBetween(locations.first(), locations.last())
        return distance < AUTOPAUSE_THRESHOLD_METERS
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainingBinding.inflate(inflater, container, false)

        binding.chronometer.base = SystemClock.elapsedRealtime()

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        mapFragment = (childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartStop.setOnClickListener {
            Log.d("Click", "Click Corto")
            if (isRunning) {
                if (isPaused){
                    resumeTraining()
                }else{
                    stopTraining()
                }
            } else {
                startTraining()
            }
        }

        binding.btnStartStop.setOnLongClickListener {
            Log.d("LongClick", "Click Largo")
            if (isRunning){
                showConfirmationDialog()
            }
            true
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Resetear Entrenamiento")
        builder.setMessage("¿Estás seguro de que deseas resetear el entrenamiento?")
        builder.setPositiveButton("Sí") { _, _ ->
            if (isRunning) {
                resetTraining()
            }
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun updateRoute() {
        googleMap.addPolyline(PolylineOptions().addAll(routePoints).color(Color.RED))
    }

    private fun updatePausedRoute() {
        googleMap.addPolyline(PolylineOptions().addAll(routePausedPoints).color(Color.TRANSPARENT))
    }

    private fun startTraining() {
        isRunning = true
        isPaused = false
        binding.chronometer.base = SystemClock.elapsedRealtime()
        binding.chronometer.start()
        binding.btnStartStop.text = "Pausar Entrenamiento"
        startLocationUpdates()
    }

    private fun stopTraining() {
        isPaused = true
        binding.chronometer.stop()
        binding.btnStartStop.text = "Reanudar Entrenamiento"
    }

    private fun resumeTraining(){
        isPaused = false
        isRunning = true
        binding.chronometer.start()
        binding.btnStartStop.text = "Pausar Entrenamiento"
        startLocationUpdates()
    }

    private fun resetTraining(){
        CoroutineScope(Dispatchers.IO).launch {
            val entrenamiento = Entrenamiento(
                tiempo = totalTrainingTime.toLong(),
                distancia = totalDistance,
                ritmo = rhythm.toDouble(),
                cadencia = cadence,
                fecha = System.currentTimeMillis()
            )
            RunTrackerApp.database.entrenamientoDao().insert(entrenamiento)
        }
        resetUserInterface()
    }

    private fun resetUserInterface() {
        isRunning = false
        isPaused = true
        binding.textViewDistance.text = "0.00 km"
        binding.textViewCadence.text = "0 spm"
        binding.textViewRhythm.text = "0:00 min/km"
        binding.btnStartStop.text = "Iniciar Entrenamiento"
        googleMap.clear()
        routePoints.clear()
        binding.chronometer.base = SystemClock.elapsedRealtime()
        binding.chronometer.stop()
        routePolyline = null
        totalDistance = 0.0
        cadence = 0
        rhythm = 0
        totalTrainingTime = 0
        stopLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.isMyLocationEnabled = true
        this.googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this.requireContext(),
                R.raw.map_style_night
            )
        )
        mapFragment.view?.findViewWithTag<ImageView>("GoogleMapMyLocationButton")?.visibility = View.GONE;
        mapFragment.view?.setBackgroundColor(resources.getColor(R.color.md_theme_inverseOnSurface))
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        val locationRequest = LocationRequest.Builder(getPriorityFromSharedPreference(), 100).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun getPriorityFromSharedPreference(): Int {
        when(PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("gps_accuracy", "high")){
            "high" -> return  Priority.PRIORITY_HIGH_ACCURACY
            "medium" -> return Priority.PRIORITY_BALANCED_POWER_ACCURACY
            "low" -> return Priority.PRIORITY_LOW_POWER
        }
        return  Priority.PRIORITY_HIGH_ACCURACY
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("onDestroyView", "onDestroyView")
        stopLocationUpdates()
        isRunning = false
        isPaused = true
        this.googleMap.clear()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor == stepSensor) {
                // Manejar los cambios en el contador de pasos aquí
                val steps = it.values[0].toInt()
                // Actualizar la UI con el número de pasos
                Log.d("Pasos", steps.toString())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}