package com.example.skycast.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skycast.util.RestoreAlarmsWorker



class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted: Restoring alarms...")

            val workRequest = OneTimeWorkRequestBuilder<RestoreAlarmsWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
