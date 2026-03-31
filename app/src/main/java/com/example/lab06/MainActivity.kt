package com.example.lab06

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    private var boundService: MyBoundService? = null
    private var isBound = false
    private lateinit var myReceiver: MyReceiver

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyBoundService.LocalBinder
            boundService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Started Service buttons
        val startBtn = findViewById<Button>(R.id.btnStart)
        val stopBtn = findViewById<Button>(R.id.btnStop)
        val serviceIntent = Intent(this, MyStartedService::class.java)


        startBtn.setOnClickListener {
            startService(serviceIntent)
        }

        stopBtn.setOnClickListener {
            stopService(serviceIntent)
        }

        // Bound Service UI
        val btnBind = findViewById<Button>(R.id.btnBind)
        val btnUnbind = findViewById<Button>(R.id.btnUnbind)
        val btnGetNumber = findViewById<Button>(R.id.btnGetNumber)
        val txtNumber = findViewById<TextView>(R.id.txtNumber)

        // Bind Service
        btnBind.setOnClickListener {
            val intent = Intent(this, MyBoundService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        // Unbind Service
        btnUnbind.setOnClickListener {
            if (isBound) {
                unbindService(connection)
                isBound = false
            }
        }

        // Get Data from Service
        btnGetNumber.setOnClickListener {
            if (isBound) {
                val number = boundService?.getRandomNumber()
                txtNumber.text = getString(R.string.random_number, number)
            } else {
                txtNumber.text = getString(R.string.service_not_bound)
            }
        }
        myReceiver = MyReceiver()

// Custom broadcast filter
        val customFilter = IntentFilter("com.example.lab06.MY_CUSTOM_ACTION")

// System broadcast filter
        val systemFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_BATTERY_LOW)
        }

// Register receiver
        ContextCompat.registerReceiver(
            this,
            myReceiver,
            customFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        ContextCompat.registerReceiver(
            this,
            myReceiver,
            systemFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )


        CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    val intent = Intent("com.example.lab06.MY_CUSTOM_ACTION")
            intent.setPackage(packageName)
            sendBroadcast(intent)
                }

        val listView = findViewById<ListView>(R.id.listViewContacts)

// Check permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
            )
        } else {
            loadContacts(listView)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            val listView = findViewById<ListView>(R.id.listViewContacts)
            loadContacts(listView)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        unregisterReceiver(myReceiver)
    }
    private fun loadContacts(listView: ListView) {

        val contactsList = mutableListOf<String>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {

            val nameIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            val numberIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (it.moveToNext()) {

                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)

                contactsList.add("$name\n$number")
            }
        }

        // Display in ListView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            contactsList
        )

        listView.adapter = adapter
    }
}