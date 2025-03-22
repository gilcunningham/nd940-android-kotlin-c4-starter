// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.udacity.project4.utils

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

/**
 * Utility class for location runtime permissions.
 *
 * @author Gil Cunningham
 */
object LocationPermissionUtils {

    fun areLocationServicesSetup(context: Context) =
        isLocationPermissionGranted(context) && isDeviceLocationEnabled(context)

    fun isDeviceLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    fun isLocationPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun shouldShowFineLocationPermissionRationale(activity: Activity) =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permission.ACCESS_FINE_LOCATION
        )

    fun shouldShowCoarseLocationPermissionRationale(activity: Activity) =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permission.ACCESS_COARSE_LOCATION
        )

    fun shouldShowRequestPermissionRationale(activity: Activity) =
        shouldShowFineLocationPermissionRationale(activity) ||
                shouldShowCoarseLocationPermissionRationale(activity)

    fun requestLocationPermission(activity: Activity, requestCode : Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                permission.ACCESS_COARSE_LOCATION,
                permission.ACCESS_FINE_LOCATION
            ),
            requestCode
        )
    }
}