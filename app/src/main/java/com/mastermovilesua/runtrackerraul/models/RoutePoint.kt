package com.mastermovilesua.runtrackerraul.models


import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint


data class RoutePoint(
    var point: GeoPoint = GeoPoint(0.0, 0.0),
    var color: Int = 0
){
    constructor() : this(GeoPoint(0.0, 0.0), 0)
}
