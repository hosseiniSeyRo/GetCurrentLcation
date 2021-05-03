package com.example.mygpstestapplication

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationText: TextView = findViewById(R.id.locationText)
        val locationButton: Button = findViewById(R.id.locationButton)

        locationButton.setOnClickListener {
            if (getCurrentLocationIsSuccess())
                locationText.text = "${currentLocation?.latitude}***${currentLocation?.longitude}"
            else
                locationText.text = "FAILED"
        }
    }

    private fun getCurrentLocationIsSuccess(): Boolean {
        currentLocation = gpsTracker.currentLocation

        if (currentLocation != null) {
            return true
        }

        if (gpsTracker.getLocationAndItIsNull) {
            if (isFirstTimeGetLocation) {
                Toast.makeText(this, "FAILED: REPEAT", Toast.LENGTH_SHORT).show()
                isFirstTimeGetLocation = false
                return false
            }
            return true
        }

        return false
    }

    override fun onPause() {
        gpsTracker.stopUsingGPS()
        super.onPause()
    }
}