package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SelectLocationViewModel : ViewModel(), OnMapReadyCallback {

    // TODO: get from stored
    val lastKnownLat = 0.0
    val lasKnownLng = 0.0

    private fun getLastKnownLatLng() = LatLng(lastKnownLat, lasKnownLng)

    private val _onMapReady = MutableLiveData<GoogleMap>()
    val onMapReady: LiveData<GoogleMap> = _onMapReady

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(getLastKnownLatLng())
                .title("Marker")
        )
        _onMapReady.value = googleMap
    }
}