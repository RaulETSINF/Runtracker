package com.mastermovilesua.runtrackerraul.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mastermovilesua.runtrackerraul.R
import com.mastermovilesua.runtrackerraul.databinding.FragmentRouteDetailBinding
import com.mastermovilesua.runtrackerraul.models.EntrenamientoFirebase
import com.mastermovilesua.runtrackerraul.models.RoutePoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions

class RouteDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentRouteDetailBinding
    private lateinit var googleMap: GoogleMap

    private var entrenamiento: EntrenamientoFirebase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        entrenamiento = arguments?.getParcelable("entrenamiento")
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)
    }

    private fun drawRoute(route: List<RoutePoint>) {
        val boundsBuilder = LatLngBounds.Builder()

        for (i in 0 until route.size - 1) {
            val startPoint = LatLng(route[i].point.latitude, route[i].point.longitude)
            val endPoint = LatLng(route[i + 1].point.latitude, route[i + 1].point.longitude)
            val color = route[i].color

            val polylineOptions = PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .width(5f)
                .color(color)

            googleMap.addPolyline(polylineOptions)

            // Incluir ambos puntos en los límites
            boundsBuilder.include(startPoint)
            boundsBuilder.include(endPoint)
        }

        if (route.isNotEmpty()) {
            val bounds = boundsBuilder.build()
            val padding = 100 // padding around the route in pixels
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this.requireContext(),
                R.raw.map_style_night
            )
        )

        // Desactivar la interactividad del mapa
        this.googleMap.uiSettings.isScrollGesturesEnabled = false
        this.googleMap.uiSettings.isZoomGesturesEnabled = false
        this.googleMap.uiSettings.isTiltGesturesEnabled = false
        this.googleMap.uiSettings.isRotateGesturesEnabled = false

        entrenamiento?.let { drawRoute(it.route) }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }
}
