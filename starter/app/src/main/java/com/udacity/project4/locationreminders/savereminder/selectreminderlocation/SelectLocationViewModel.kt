package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.udacity.project4.utils.LocationDialogUtils.DeviceLocationRationaleDialog
import com.udacity.project4.utils.LocationDialogUtils.LocationPermissionDeniedDialog
import com.udacity.project4.utils.LocationDialogUtils.LocationRationaleDialog
import com.udacity.project4.utils.LocationPermissionUtils.areLocationServicesSetup
import com.udacity.project4.utils.LocationPermissionUtils.isDeviceLocationEnabled
import com.udacity.project4.utils.LocationPermissionUtils.isLocationPermissionGranted
import com.udacity.project4.utils.LocationPermissionUtils.requestLocationPermission
import com.udacity.project4.utils.LocationPermissionUtils.shouldShowRequestPermissionRationale

import com.udacity.project4.utils.SingleLiveEvent

class SelectLocationViewModel : ViewModel(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    // TODO: get stored
    private var lastKnownLat = 0.0
    private var lastKnownLng = 0.0
    private var locationRationaleShown = false
    val onMapReady = SingleLiveEvent<Boolean>()
    private val onRequestLocationPermissionListener = LocationRationaleDialog
        .OnRequestLocationPermissionListener { activity ->
            permissionRequestShown = true
            requestLocationPermission(activity, LOCATION_PERMISSION_REQUEST_CODE)
        }
    private var permissionRequestShown = false

    fun addLastKnownLocationMarker() = addLocationMarker(getLastKnownLatLng())

    fun addLocationMarker(latLng: LatLng, title: String = "Marker") =
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
        )

    private fun getLastKnownLatLng() = LatLng(lastKnownLat, lastKnownLng)

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        onMapReady.value = true
    }

    private fun showMissingPermissionError(fragment: Fragment) {
        LocationPermissionDeniedDialog.newInstance(true)
            .show(fragment.childFragmentManager, "dialog")
    }

    private fun isMapReady() = onMapReady.value != null

    fun onResumePermissionFlow(fragment: Fragment) {
        if (!isMapReady()) {
            return
        }
        val context = fragment.requireContext()
        val activity = fragment.requireActivity()
        // device location denied
        if (wasDeviceLocationDenied(context)) {
            activity.finish()
            return
        }
        // location permission denied
        if (wasLocationPermissionDenied(context)) {
            showMissingPermissionError(fragment)
            return
        }
        enableMyLocation(fragment)
    }

    private fun wasLocationPermissionDenied(context: Context) =
        permissionRequestShown && !areLocationServicesSetup(context)

    private fun wasDeviceLocationDenied(context: Context) =
        locationRationaleShown && !isDeviceLocationEnabled(context)

    @SuppressLint("MissingPermission")
    fun enableMyLocation(fragment: Fragment) {
        val context = fragment.requireContext()
        //TODO: make these into rules
        if (areLocationServicesSetup(context)) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            googleMap.isMyLocationEnabled = true
            googleMap.zoomMyLocation()
            //TODO - add map callbacks
            return
        }
        // device location
        if (!isDeviceLocationEnabled(context)) {
            DeviceLocationRationaleDialog.newInstance(
                context,
                DEVICE_LOCATION_PERMISSION_REQUEST_CODE
            ).show(fragment.requireActivity())
            locationRationaleShown = true
            return
        }
        // location permission rationale
        if (!locationRationaleShown &&
            shouldShowRequestPermissionRationale(fragment.requireActivity())
        ) {
            LocationRationaleDialog.newInstance(onRequestLocationPermissionListener)
                .show(fragment.childFragmentManager, "location rationale dialog")
            locationRationaleShown = true
            return
        }
        // request location permissions
        if (!permissionRequestShown) {
            permissionRequestShown = true
            requestLocationPermission(fragment.requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    fun GoogleMap.zoomMyLocation() {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location?.let {
                lastKnownLat = it.latitude
                lastKnownLng = it.longitude
                zoomLastKnownLocation()
            }
        }.addOnFailureListener { exception ->
            //TODO - error handling
        }
    }

    fun GoogleMap.zoomLastKnownLocation() {
        val coordinate = LatLng(lastKnownLat, lastKnownLng)
        val zoomLocation = CameraUpdateFactory.newLatLngZoom(coordinate, ZOOM_LEVEL)
        animateCamera(zoomLocation, ZOOM_DURATION, object : CancelableCallback {
            override fun onCancel() {}
            override fun onFinish() {
                //TODO - add marker ?
            }
        })
    }

    private companion object {
        // request code for device location request
        const val DEVICE_LOCATION_PERMISSION_REQUEST_CODE = 1

        // request code for location permission request
        const val LOCATION_PERMISSION_REQUEST_CODE = 2
        const val ZOOM_LEVEL = 12f
        const val ZOOM_DURATION = 2000
    }
}
