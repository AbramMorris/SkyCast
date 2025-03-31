package com.example.skycast.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.skycast.services.AlarmService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        if (action == "STOP_ALARM_SERVICE") {
            context.stopService(Intent(context, AlarmService::class.java))
            return
        }

        val label = intent.getStringExtra("alarm_label") ?: "Alarm"
        Toast.makeText(context, "Alarm: $label", Toast.LENGTH_LONG).show()
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("temperature", intent.getDoubleExtra("temperature", 0.0))
            putExtra("description", intent.getStringExtra("description"))
            putExtra("humidity", intent.getIntExtra("humidity", 0))
            putExtra("currentTemperatureUnit", intent.getStringExtra("currentTemperatureUnit"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        }else{
            context.startService(serviceIntent)
        }

    }
}
