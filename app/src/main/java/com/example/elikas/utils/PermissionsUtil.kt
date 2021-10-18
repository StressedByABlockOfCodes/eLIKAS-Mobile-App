package com.example.elikas.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.elikas.utils.Constants.REQUEST_PERMISSIONS_REQUEST_CODE
import com.example.elikas.utils.Constants.REQUEST_PERMISSIONS_SEND_SMS

internal object PermissionsUtil {
    fun checkPermissions(activity: Activity, type: String): Boolean {
        return when (type) {
            "FINE_LOCATION" -> {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            "SMS" -> {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.SEND_SMS
                )
            }
            else -> false
        }
    }

    fun startPermissionRequest(activity: Activity, type: String) {
        when (type) {
            "FINE_LOCATION" -> {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
            "SMS" -> {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.SEND_SMS),
                    REQUEST_PERMISSIONS_SEND_SMS
                )
            }
        }
    }
}