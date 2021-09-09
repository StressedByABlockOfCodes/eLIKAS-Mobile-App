package com.example.elikas

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.elikas.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.net.http.SslError
import android.util.Log

import android.webkit.SslErrorHandler

import android.widget.Toast




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    //private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //webView = findViewById(R.id.webView)
        progressBar = binding.progressBar
        //swipeRefresh = binding.swipeRefresh
        webView = binding.webView
        val bottomNavView: BottomNavigationView = binding.bottomNavView
        bottomNavView.itemTextColor = null
        bottomNavView.itemIconTintList = null

        initWebView()
        webView.loadUrl(PROD_PAGE_URL)

        bottomNavView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.back -> {
                    if (webView.canGoBack())
                        webView.goBack()
                    return@setOnItemSelectedListener true
                }
                R.id.home -> {
                    webView.loadUrl(PROD_PAGE_URL + "home")
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    webView.loadUrl(PROD_PAGE_URL + "profile")
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = MyWebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true             // Enable responsive layout
        webView.settings.loadWithOverviewMode = true        // Zoom out if the content width is greater than the width of the viewport
        webView.settings.domStorageEnabled = true
    }

    private inner class MyWebViewClient : WebViewClient() {

        /*override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
             view.loadUrl(url)
            progressBar.show()
            return true
            if (Uri.parse(url).host == "www.example.com") {
                // This is my web site, so do not override; let my WebView load the page
                return false
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
            }
            return true
        }*/

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_LONG).show()
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
            //swipe.setRefreshing(false)
            progressBar.visibility = View.GONE
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

    companion object {
        const val DEV_PAGE_URL = "http://192.168.1.4:8000/"
        const val PROD_PAGE_URL = "https://elikasphilippines.herokuapp.com/"
        const val MAX_PROGRESS = 100
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