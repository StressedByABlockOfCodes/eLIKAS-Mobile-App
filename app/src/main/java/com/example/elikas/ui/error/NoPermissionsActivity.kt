package com.example.elikas.ui.error

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.elikas.BuildConfig.APPLICATION_ID
import com.example.elikas.databinding.ActivityNoPermissionsBinding
import com.example.elikas.ui.base.MainActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

class NoPermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoPermissionsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_no_permissions)
        binding = ActivityNoPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        onActivityResult()

        binding.btnEnableGPS.isEnabled = !isGPSProviderEnabled()
        binding.btnEnableGPS.setOnClickListener {
            enableGPSPrompt()
        }

        binding.btnLocationAccess.isEnabled = !checkPermissions()
        binding.btnLocationAccess.setOnClickListener {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", APPLICATION_ID, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.btnEnableGPS.isEnabled = !isGPSProviderEnabled()
        binding.btnLocationAccess.isEnabled = !checkPermissions()

        if(isGPSProviderEnabled() && checkPermissions()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isGPSProviderEnabled(): Boolean =
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun enableGPSPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val result = LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Cast to a resolvable exception.
                            val resolvable: ResolvableApiException = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            //resolvable.startResolutionForResult(this, LocationRequest.PRIORITY_HIGH_ACCURACY)

                            val intentSenderRequest = IntentSenderRequest.Builder(resolvable.resolution).build()
                            launcher.launch(intentSenderRequest)

                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    }
                }
            }
        }
    }

    private fun onActivityResult() {
        launcher = this.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    Log.i("Status: ","On")
                    if(isGPSProviderEnabled() && checkPermissions()) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Log.i("Status: ","Off")
                    Toast.makeText(applicationContext, "GPS NOT ENABLED", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.i("Status: ","Off")
                }
            }
        }
    }
}