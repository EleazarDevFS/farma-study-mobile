package com.example.farmastudy

import android.app.Application
import com.example.farmastudy.data.datasource.SeedData
import com.example.farmastudy.data.local.FarmaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FarmaApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: FarmaDatabase by lazy { FarmaDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        seedDatabase()
    }

    private fun seedDatabase() {
        appScope.launch {
            SeedData.seedIfEmpty(this@FarmaApp, database)
        }
    }
}