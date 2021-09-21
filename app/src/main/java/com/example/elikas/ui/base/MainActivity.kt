/*  Copyright 2019 Google LLC
 *  Copyright 2021 eLIKAS
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/


package com.example.elikas.ui.base

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.elikas.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.net.http.SslError
import android.util.Log
import android.webkit.*

import android.widget.Toast
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.elikas.service.ForegroundOnlyLocationService
import com.example.elikas.utils.toText
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import android.location.LocationManager
import com.example.elikas.R
import com.example.elikas.ui.error.NoInternetActivity
import com.example.elikas.ui.error.NoPermissionsActivity
import com.example.elikas.utils.InternetConnectionUtil
import com.example.elikas.utils.SharedPreferenceUtil


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var webView: WebView
    private lateinit var userID: String
    private lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var locationManager: LocationManager
    private var foregroundOnlyLocationServiceBound = false
    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null
    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences
    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Check first if there's internet connection
        //this is a temporary solution, will change to network listener in the future
        if(InternetConnectionUtil.isNetworkAvailable(this)) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        }

        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        progressBar = binding.progressBar
        swipeRefresh = binding.swipeRefresh
        webView = binding.webView
        val bottomNavView: BottomNavigationView = binding.bottomNavView
        bottomNavView.itemTextColor = null
        bottomNavView.itemIconTintList = null

        //WebView.setWebContentsDebuggingEnabled(false)
        initWebView()
        pullUpToRefresh()
        onActivityResult()
        bottomNavView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.back -> {
                    if (webView.canGoBack())
                        webView.goBack()
                    return@setOnItemSelectedListener true
                }
                R.id.home -> {
                    webView.loadUrl(CURRENT_URL + "home")
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    webView.loadUrl(CURRENT_URL + "profile")
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

        //sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        // TODO: Call this when Android interface of courier is called or check user role in onStart if it is a Courier
        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = MyWebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true             // Enable responsive layout
        webView.settings.loadWithOverviewMode = true        // Zoom out if the content width is greater than the width of the viewport
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")

        webView.loadUrl(CURRENT_URL + "home")
    }

    private fun pullUpToRefresh() {
        swipeRefresh.setOnRefreshListener {
            webView.reload()
            //webView.loadUrl("javascript:window.location.reload( true )")
        }
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorPrimary)
        swipeRefresh.setColorSchemeResources(
            R.color.colorSecondary
        )
    }

    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            view.loadUrl(url)
            progressBar.visibility = View.VISIBLE
            return true

            /*if (Uri.parse(url).host == "www.example.com") {
                // This is my web site, so do not override; let my WebView load the page
                return false
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
            }
            return true*/
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            Toast.makeText(applicationContext, description, Toast.LENGTH_LONG).show()
            //webView.loadUrl("file:///android_asset/lost.html")
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)

        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            swipeRefresh.isRefreshing = false
            progressBar.visibility = View.GONE

            /*try {
                val cookies: String = CookieManager.getInstance().getCookie(url)
                Log.d("Cookie", cookies)
            } catch (e: MalformedURLException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }*/
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
            //Log.d("progress", newProgress.toString())
            if (newProgress < MAX_PROGRESS && progressBar.visibility == View.GONE) {
                progressBar.visibility = View.VISIBLE
            }
            if (newProgress == MAX_PROGRESS) {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            startActivity(Intent(this, NoPermissionsActivity::class.java))
            finish()
            /*Snackbar.make(findViewById(R.id.activity_main), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {
                    startLocationPermissionRequest()
                }.show()*/
        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> when {
                // Permission was cancelled.
                grantResults.isEmpty() -> Log.d(TAG, "User interaction was cancelled.")
                // Permission was granted.
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    if(isGPSProviderEnabled())
                        foregroundOnlyLocationService?.subscribeToLocationUpdates()
                    else
                        enableGPSPrompt()
                }
                else -> {
                    // Permission denied.
                    startActivity(Intent(this, NoPermissionsActivity::class.java))
                    finish()
                    /*Snackbar.make(findViewById(R.id.activity_main), R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        }
                        .show()*/
                }
            }
        }
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
                foregroundOnlyLocationService?.subscribeToLocationUpdates()
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
                    //GPS is enabled so start location updates
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                    Toast.makeText(applicationContext, "GPS ENABLED", Toast.LENGTH_SHORT).show()
                }
                Activity.RESULT_CANCELED -> {
                    Log.i("Status: ","Off")
                    Toast.makeText(applicationContext, "GPS NOT ENABLED", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, NoPermissionsActivity::class.java))
                    finish()
                }
                else -> {
                    Log.i("Status: ","Off")
                }
            }
        }
    }

    /**
     * Receiver for location broadcasts from [ForegroundOnlyLocationService].
     */
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                //logResultsToScreen("Foreground location: ${location.toText()}")
                Log.d(TAG, "${location.toText()}")
            }
        }
    }

    /** Instantiate the interface and set the context  */
    private inner class WebAppInterface(private val mContext: Context) {

        //This is called by the courier
        @JavascriptInterface
        fun startSendingLocations(user_id: String, user_type: String) {
            Log.i("User ID", user_id)
            Log.i("User Type", user_type)
            SharedPreferenceUtil.saveUserID(applicationContext, user_id.toInt())
            //Toast.makeText(mContext, user_id, Toast.LENGTH_SHORT).show()
            // TODO: check for the user role and return if it's not courier
            userID = user_id

            if (!checkPermissions()) {
                requestPermissions()
            } else {
                if(isGPSProviderEnabled())
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                else
                    enableGPSPrompt()
            }
        }

        @JavascriptInterface
        fun logout() {
            foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
            SharedPreferenceUtil.reset(applicationContext)
        }
    }

    companion object {
        const val DEV_PAGE_URL = "http://192.168.1.9:8000/"
        const val PROD_PAGE_URL = "https://elikasphilippines.herokuapp.com/"
        const val CURRENT_URL = DEV_PAGE_URL
        const val MAX_PROGRESS = 100
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        private const val TAG = "MainActivity"
    }

    /*override fun onBackPressed() {
        // if your webview can go back it will go back
        if (webView.canGoBack())
            webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }*/

}