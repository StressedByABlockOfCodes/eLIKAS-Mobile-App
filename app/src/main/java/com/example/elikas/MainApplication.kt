package com.example.elikas

import android.app.Application
import com.example.elikas.data.AppDatabase
import com.example.elikas.data.DisasterResponseRepository
import com.example.elikas.data.ResidentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ResidentRepository(database.residentDao()) }
    val repositoryDR by lazy { DisasterResponseRepository(database.disasterResponseDao()) }

}