package com.example.mygpstestapplication

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var gpsTracker: GpsTracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationText: TextView = findViewById(R.id.locationText)
        val locationButton: Button = findViewById(R.id.locationButton)

        gpsTracker = GpsTracker(this@MainActivity)

        locationButton.setOnClickListener {
            Log.e("RHLog", "btn clicked")

            val currentLocation = getCurrentLocation()
            if (currentLocation == null)
                locationText.text = "Unknown location(null)"
            else
                locationText.text = "${currentLocation?.latitude}***${currentLocation?.longitude}"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            GpsTracker.REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getCurrentLocation(): Location? {
        gpsTracker?.let {
            it.getLocation()
            if (it.hasPermission()) {
                if (it.canGetLocation()) {
                    return it.location
                } else {
                    gpsTracker!!.showSettingsAlert()
                }
            }
        }
        return null
    }

    override fun onStop() {
        super.onStop()
        gpsTracker?.stopUsingGPS()
    }
}
