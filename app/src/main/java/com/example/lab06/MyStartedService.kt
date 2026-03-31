package com.example.lab06

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import kotlinx.coroutines.*

class MyStartedService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()

        scope.launch {
            while (isActive) {
                delay(5000)
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Service Running", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }
}