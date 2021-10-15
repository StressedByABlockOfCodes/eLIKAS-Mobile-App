package com.example.elikas.ui.sms

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.elikas.R
import com.example.elikas.databinding.ActivityOfflineModeBinding
import com.example.elikas.ui.base.MainActivity
import com.example.elikas.ui.error.NoPermissionsActivity
import com.example.elikas.utils.Constants
import com.example.elikas.utils.PermissionsUtil
import com.example.elikas.utils.PermissionsUtil.checkPermissions
import com.example.elikas.utils.PermissionsUtil.startPermissionRequest
import com.google.android.material.snackbar.Snackbar

class OfflineModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfflineModeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfflineModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = binding.navView
        val toolbar: Toolbar = binding.toolbar

        //setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_profile,
                R.id.CMAdmitFragment, R.id.BCAddFragment,
                R.id.CMDischargeFragment, R.id.CMDispenseFragment,
                R.id.CMRequestFragment, R.id.BCDispenseFragment,
                R.id.viewSMSFragment, R.id.BCViewNonEvacueesFragment
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.back -> {

                    super.onBackPressed()
                    return@setOnItemSelectedListener true
                }
                R.id.home -> {
                    navController.navigate(R.id.navigation_home)
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    navController.navigate(R.id.navigation_profile)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)*/


        if (!checkPermissions(this, "SMS")) {
            startPermissionRequest(this, "SMS")
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
                }
                else -> {
                    // Permission denied.
                    Snackbar.make(findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.settings) {
                            startPermissionRequest(
                                this,
                                "SMS"
                            )
                        }
                        .show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /*override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }*/

}