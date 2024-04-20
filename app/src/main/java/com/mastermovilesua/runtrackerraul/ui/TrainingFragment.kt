package com.mastermovilesua.runtrackerraul.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.maps.model.MarkerOptions
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.databinding.FragmentTrainingBinding

class TrainingFragment : Fragment(), OnMapReadyCallback {

    private var isRunning = false

    private lateinit var binding: FragmentTrainingBinding

    private lateinit var googleMap: GoogleMap


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null

    private val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            super.onLocationResult(locationResult)
            Log.d("My Location", "Location Updated")
            currentLocation = LatLng(locationResult.lastLocation!!.latitude, locationResult.lastLocation!!.longitude)
            currentLocation?.let {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                //googleMap.addMarker(MarkerOptions().position(it).title("Mi ubicación"))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingBinding.inflate(inflater, container, false)

        binding.chronometer.base = SystemClock.elapsedRealtime()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartStop.setOnClickListener {
            if (isRunning) {
                stopTraining()
            } else {
                startTraining()
            }
        }
    }

    private fun startTraining() {
        isRunning = true
        binding.chronometer.base = SystemClock.elapsedRealtime()
        binding.chronometer.start()
        binding.btnStartStop.text = "Pausar Entrenamiento"
        startLocationUpdates()
    }

    private fun stopTraining() {
        isRunning = false
        binding.chronometer.stop()
        binding.btnStartStop.text = "Iniciar Entrenamiento"
        stopLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}