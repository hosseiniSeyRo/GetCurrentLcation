package com.example.mygpstestapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Main2Activity : AppCompatActivity() {

    private val gpsTracker = GpsTracker2(this, 0, 0)
    lateinit var locationText: TextView
    lateinit var locationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationText = findViewById(R.id.locationText)
        locationButton = findViewById(R.id.locationButton)

        locationButton.setOnClickListener {
            if (gpsTracker.getCurrentLocationIsSuccess())
                locationText.text =
                    "${gpsTracker.location?.latitude}***${gpsTracker.location?.longitude}"
            else {
                if (gpsTracker.pleaseRepeat)
                    locationText.text = "REPEAT!!!"
                else
                    locationText.text = "FAILED"
            }
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
                    gpsTracker.getCurrentLocationIsSuccess()
                }
                return
            }
        }
    }

    override fun onStop() {
        gpsTracker.stopUsingGPS()
        super.onStop()
    }
}