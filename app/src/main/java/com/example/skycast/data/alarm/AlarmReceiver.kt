package com.example.skycast.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.skycast.services.AlarmService
import com.example.skycast.util.AlarmScheduler.scheduleAlarm
import com.example.skycast.util.AlarmScheduler.scheduleSnooze


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // First check if this is a snooze request
        if (intent.action == "SNOOZE_ALARM") {
            val alarmId = intent.getIntExtra("alarm_id", -1)
            Log.d("AlarmReceiver", "Received snooze request for alarm ID: $alarmId")
            if (alarmId != -1) {
                // Schedule alarm to trigger again in 5 seconds
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtras(intent.extras ?: Bundle()) // Carry all original extras
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmId + 1000, // Different request code for snooze
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val triggerTime = System.currentTimeMillis() + 5000 // 5 seconds from now
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                scheduleSnooze(context, alarmId, snoozeIntent)

                Toast.makeText(context, "Alarm snoozed for 5 seconds", Toast.LENGTH_SHORT).show()
                return
            }
        }
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
        } else {
            context.startService(serviceIntent)
        }
    }
}

//class AlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//
//        val action = intent.action
//        if (action == "STOP_ALARM_SERVICE") {
//            context.stopService(Intent(context, AlarmService::class.java))
//            return
//        }
//
//        val label = intent.getStringExtra("alarm_label") ?: "Alarm"
//        Toast.makeText(context, "Alarm: $label", Toast.LENGTH_LONG).show()
//        val serviceIntent = Intent(context, AlarmService::class.java).apply {
//            putExtra("temperature", intent.getDoubleExtra("temperature", 0.0))
//            putExtra("description", intent.getStringExtra("description"))
//            putExtra("humidity", intent.getIntExtra("humidity", 0))
//            putExtra("currentTemperatureUnit", intent.getStringExtra("currentTemperatureUnit"))
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(serviceIntent)
//        }else{
//            context.startService(serviceIntent)
//        }
//
//    }
//}
