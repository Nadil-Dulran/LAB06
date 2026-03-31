package com.example.lab06

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {

            "com.example.lab06.MY_CUSTOM_ACTION" -> {
                Toast.makeText(context, "Custom broadcast received!", Toast.LENGTH_LONG).show()
            }

            Intent.ACTION_SCREEN_ON -> {
                Toast.makeText(context, "Screen ON", Toast.LENGTH_LONG).show()
            }

            Intent.ACTION_SCREEN_OFF -> {
                Toast.makeText(context, "Screen OFF", Toast.LENGTH_LONG).show()
            }

            Intent.ACTION_BATTERY_LOW -> {
                Toast.makeText(context, "Battery Low!", Toast.LENGTH_LONG).show()
            }
        }
    }
}