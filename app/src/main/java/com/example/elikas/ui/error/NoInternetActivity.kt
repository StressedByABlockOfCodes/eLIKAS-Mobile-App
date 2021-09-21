package com.example.elikas.ui.error

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.elikas.R

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        val btnOffline: Button = findViewById(R.id.btnSendDataOffline)
        btnOffline.setOnClickListener {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        }
    }
}