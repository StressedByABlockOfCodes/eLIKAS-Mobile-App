package com.example.elikas.ui.error

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.elikas.R
import com.example.elikas.data.NetworkStatus
import com.example.elikas.data.User
import com.example.elikas.ui.base.MainActivity
import com.example.elikas.ui.sms.OfflineModeActivity
import com.example.elikas.utils.Constants
import com.example.elikas.utils.NetworkStatusHelper
import com.example.elikas.utils.PermissionsUtil
import com.example.elikas.utils.SharedPreferenceUtil
import com.google.android.material.snackbar.Snackbar

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        val user: User = SharedPreferenceUtil.getUser(this)
        NetworkStatusHelper(this@NoInternetActivity).observe(this, {
            when(it){
                NetworkStatus.Available -> {
                    Snackbar
                        .make(findViewById(android.R.id.content),"Network Connection Established",Snackbar.LENGTH_LONG)
                        .show()
                    /*if(user.type == "Camp Manager" || user.type == "Barangay Captain") {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }*/
                }
                NetworkStatus.Unavailable -> {
                    Snackbar
                        .make(findViewById(android.R.id.content),"No Internet Connection",Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        })

        val btnOffline: Button = findViewById(R.id.btnSendDataOffline)
        btnOffline.setOnClickListener {
            if (!PermissionsUtil.checkPermissions(this, "SMS")) {
                PermissionsUtil.startPermissionRequest(this, "SMS")
            }
            else {
                startActivity(Intent(this, OfflineModeActivity::class.java))
                finish()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.REQUEST_PERMISSIONS_SEND_SMS -> when {
                // Permission was cancelled.
                grantResults.isEmpty() -> Log.d("OfflineModeActivity", "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission was granted.
                    Log.d("OfflineModeActivity", "SMS Permission granted.")
                    startActivity(Intent(this, OfflineModeActivity::class.java))
                    finish()
                }
                else -> {
                    // Permission denied.
                    Snackbar.make(
                        findViewById(R.id.activity_no_internet),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}