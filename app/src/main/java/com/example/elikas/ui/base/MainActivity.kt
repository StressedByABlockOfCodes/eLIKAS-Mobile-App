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

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.DefaultRetryPolicy
import com.example.elikas.MainApplication
import com.example.elikas.R
import com.example.elikas.data.Area
import com.example.elikas.data.DisasterResponse
import com.example.elikas.data.Resident
import com.example.elikas.data.User
import com.example.elikas.databinding.ActivityMainBinding
import com.example.elikas.networking.GsonRequest
import com.example.elikas.networking.VolleySingleton
import com.example.elikas.service.ForegroundOnlyLocationService
import com.example.elikas.ui.custom.LoadingDialog
import com.example.elikas.ui.error.NoInternetActivity
import com.example.elikas.ui.error.NoPermissionsActivity
import com.example.elikas.utils.Constants.AREA_GET_URL
import com.example.elikas.utils.Constants.BARANGAY_RESIDENTS_GET_URL
import com.example.elikas.utils.Constants.CURRENT_URL
import com.example.elikas.utils.Constants.DISASTER_RESPONSE_GET_URL
import com.example.elikas.utils.Constants.EVACUEES_GET_URL
import com.example.elikas.utils.Constants.REQUEST_PERMISSIONS_REQUEST_CODE
import com.example.elikas.utils.InternetConnectionUtil
import com.example.elikas.utils.PermissionsUtil.checkPermissions
import com.example.elikas.utils.PermissionsUtil.startPermissionRequest
import com.example.elikas.utils.SharedPreferenceUtil
import com.example.elikas.utils.toText
import com.example.elikas.viewmodel.DisasterResponseViewModel
import com.example.elikas.viewmodel.DisasterResponseViewModelFactory
import com.example.elikas.viewmodel.ResidentViewModelFactory
import com.example.elikas.viewmodel.ResidentsViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var webView: WebView
    private lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var filechooserLauncher: ActivityResultLauncher<Intent>
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var user: User

    private lateinit var locationManager: LocationManager
    private var foregroundOnlyLocationServiceBound = false
    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null
    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
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

    //private var mUploadMessage: ValueCallback<Uri>? = null
    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    private val viewModel: ResidentsViewModel by viewModels {
        ResidentViewModelFactory((application as MainApplication).repository)
    }

    private val viewModelDR: DisasterResponseViewModel by viewModels {
        DisasterResponseViewModelFactory((application as MainApplication).repositoryDR)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = SharedPreferenceUtil.getUser(this)
        //Check first if there's internet connection
        //this is a temporary solution, will change to network listener in the future
        if(!InternetConnectionUtil.isNetworkAvailable(this)) {
            //Log.i(TAG, user.type)
            if(user.type == "Camp Manager" || user.type == "Barangay Captain") {
                startActivity(Intent(this, NoInternetActivity::class.java))
                finish()
            }
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
        onFileChooserResult()
        bottomNavView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.back -> {
                    if (webView.canGoBack())
                        webView.goBack()
                    return@setOnItemSelectedListener true
                }
                R.id.home -> {
                    webView.loadUrl("$CURRENT_URL/home")
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    webView.loadUrl("$CURRENT_URL/profile")
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()

        //sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        //Call this when Android interface of courier is called or check user role in onStart if it is a Courier
        if(user.type == "Courier") {
            val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
            bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        //val user: User = SharedPreferenceUtil.getUser(this)
        if(user.type == "Courier") {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                foregroundOnlyBroadcastReceiver,
                IntentFilter(
                    ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
            )
        }
    }

    override fun onPause() {
        super.onPause()
        //val user: User = SharedPreferenceUtil.getUser(this)
        if(user.type == "Courier") {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                foregroundOnlyBroadcastReceiver
            )
        }
    }

    override fun onStop() {
        super.onStop()
        //val user: User = SharedPreferenceUtil.getUser(this)
        if(user.type == "Courier") {
            if (foregroundOnlyLocationServiceBound) {
                unbindService(foregroundOnlyServiceConnection)
                foregroundOnlyLocationServiceBound = false
            }
        }

        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = MyWebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true             // Enable responsive layout
        webView.settings.loadWithOverviewMode = true        // Zoom out if the content width is greater than the width of the viewport
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")

        webView.loadUrl("$CURRENT_URL/home")
        loadingDialog = LoadingDialog(this@MainActivity)
        loadingDialog.showDialog()
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
            if(url == "$CURRENT_URL/home") {
                if(!loadingDialog.isShowing())
                    loadingDialog.showDialog()

            }
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
            //Toast.makeText(applicationContext, description, Toast.LENGTH_LONG).show()
            //webView.loadUrl("file:///android_asset/lost.html")
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
            Log.e("SSL error", "SSL error $error")
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.i("url", url)
            progressBar.visibility = View.VISIBLE
            if(url == "$CURRENT_URL/home") {
                if(!loadingDialog.isShowing())
                    loadingDialog.showDialog()
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            swipeRefresh.isRefreshing = false
            progressBar.visibility = View.GONE

            if(url == "$CURRENT_URL/home") {
                loadingDialog.close()
            }

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

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult
        ): Boolean {
            AlertDialog.Builder(this@MainActivity)
                //.setTitle()
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, which -> result.confirm() })
                .setCancelable(false)
                .create()
                .show()
            return true
        }

        override fun onJsConfirm(
            view: WebView,
            url: String,
            message: String?,
            result: JsResult
        ): Boolean {
            AlertDialog.Builder(this@MainActivity)
                //.setTitle("Javascript Dialog")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, which -> result.confirm() })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, which -> result.cancel() })
                .create()
                .show()
            return true
        }

        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
            uploadMessage?.onReceiveValue(null)
            uploadMessage = null

            uploadMessage = filePathCallback

            val intent = fileChooserParams!!.createIntent()
            try {
                intent.type = "image/*"
                filechooserLauncher.launch(intent)
                //startActivityForResult(intent, REQUEST_SELECT_FILE)
            } catch (e: ActivityNotFoundException) {
                uploadMessage = null
                Toast.makeText(applicationContext, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }
    }

    //This is a DEPRECATED method
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null)
                return
            var results: Array<Uri>? = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            uploadMessage?.onReceiveValue(results)
            uploadMessage = null
        }
        else
            Toast.makeText(applicationContext, "Failed to Upload Image", Toast.LENGTH_LONG).show()
    }*/

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            startActivity(Intent(this, NoPermissionsActivity::class.java))
            finish()
            /*Snackbar.make(findViewById(R.id.activity_main), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {
                    startPermissionRequest("FINE_LOCATION")
                }.show()*/
        } else {
            Log.i(TAG, "Requesting permission")
            startPermissionRequest(this, "FINE_LOCATION")
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
                    if(isGPSProviderEnabled()) {
                        Log.i(TAG, "Starting Location Updates")
                        foregroundOnlyLocationService?.subscribeToLocationUpdates()
                    }
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
        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

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

    private fun onFileChooserResult() {
        filechooserLauncher = this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    if (uploadMessage != null) {
                        val results: Array<Uri>? = WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
                        uploadMessage?.onReceiveValue(results)
                        uploadMessage = null
                    }
                }
                Activity.RESULT_CANCELED -> {
                    //Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
                    uploadMessage?.onReceiveValue(null)
                    uploadMessage = null
                }
                else -> {
                    Toast.makeText(applicationContext, "Failed to Upload Image", Toast.LENGTH_LONG).show()
                    uploadMessage?.onReceiveValue(null)
                    uploadMessage = null
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
                Log.d("Location Updates", location.toText())
            }
        }
    }

    /** Instantiate the interface and set the context  */
    private inner class WebAppInterface(private val mContext: Context) {

        //This is called by the courier
        @JavascriptInterface
        fun currentUser(user_id: String, user_type: String) {
            Log.i("User ID", user_id)
            Log.i("User Type", user_type)

            val currentUser = User(user_id.toInt(), user_type)
            SharedPreferenceUtil.saveUser(applicationContext, currentUser)

            if(user_type == "Courier") {
                if (!checkPermissions(this@MainActivity, "FINE_LOCATION")) {
                    //requestPermissions()
                    startActivity(Intent(this@MainActivity, NoPermissionsActivity::class.java))
                    finish()
                } else {
                    if(isGPSProviderEnabled())
                        foregroundOnlyLocationService?.subscribeToLocationUpdates()
                    else{
                        //enableGPSPrompt()
                        startActivity(Intent(this@MainActivity, NoPermissionsActivity::class.java))
                        finish()
                    }
                }
            }
        }

        @JavascriptInterface
        fun areaOfFieldOfficer(designatedPlace: String, total: String) {
            Log.i("designatedPlace", designatedPlace)
            val area = Area(designatedPlace, total)
            SharedPreferenceUtil.saveArea(applicationContext, area)

            syncWithDB(designatedPlace)
        }

        @JavascriptInterface
        fun updateResidents() {
            val area = SharedPreferenceUtil.getArea(this@MainActivity)
            syncWithDB(area.designated_place)
        }

        @JavascriptInterface
        fun logout() {
            Log.i(TAG, "Logging Out")
            foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
            SharedPreferenceUtil.reset(applicationContext)
            viewModel.removeAll()
            viewModelDR.removeAll()
        }
    }

    private fun getArea() {
        val areaToken: TypeToken<Area> = object: TypeToken<Area>(){}
        val areaGsonRequest = GsonRequest(
            AREA_GET_URL, areaToken,null,
            { response ->
                if(response.toString() != "") {
                    Log.i("Volley Area Request", response.toString())
                    SharedPreferenceUtil.saveArea(applicationContext, response)
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        areaGsonRequest.retryPolicy = DefaultRetryPolicy(
            50000,
            5,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        VolleySingleton.getInstance(this).addToRequestQueue(areaGsonRequest)
    }

    private fun syncWithDB(designatedPlace: String) {
        if(!InternetConnectionUtil.isNetworkAvailable(this))
            return

        val token: TypeToken<List<Resident>> = object: TypeToken<List<Resident>>(){}
        //val user: User = SharedPreferenceUtil.getUser(this)
        var url = ""
        val currentUser = SharedPreferenceUtil.getUser(applicationContext)
        when(currentUser.type) {
            "Camp Manager" -> url = EVACUEES_GET_URL + currentUser.id
            "Barangay Captain" -> {
                val place = designatedPlace.replace(" ", "%20")
                url = BARANGAY_RESIDENTS_GET_URL + place
                Log.i("URL", url)
            }
            //URLEncoder.encode(designatedPlace, "UTF-8")
        }
        Log.i("URL", url)
        val residentsGsonRequest = GsonRequest(
            url, token,null,
            { response ->
                if(response.isNotEmpty()) {
                    Log.i("Volley Residents Request", response.toString())
                    viewModel.insertAll(response)
                }
                else {
                    viewModel.removeAll()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )

        val token_dr: TypeToken<List<DisasterResponse>> = object: TypeToken<List<DisasterResponse>>(){}
        val DRGsonRequestDRGsonRequest = GsonRequest(
            DISASTER_RESPONSE_GET_URL, token_dr,null,
            { response ->
                if(response.isNotEmpty()) {
                    Log.i("Volley Disaster Response Request", response.toString())
                    viewModelDR.insertAll(response)
                }
                else {
                    viewModelDR.removeAll()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        /*val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, Constants.RESIDENTS_GET_URL, null,
            { response ->
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    Log.i("Volley GET Request", jsonObject.getString("name"))
                }

            },
            { error ->
                error.printStackTrace()
            }
        )*/
        residentsGsonRequest.retryPolicy = DefaultRetryPolicy(
            50000,
            5,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        DRGsonRequestDRGsonRequest.retryPolicy = DefaultRetryPolicy(
            50000,
            5,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        VolleySingleton.getInstance(this).addToRequestQueue(residentsGsonRequest)
        VolleySingleton.getInstance(this).addToRequestQueue(DRGsonRequestDRGsonRequest)
    }

    companion object {
        const val MAX_PROGRESS = 100
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