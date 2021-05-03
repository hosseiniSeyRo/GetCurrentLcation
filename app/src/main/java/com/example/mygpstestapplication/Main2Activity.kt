package com.example.mygpstestapplication

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Main2Activity : AppCompatActivity() {

    private val gpsTracker = GpsTracker2(this, 0, 0)
    private var currentLocation: Location? = null
    private var isFirstTimeGetLocation = true
    lateinit var locationText: TextView
    lateinit var locationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationText = findViewById(R.id.locationText)
        locationButton = findViewById(R.id.locationButton)

        locationButton.setOnClickListener {
            if (!gpsTracker.hasPermission()) {
                gpsTracker.getPermission()
                return@setOnClickListener
            }

            if (!gpsTracker.canGetLocation()) {
                // no gps and network provider is enabled
                isFirstTimeGetLocation = true
                gpsTracker.showSettingsAlert()
                return@setOnClickListener
            }

            if (getCurrentLocationIsSuccess())
                locationText.text = "${currentLocation?.latitude}***${currentLocation?.longitude}"
            else
                locationText.text = "FAILED"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            GpsTracker2.REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted
                    if (!gpsTracker.canGetLocation()) {
                        gpsTracker.showSettingsAlert()
                    }
                }
                return
            }
        }
    }

    private fun getCurrentLocationIsSuccess(): Boolean {
        currentLocation = gpsTracker.currentLocation

        if (currentLocation != null) return true

        if (isFirstTimeGetLocation) {
            Toast.makeText(this, "FAILED: REPEAT", Toast.LENGTH_SHORT).show()
            isFirstTimeGetLocation = false
            return false
        }

        return true
    }

    override fun onPause() {
        super.onPause()
        gpsTracker.stopUsingGPS()
    }
}