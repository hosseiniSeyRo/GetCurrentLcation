package com.example.mygpstestapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GpsTracker3(
    private val context: Context,
    private val activity: Activity,
    private val minTimeBWUpdate: Long,
    private val minDistanceChangeForUpdate: Float
) : Service(), LocationListener {

    private lateinit var locationManager: LocationManager
    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    var location: Location? = null
    private var isFirstTimeGetLocation = true
    var pleaseRepeat = false

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else { //permission is automatically granted on sdk<23 upon installation
            // Permission is granted
            true
        }
    }

    private fun getPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    private fun canGetLocation(): Boolean {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGPSEnabled || isNetworkEnabled
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocationIsSuccess(): Boolean {
        if (!hasPermission()) {
            getPermission()
            return false
        }
        if (!canGetLocation()) {
            // gps and network provider is disable
            Log.d("RHLog","gps and network provider is disable")
            showSettingsAlert()
            return false
        }

        // First get location from Network Provider
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTimeBWUpdate,
                minDistanceChangeForUpdate, this
            )
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                Log.d("RHLog","network location: " + location!!.latitude + "***" + location!!.longitude)
                return true
            }
        }

        // get location from GPS Provider
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTimeBWUpdate,
                minDistanceChangeForUpdate, this
            )
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location != null) {
                Log.d("RHLog","gps location: " + location!!.latitude + "***" + location!!.longitude)
                return true
            }
        }
        if (isFirstTimeGetLocation) {
            //REPEAT 1 TIME!!!
            Log.d("RHLog","please repeat")
            pleaseRepeat = true
            isFirstTimeGetLocation = false
            return false
        }

        pleaseRepeat = false
        return true
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        locationManager.removeUpdates(this)
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("GPS Setting")
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
        alertDialog.setPositiveButton(
            "Settings"
        ) { dialog: DialogInterface?, which: Int ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1234
    }
}