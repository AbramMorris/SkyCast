package com.example.skycast.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.skycast.data.alarm.AlarmReceiver
import com.example.skycast.data.models.AlarmEntity
import java.util.*
import java.util.concurrent.TimeUnit


object AlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_label", alarm.label)
        }
        val uniqueId = "alarm_${alarm.id}"
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("AlarmScheduler", "Scheduling alarm for ID ${alarm.id}")
        Log.d("AlarmScheduler", "Latitude: ${alarm}")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )

        val delay = calendar.timeInMillis - System.currentTimeMillis()
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "alarm_label" to alarm.label
                )
            )
            .addTag(uniqueId)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueId,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
    fun cancelAlarm(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val uniqueId = "alarm_${alarm.id}"
        // Create the exact same PendingIntent used when scheduling the alarm
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Cancel AlarmManager alarm
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("AlarmScheduler", "AlarmManager canceled for ID ${alarm.id}")
        // Stop the service when the alarm is deleted
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "STOP_ALARM_SERVICE"
            Log.d("AlarmScheduler", "Stopping service for ID $alarm.id")
        }
        context.sendBroadcast(stopIntent)
        // Cancel WorkManager task (Make sure you set the tag when scheduling)
        deleteScheduledAlarm(context, alarm.id)
        Log.d("AlarmScheduler", "WorkManager task canceled for ID $alarm.id")
    }
    @SuppressLint("SuspiciousIndentation")
    fun deleteScheduledAlarm(context: Context, alarmId: Int ) {
        val workManager = WorkManager.getInstance(context)
        val uniqueId = "alarm_$alarmId"
            try {
                val workInfos = workManager.getWorkInfosByTag(uniqueId).get()

                if (workInfos.isEmpty()) {
                    Log.d("AlarmWorker", "No matching work found for tag: $uniqueId")
                }
                workInfos.forEach { workInfo ->
                    if (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                        workManager.cancelWorkById(workInfo.id)
                        Log.d("AlarmWorker", "Canceled alarm with ID: ${workInfo.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AlarmWorker", "Error fetching work info: ${e.message}")
            }

    }

}
