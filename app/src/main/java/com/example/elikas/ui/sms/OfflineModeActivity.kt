package com.example.elikas.ui.sms

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
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
import com.example.elikas.data.NetworkStatus
import com.example.elikas.data.User
import com.example.elikas.databinding.ActivityOfflineModeBinding
import com.example.elikas.ui.base.MainActivity
import com.example.elikas.ui.error.NoInternetActivity
import com.example.elikas.ui.error.NoPermissionsActivity
import com.example.elikas.utils.Constants
import com.example.elikas.utils.NetworkStatusHelper
import com.example.elikas.utils.PermissionsUtil
import com.example.elikas.utils.PermissionsUtil.checkPermissions
import com.example.elikas.utils.PermissionsUtil.startPermissionRequest
import com.example.elikas.utils.SharedPreferenceUtil
import com.google.android.material.snackbar.Snackbar

class OfflineModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfflineModeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfflineModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user: User = SharedPreferenceUtil.getUser(this)
        NetworkStatusHelper(this@OfflineModeActivity).observe(this, {
            when(it){
                NetworkStatus.Available -> {
                    Toast.makeText(applicationContext, "Network Connection Established", Toast.LENGTH_LONG).show()
                    if(user.type == "Camp Manager" || user.type == "Barangay Captain") {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
                NetworkStatus.Unavailable -> {

                }
            }
        })

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