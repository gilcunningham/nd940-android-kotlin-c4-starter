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

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.udacity.project4.R

/**
 * Utility class for location runtime dialogs.
 *
 * @author Gil Cunningham
 */
object LocationDialogUtils {

    fun makeLocationPermissionDeniedToast(activity: Activity) {
        Toast.makeText(
            activity, R.string.permission_required_toast,
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * A dialog that displays a permission denied message.
     */
    class LocationPermissionDeniedDialog private constructor(): DialogFragment() {
        private var finishActivity = false
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            finishActivity = arguments?.getBoolean(ARGUMENT_FINISH_ACTIVITY) ?: false
            return AlertDialog.Builder(activity)
                .setMessage(R.string.location_permission_denied)
                .setPositiveButton(android.R.string.ok, null)
                .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (finishActivity) {
                makeLocationPermissionDeniedToast(requireActivity())
                activity?.finish()
            }
        }

        companion object {
            private const val ARGUMENT_FINISH_ACTIVITY = "finish"

            /**
             * Creates a new instance of this dialog and optionally finishes the calling Activity
             * when the 'Ok' button is clicked.
             */
            fun newInstance(finishActivity: Boolean): LocationPermissionDeniedDialog {
                val arguments = Bundle().apply {
                    putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                }
                return LocationPermissionDeniedDialog().apply {
                    this.arguments = arguments
                }
            }
        }
    }

    /**
     * TODO:
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     *
     * The activity should implement
     * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback]
     * to handle permit or denial of this permission request.
     */
    class LocationRationaleDialog private constructor(
        private var finishActivity : Boolean = true,
        private var onPositiveClickListener: OnRequestLocationPermissionListener? = null
    ) : DialogFragment() {

        fun interface OnRequestLocationPermissionListener {
            fun onRequestLocationPermission(activit: Activity)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(activity)
                .setMessage(R.string.permission_rationale_location)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    onPositiveClickListener?.onRequestLocationPermission(requireActivity())
                    finishActivity = false
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (finishActivity) {
                makeLocationPermissionDeniedToast(requireActivity())
                activity?.finish()
            }
        }

        companion object {
            /**
             * Creates a new instance of a dialog displaying the rationale for the use of the location
             * permission. The permission is requested after clicking 'ok'.
             *
             * @param requestCode Id of the request that is used to request the permission. It is
             * returned to the
             * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback].
             * @param finishActivity Whether the calling Activity should be finished if the dialog is
             * cancelled.
             */
            fun newInstance(
                onLocationPermissionsRequestdListener: OnRequestLocationPermissionListener
            ): LocationRationaleDialog {
                return LocationRationaleDialog().apply {
                    this.onPositiveClickListener = onLocationPermissionsRequestdListener
                }
            }
        }
    }

    /**
     * A system dialog that explains location services and allows a user to enable or
     * deny location services.
     */
    class DeviceLocationRationaleDialog private constructor(
        private val requestCode: Int,
        private val settingsTask: Task<LocationSettingsResponse>,
    ) {
        fun show(activity: Activity) {
            settingsTask.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    exception.startResolutionForResult(activity, requestCode)
                }
            }
        }

        companion object {
            /**
             * TODO
             * Creates a new instance of a dialog displaying the rationale for the use of the device
             * location permission. The permission is enabled after clicking 'ok'.
             *
             * @param requestCode    Id of the request that is used to request the permission.
             * It is returned to the [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback].
             * @param finishActivity Whether the calling Activity should be finished if the dialog is
             * cancelled.
             */
            fun newInstance(
                context: Context,
                requestCode: Int
            ): DeviceLocationRationaleDialog {
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 1000
                ).build()
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val settingsTask: Task<LocationSettingsResponse> = LocationServices
                    .getSettingsClient(context)
                    .checkLocationSettings(builder.build())
                return DeviceLocationRationaleDialog(requestCode, settingsTask)
            }
        }
    }
}