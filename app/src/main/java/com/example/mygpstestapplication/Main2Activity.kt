package com.example.mygpstestapplication

import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Main2Activity : AppCompatActivity() {

    private val gpsTracker = GpsTracker2(this, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationText: TextView = findViewById(R.id.locationText)
        val locationButton: Button = findViewById(R.id.locationButton)

        locationButton.setOnClickListener {
            val currentLocation = getCurrentLocation()
            locationText.text = "${currentLocation?.latitude}***${currentLocation?.longitude}"
        }
    }

    private fun getCurrentLocation(): Location? {
        return gpsTracker.currentLocation
    }

    override fun onPause() {
        gpsTracker.stopUsingGPS()
        super.onPause()
    }
}