package com.example.skycast.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.database.FavDataBase.AppDatabase
import com.example.skycast.data.repo.AlarmRepoImp
import com.example.skycast.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted: Restoring alarms...")
            val alarmLocal = AlarmLocalDataSource(AppDatabase.getDatabase(context).alarmDao())
            val alarmRepo = AlarmRepoImp(alarmLocal)

            CoroutineScope(Dispatchers.IO).launch {
                alarmRepo.getAllAlarms().collect { alarms ->  // Collect from Flow
                    alarms.forEach { alarm ->
                        AlarmScheduler.scheduleAlarm(context, alarm) // Schedule each alarm
                    }
                    Log.d("BootReceiver", "Alarms restored: ${alarms.size} alarms set.")
                }
            }
        }
    }
}

