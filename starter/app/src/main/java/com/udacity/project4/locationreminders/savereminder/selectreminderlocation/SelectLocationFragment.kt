package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.SupportMapFragment
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment() {

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()

    private val locationViewModel: SelectLocationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSelectLocationBinding.inflate(inflater, container, false).apply {
        viewModel = _viewModel
        lifecycleOwner = this@SelectLocationFragment.viewLifecycleOwner
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        // TODO: add the map setup implementation
        setupGoogleMap()

        // TODO: zoom to the user location after taking his permission
        // TODO: add style to the map
        // TODO: put a marker to location that the user selected
        // TODO: call this function after the user confirms on the selected location
        //onLocationSelected()
    }
    override fun onResume() {
        super.onResume()
        locationViewModel.onResumePermissionFlow(this)
    }

    private fun setupGoogleMap() {
        locationViewModel.onMapReady.observe(viewLifecycleOwner) { isMapReady ->
            if (isMapReady) {
                locationViewModel.enableMyLocation(this@SelectLocationFragment)
            }
        }
        mapFragment.getMapAsync(locationViewModel)
    }

    private val mapFragment: SupportMapFragment
        get() = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

    private fun onLocationSelected() {
        // TODO: When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            true
        }

        R.id.hybrid_map -> {
            true
        }

        R.id.satellite_map -> {
            true
        }

        R.id.terrain_map -> {
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}