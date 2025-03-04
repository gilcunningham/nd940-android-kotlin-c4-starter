package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SelectLocationViewModel : ViewModel(), OnMapReadyCallback {

    val lastKnownLat = 0.0
    val lasKnownLng = 0.0

    private fun getLastKnownLatLng() = LatLng(lastKnownLat, lasKnownLng)

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(getLastKnownLatLng())
                .title("Marker")
        )
    }
}